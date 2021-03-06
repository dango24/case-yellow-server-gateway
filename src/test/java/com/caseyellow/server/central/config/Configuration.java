package com.caseyellow.server.central.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.caseyellow.server.central.domain.analyzer.services.ImageAnalyzer;
import com.caseyellow.server.central.domain.logger.LoggerService;
import com.caseyellow.server.central.domain.mail.EmailService;
import com.caseyellow.server.central.domain.statistics.StatisticsAnalyzer;
import com.caseyellow.server.central.persistence.statistics.repository.UserInfoRepository;
import com.caseyellow.server.central.persistence.test.repository.TestLifeCycleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

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

    @Bean
    @Profile("dev")
    public ImageAnalyzer imageAnalyzer() {
        ImageAnalyzer statisticsAnalyzer = mock(ImageAnalyzer.class);
        return statisticsAnalyzer;
    }

    @Bean
    @Profile("dev")
    public TestLifeCycleRepository testLifeCycleRepository() {
        TestLifeCycleRepository testLifeCycleRepository = mock(TestLifeCycleRepository.class);
        return testLifeCycleRepository;
    }

    @Bean
    @Profile("dev")
    public LoggerService loggerService() {
        LoggerService loggerService = mock(LoggerService.class);
        return loggerService;
    }
}
