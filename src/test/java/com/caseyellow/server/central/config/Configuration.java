package com.caseyellow.server.central.config;

import com.caseyellow.server.central.domain.mail.EmailService;
import com.caseyellow.server.central.domain.metrics.MetricsService;
import com.caseyellow.server.central.persistence.test.dao.TestDAO;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteDAO;
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

    @Bean
    @Profile("dev")
    public MetricsService metricsService() {
        return new MetricsService() {
            @Override
            public void addMetrics(TestDAO test) {

            }

            @Override
            public void executeSubTestsSpeedTestMetrics(SpeedTestWebSiteDAO speedTestWebSite) {

            }
        };
    }
}
