package com.caseyellow.server.central.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

@Component
@ConfigurationProperties
public class UrlConfig {

    private Map<String, String> speedTestUrls;
    private Map<String, UrlProperty> fileDownloadUrls;

    public UrlConfig() {}

    @PostConstruct
    private void init() {
        System.out.println(fileDownloadUrls);
    }


    public Map<String, String> getSpeedTestUrls() {
        return speedTestUrls;
    }

    public void setSpeedTestUrls(Map<String, String> speedTestUrls) {
        this.speedTestUrls = speedTestUrls;
    }

    public Set<String> getFileDownloadIdentifiers() {
        return fileDownloadUrls.keySet();
    }

    public Set<String> getSpeedTestIdentifiers() {
        return speedTestUrls.keySet();
    }

    public String getFileDownload(String fileIdentifier) {
        return fileDownloadUrls.get(fileIdentifier).getUrl();
    }

    public Map<String, UrlProperty> getFileDownloadUrls() {
        return fileDownloadUrls;
    }

    public void setFileDownloadUrls(Map<String, UrlProperty> fileDownloadUrls) {
        this.fileDownloadUrls = fileDownloadUrls;
    }

    public boolean isValidSpeedTestIdentifier(String speedTestIdentifier) {
        return speedTestUrls.keySet().contains(speedTestIdentifier);
    }

    public boolean isValidFileDownloadIdentifier(String fileDownloadIdentifier) {
        return fileDownloadUrls.keySet().contains(fileDownloadIdentifier);
    }
}