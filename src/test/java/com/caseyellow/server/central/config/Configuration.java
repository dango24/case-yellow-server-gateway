package com.caseyellow.server.central.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.caseyellow.server.central.domain.mail.EmailService;
import com.caseyellow.server.central.domain.statistics.StatisticsAnalyzer;
import com.caseyellow.server.central.persistence.statistics.repository.UserInfoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;

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
    public UserInfoRepository userStatisticsRepository() {
        UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);
        return userInfoRepository;
    }

    @Bean
    @Profile("dev")
    public StatisticsAnalyzer statisticsAnalyzer() {
        StatisticsAnalyzer statisticsAnalyzer = mock(StatisticsAnalyzer.class);
        return statisticsAnalyzer;
    }
}
