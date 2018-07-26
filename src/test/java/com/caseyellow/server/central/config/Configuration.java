package com.caseyellow.server.central.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.caseyellow.server.central.domain.mail.EmailService;
import com.caseyellow.server.central.persistence.statistics.repository.UserStatisticsRepository;
import com.caseyellow.server.central.persistence.test.model.LastUserTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.List;

import static org.mockito.Mockito.mock;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    @Profile("dev")
    public EmailService devEmailService() {
        EmailService emailService = mock(EmailService.class);
        return emailService;
    }

    @Bean
    @Profile("dev")
    public AmazonDynamoDB amazonDynamoDB() {
        AmazonDynamoDB amazonDynamoDB = mock(AmazonDynamoDB.class);
        return amazonDynamoDB;
    }

    @Bean
    @Profile("dev")
    public UserStatisticsRepository userStatisticsRepository() {
        UserStatisticsRepository userStatisticsRepository = mock(UserStatisticsRepository.class);
        return userStatisticsRepository;
    }
}
