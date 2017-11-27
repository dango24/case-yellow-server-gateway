package com.caseyellow.server.central.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@ConfigurationProperties
public class UrlMapper {

    private final static String DEFAULT_SPEED_TEST_URL = "http://www.hot.net.il/heb/Internet/speed/";
    private final static String DEFAULT_FILE_DOWNLOAD_URL = "https://storage.googleapis.com/golang/go1.7.1.windows-amd64.msi";

    private Map<String, String> speedTestUrls;
    private Map<String, String> fileDownloadUrls;
    private List<String> nonFlashIdentifiers;

    public UrlMapper() {
    }

    public List<String> getNonFlashIdentifiers() {
        return nonFlashIdentifiers;
    }

    public void setNonFlashIdentifiers(List<String> nonFlashIdentifiers) {
        this.nonFlashIdentifiers = nonFlashIdentifiers;
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

    public boolean isValidSpeedTestIdentifier(String speedTestIdentifier) {
        return speedTestUrls.keySet().contains(speedTestIdentifier);
    }

    public boolean isValidFileDownloadIdentifier(String fileDownloadIdentifier) {
        return fileDownloadUrls.keySet().contains(fileDownloadIdentifier);
    }

    public boolean isNonFlashAble(String identifier) {
        return nonFlashIdentifiers.contains(identifier);
    }
}
