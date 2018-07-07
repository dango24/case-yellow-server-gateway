package com.caseyellow.server.central.domain.mail;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.caseyellow.server.central.configuration.AWSConfiguration;
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

import static java.util.stream.Collectors.toMap;

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
    private List<EmailUser> emails;

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
        if (CollectionUtils.isEmpty(lastUserTests)) {
            return;
        }

        String subject = String.format("%s - %s", MISSING_IN_ACTION_EMAIL_SUBJECT, DAY_FORMAT.format(new Date()));
        String mailBody = buildMailBody(lastUserTests);
        log.info(String.format("Send email to: %s with body: %s", emails, mailBody));

        emails.forEach(email -> sendMessage(email.getEmail(), subject, mailBody));
    }

    @Override
    public void sendImageSanity(String imageSanityPath) {
        String subject = String.format("%s - %s", SANITY_EMAIL_SUBJECT, DAY_FORMAT.format(new Date()));

        emails.stream()
              .filter(user -> user.getRole().equals("admin"))
              .forEach(user -> sendMessage(user.getEmail(), subject, imageSanityPath));
    }

    private String buildMailBody(List<LastUserTest> lastUserTests) {

        return lastUserTests.stream()
                            .map(LastUserTest::toString)
                            .collect(Collectors.joining("\n\n"));
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
}
