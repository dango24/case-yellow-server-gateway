package com.caseyellow.server.central.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Configuration
@ConfigurationProperties(locations = {"classpath:urls.yml"})
public class UrlMapper {

    private final static String DEFAULT_URL = "http://www.hot.net.il/heb/Internet/speed/";

    private Map<String, String> urls;

    public UrlMapper() {
    }

    public Map<String, String> getUrls() {
        return urls;
    }

    public void setUrls(Map<String, String> urls) {
        this.urls = urls;
    }

    public String get(String identifier) {
        return urls.getOrDefault(identifier, DEFAULT_URL);
    }

    @Override
    public String toString() {
        return "UrlMapper{" +
                "urls=" + urls +
                '}';
    }
}
