package com.caseyellow.server.central.config;

import com.caseyellow.server.central.domain.mail.EmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    @Profile("dev")
    public EmailService devEmailService() {
        return new EmailService() {
            @Override
            public void sendNotification() {

            }
        };
    }
}
