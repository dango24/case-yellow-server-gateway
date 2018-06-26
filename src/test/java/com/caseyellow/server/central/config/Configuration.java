package com.caseyellow.server.central.config;

import com.caseyellow.server.central.domain.mail.EmailService;
import com.caseyellow.server.central.domain.mail.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.List;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    @Profile("dev")
    public EmailService devEmailService() {
        return new EmailService() {
            @Override
            public void sendNotification(List<User> users) {

            }
        };
    }
}
