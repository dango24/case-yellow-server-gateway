package com.caseyellow.server.central.domain.mail;

import com.caseyellow.server.central.domain.test.services.TestService;
import com.caseyellow.server.central.persistence.test.model.LastUserTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@Profile("prod")
@ConfigurationProperties
public class EmailServiceImpl implements EmailService {

    private static final String EMAIL_SUBJECT = "Case Yellow missing in action";
    public static SimpleDateFormat DATE_FORMAT;

    static {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
    }

    private JavaMailSender emailSender;
    private TestService testService;
    private List<String> emails;

    @Autowired
    public EmailServiceImpl(TestService testService, JavaMailSender emailSender) {
        this.emailSender = emailSender;
        this.testService = testService;
    }

    @Override
    public void sendNotification() {
        List<LastUserTest> lastUserTests =
            testService.lastUserTests()
                        .stream()
                        .filter(this::isLastTestOverOneDay)
                        .collect(Collectors.toList());

        String mailBody = buildMailBody(lastUserTests);

        log.info(String.format("Send mail to: %s with body: %s", emails, mailBody));
        emails.forEach(email -> sendSimpleMessage(email, EMAIL_SUBJECT, mailBody));
    }

    private boolean isLastTestOverOneDay(LastUserTest lastUserTest) {
        return (System.currentTimeMillis() - lastUserTest.getTimestamp()) > TimeUnit.DAYS.toMillis(1);
    }

    private String buildMailBody(List<LastUserTest> lastUserTests) {

        return lastUserTests.stream()
                            .map(LastUserTest::toString)
                            .collect(Collectors.joining("\n\n"));
    }

    private void sendSimpleMessage(String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }
}
