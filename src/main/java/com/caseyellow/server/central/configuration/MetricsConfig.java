package com.caseyellow.server.central.configuration;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Value("${metrics.statsd.host:18.197.16.236}")
    private String host;

    @Value("${metrics.statsd.port:8125}")
    private int port;

    @Value("${metrics.prefix:servile}")
    private String prefix;

    @Bean
    public StatsDClient statsDClient() {
        return new NonBlockingStatsDClient(prefix, host, port);
    }
}
