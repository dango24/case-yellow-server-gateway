package com.caseyellow.server.central.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@Configuration
@ConfigurationProperties(locations = {"classpath:urls.yml"})
public class UrlMapper {

    private final static String DEFAULT_SPEED_TEST_URL = "http://www.hot.net.il/heb/Internet/speed/";
    private final static String DEFAULT_FILE_DOWNLOAD_URL = "https://storage.googleapis.com/golang/go1.7.1.windows-amd64.msi";

    private Map<String, String> speedTestUrls;
    private Map<String, String> fileDownloadUrls;

    public UrlMapper() {
    }

    public Map<String, String> getSpeedTestUrls() {
        return speedTestUrls;
    }

    public void setSpeedTestUrls(Map<String, String> speedTestUrls) {
        this.speedTestUrls = speedTestUrls;
    }

    public Map<String, String> getFileDownloadUrls() {
        return fileDownloadUrls;
    }

    public Set<String> getFileDownloadIdentifiers() {
        return fileDownloadUrls.keySet();
    }

    public Set<String> getSpeedTestIdentifiers() {
        return speedTestUrls.keySet();
    }

    public void setFileDownloadUrls(Map<String, String> fileDownloadUrls) {
        this.fileDownloadUrls = fileDownloadUrls;
    }

    public String getSpeedTest(String speedTestIdentifier) {
        return speedTestUrls.getOrDefault(speedTestIdentifier, DEFAULT_SPEED_TEST_URL);
    }

    public String getFileDownload(String fileIdentifier) {
        return fileDownloadUrls.getOrDefault(fileIdentifier, DEFAULT_FILE_DOWNLOAD_URL);
    }
}
