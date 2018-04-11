package com.caseyellow.server.central.configuration;

import com.caseyellow.server.central.domain.file.model.FileDownloadProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@ConfigurationProperties
public class UrlConfig {

    private Map<String, String> speedTestUrls;
    private Map<String, FileDownloadProperties> fileDownloadProperties;

    public UrlConfig() {}

    @PostConstruct
    private void init() {
        log.info(String.format("FileDownloadProperties are: %s", fileDownloadProperties.toString()));
        log.info(String.format("SpeedTestUrls are: %s", speedTestUrls.toString()));
    }

    public Map<String, String> getSpeedTestUrls() {
        return speedTestUrls;
    }

    public void setSpeedTestUrls(Map<String, String> speedTestUrls) {
        this.speedTestUrls = speedTestUrls;
    }

    public Set<String> getFileDownloadIdentifiers() {
        return fileDownloadProperties.keySet();
    }

    public Set<String> getSpeedTestIdentifiers() {
        return speedTestUrls.keySet();
    }

    public String getFileDownload(String fileIdentifier) {
        return fileDownloadProperties.get(fileIdentifier).getUrl();
    }

    public FileDownloadProperties getFileDownloadProperties(String identifier) {
        return fileDownloadProperties.get(identifier);
    }

    public Map<String, FileDownloadProperties> getFileDownloadProperties() {
        return fileDownloadProperties;
    }

    public void setFileDownloadProperties(Map<String, FileDownloadProperties> fileDownloadProperties) {
        this.fileDownloadProperties = fileDownloadProperties;
    }

    public boolean isValidSpeedTestIdentifier(String speedTestIdentifier) {
        return speedTestUrls.keySet().contains(speedTestIdentifier);
    }

    public boolean isValidFileDownloadIdentifier(String fileDownloadIdentifier) {
        return fileDownloadProperties.keySet().contains(fileDownloadIdentifier);
    }
}