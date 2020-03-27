package com.caseyellow.server.central.domain.mail;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.caseyellow.server.central.configuration.AWSConfiguration;
import com.caseyellow.server.central.domain.statistics.UserTestsStats;
import com.caseyellow.server.central.domain.test.services.TestService;
import com.caseyellow.server.central.persistence.test.model.LastUserTest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@Profile("prod")
@ConfigurationProperties
public class EmailServiceImpl implements EmailService {

    private static final String MISSING_IN_ACTION_EMAIL_SUBJECT = "Case Yellow missing in action";
    private static final String SANITY_EMAIL_SUBJECT = "Case Yellow Image Sanity";
    public static SimpleDateFormat DATE_FORMAT;
    public static SimpleDateFormat DAY_FORMAT;
    public static SimpleDateFormat LIGHT_DATE_FORMAT;

    static {
        DATE_FORMAT = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        LIGHT_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
        DAY_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
        DAY_FORMAT.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
        LIGHT_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
    }

    @Value("${email.username}")
    private String emailUser;

    @Getter @Setter
    private List<EmailUser> emailUsers;

    private TestService testService;
    private AmazonSimpleEmailService emailService;

    @Autowired
    public EmailServiceImpl(TestService testService, AWSConfiguration awsConfiguration) {
        this.testService = testService;
        initSES(awsConfiguration);
    }

    private void initSES(AWSConfiguration awsConfiguration) {
        AWSCredentials credentials =
                new BasicAWSCredentials(awsConfiguration.accessKeyID(), awsConfiguration.secretAccessKey());

        this.emailService =
                AmazonSimpleEmailServiceClientBuilder.standard()
                                                     .withRegion(Regions.EU_WEST_1)
                                                     .withCredentials(new AWSStaticCredentialsProvider(credentials))
                                                     .build();
    }

    @Override
    public void sendEmails(List<LastUserTest> lastUserTests) {
        emailUsers.stream()
                .filter(emailUser -> emailUser.getRole().equals("admin") || emailUser.getRole().equals("spotMaster"))
                .forEach(emailUser -> sendEmails(emailUser, lastUserTests));
    }

    public void sendEmails(EmailUser emailUser, List<LastUserTest> lastUserTests) {

        String subject = String.format("%s - %s", MISSING_IN_ACTION_EMAIL_SUBJECT, DAY_FORMAT.format(new Date()));
        String mailBody;

        if (CollectionUtils.isEmpty(lastUserTests)) {
            mailBody = "Everything is running smoothly, you can go to a party...";
        } else {
            mailBody = buildLastUserTestsMailBody(emailUser, lastUserTests);
        }

        log.info(String.format("Send email to: %s with body: %s", emailUsers, mailBody));

        sendMessage(emailUser.getEmail(), subject, mailBody);
    }

    @Override
    public void sendImageSanity(String imageSanityPath) {
        String subject = String.format("%s - %s", SANITY_EMAIL_SUBJECT, DAY_FORMAT.format(new Date()));

        emailUsers.stream()
                  .filter(user -> user.getRole().equals("admin"))
                  .forEach(user -> sendMessage(user.getEmail(), subject, imageSanityPath));
    }

    @Override
    public void sendUsersDoneTests(List<UserTestsStats> doneUsers, List<UserTestsStats> activeRunningUsers) {
        emailUsers.stream()
                  .filter(emailUser -> emailUser.getRole().equals("admin") || emailUser.getRole().equals("spotMaster"))
                  .forEach(emailUser -> sendUsersDoneTestEmail(emailUser, doneUsers, activeRunningUsers));
    }

    private void sendUsersDoneTestEmail(EmailUser emailUser, List<UserTestsStats> doneUsers, List<UserTestsStats> activeRunningUsers) {
        String mailBody;
        String subject = String.format("%s - %s", "Users Tests Status", DAY_FORMAT.format(new Date()));


        String activeUsersMessage = buildActiveRunningUsersMailBody(emailUser, activeRunningUsers);
        String doneUsersMessage = buildDoneUsersMailBody(emailUser, doneUsers);

        activeUsersMessage = addTabPrefix(activeUsersMessage);
        doneUsersMessage = addTabPrefix(doneUsersMessage);

        String message = activeUsersMessage + "\n\n";
        message += doneUsersMessage;

        mailBody = message;

        sendMessage(emailUser.getEmail(), subject, mailBody);
    }

    private String buildLastUserTestsMailBody(EmailUser emailUser,List<LastUserTest> lastUserTests) {

        return lastUserTests.stream()
                            .filter(lastUserTest -> emailUser.getRole().equals("admin") || isSpotMasterUser(emailUser, lastUserTest))
                            .map(LastUserTest::toString)
                            .collect(Collectors.joining("\n\n"));
    }

    private String buildActiveRunningUsersMailBody(EmailUser emailUser, List<UserTestsStats> activeRunningUsers) {
        StringBuilder stringBuilder = new StringBuilder();

        List<UserTestsStats> emailActiveRunningUsers =
                activeRunningUsers.stream()
                                  .filter(userTestsStats -> emailUser.getRole().equals("admin") || isSpotMasterUser(emailUser, userTestsStats))
                                  .collect(Collectors.toList());

        stringBuilder.append(String.format("Active users: %d\n\n", emailActiveRunningUsers.size()));

        emailActiveRunningUsers.stream()
                               .map(this::ActiveRunningUserMessage)
                               .forEach(stringBuilder::append);

        return stringBuilder.toString();
    }

    private boolean isSpotMasterUser(EmailUser emailUser, LastUserTest lastUserTest) {
        return emailUser.getRole().equals("spotMaster") &&
               nonNull(lastUserTest.getSpotMasterReferral()) &&
               lastUserTest.getSpotMasterReferral().equals(emailUser.getName());
    }

    private boolean isSpotMasterUser(EmailUser emailUser, UserTestsStats userTestsStats) {
        return emailUser.getRole().equals("spotMaster") &&
               nonNull(userTestsStats.getSpotMasterReferral()) &&
               userTestsStats.getSpotMasterReferral().equals(emailUser.getName());
    }

    private String buildDoneUsersMailBody(EmailUser emailUser, List<UserTestsStats> doneUsers) {
        StringBuilder stringBuilder = new StringBuilder();

        List<UserTestsStats> emailDoneUsers =
                doneUsers.stream()
                        .filter(doneUser -> emailUser.getRole().equals("admin") || isSpotMasterUser(emailUser, doneUser))
                        .collect(Collectors.toList());

        stringBuilder.append(String.format("Done users: %d\n\n\t", emailDoneUsers.size()));

        stringBuilder.append(
                emailDoneUsers.stream()
                              .map(UserTestsStats::getName)
                              .collect(Collectors.joining("\n\t")));

        return stringBuilder.toString();
    }


    private void sendMessage(String to, String subject, String text) {

        Message message =
                new Message().withBody(new Body().withText(new Content().withCharset("UTF-8").withData(text)))
                             .withSubject(new Content().withCharset("UTF-8").withData(subject));

        SendEmailRequest request =
                new SendEmailRequest().withDestination(new Destination().withToAddresses(to))
                                      .withMessage(message)
                                      .withSource(emailUser);

        emailService.sendEmail(request);
    }

    private String ActiveRunningUserMessage(UserTestsStats userTestsStats) {
        return String.format("\t%s: LAN: %d, Wifi: %d\n", userTestsStats.getName(), userTestsStats.getLanCount(), userTestsStats.getWifiCount());
    }

    private String addTabPrefix(String str) {
        return Stream.of(str.split("\n"))
                     .map(messageLine -> "\t" + messageLine)
                     .collect(Collectors.joining("\n"));
    }
}
