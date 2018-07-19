package com.caseyellow.server.central.configuration;

import com.caseyellow.server.central.domain.file.model.FileDownloadProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

@Slf4j
@Data
@Component
@NoArgsConstructor
@ConfigurationProperties
public class UrlConfig {

    private Map<String, String> speedTestUrls;
    private Map<String, FileDownloadProperties> fileDownloadProperties;

    @PostConstruct
    private void init() {
        log.info(String.format("FileDownloadProperties are: %s", fileDownloadProperties.toString()));
        log.info(String.format("SpeedTestUrls are: %s", speedTestUrls.toString()));
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

    public boolean isValidSpeedTestIdentifier(String speedTestIdentifier) {
        return speedTestUrls.keySet().contains(speedTestIdentifier);
    }

    public boolean isValidFileDownloadIdentifier(String fileDownloadIdentifier) {
        return fileDownloadProperties.keySet().contains(fileDownloadIdentifier);
    }
}