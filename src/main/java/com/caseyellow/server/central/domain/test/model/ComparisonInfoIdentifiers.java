package com.caseyellow.server.central.domain.test.model;

import com.caseyellow.server.central.persistence.test.dao.ComparisonInfoDAO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonInfoIdentifiers {

    private String speedTestIdentifier;
    private String fileDownloadIdentifier;

    public ComparisonInfoIdentifiers(ComparisonInfo comparisonInfo) {
       this(comparisonInfo.getSpeedTestWebSite().getSpeedTestIdentifier(), comparisonInfo.getFileDownloadInfo().getFileName());
    }

    public ComparisonInfoIdentifiers(ComparisonInfoDAO comparisonInfo) {
        this(comparisonInfo.getSpeedTestWebSiteDAO().getSpeedTestIdentifier(), comparisonInfo.getFileDownloadInfoDAO().getFileName());
    }



}

