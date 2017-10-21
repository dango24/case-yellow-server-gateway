package com.caseyellow.server.central.domain.test.model;

import com.caseyellow.server.central.persistence.test.dao.ComparisonInfoDAO;

public class ComparisonInfoIdentifiers {

    private String speedTestIdentifier;
    private String fileDownloadIdentifier;

    public ComparisonInfoIdentifiers() {
    }

    public ComparisonInfoIdentifiers(ComparisonInfoDAO comparisonInfo) {
       this(comparisonInfo.getSpeedTestWebSiteDAO().getSpeedTestIdentifier(), comparisonInfo.getFileDownloadInfoDAO().getFileName());
    }

    public ComparisonInfoIdentifiers(String speedTestIdentifier, String fileDownloadIdentifier) {
        this.speedTestIdentifier = speedTestIdentifier;
        this.fileDownloadIdentifier = fileDownloadIdentifier;
    }

    public String getSpeedTestIdentifier() {
        return speedTestIdentifier;
    }

    public void setSpeedTestIdentifier(String speedTestIdentifier) {
        this.speedTestIdentifier = speedTestIdentifier;
    }

    public String getFileDownloadIdentifier() {
        return fileDownloadIdentifier;
    }

    public void setFileDownloadIdentifier(String fileDownloadIdentifier) {
        this.fileDownloadIdentifier = fileDownloadIdentifier;
    }
}

