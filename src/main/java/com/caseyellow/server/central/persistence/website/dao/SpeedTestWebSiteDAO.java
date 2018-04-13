package com.caseyellow.server.central.persistence.website.dao;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Dan on 12/10/2016.
 */
@Data
@Entity
@Table(name = "speed_test_web_site")
public class SpeedTestWebSiteDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String urlAddress;
    private String speedTestIdentifier;
    private String s3FileAddress;
    private double downloadRateInMbps; // Mega   bit per second
    private long startMeasuringTimestamp;
    private AnalyzedState analyzedState;

    public SpeedTestWebSiteDAO() {
        this(null);
    }

    public SpeedTestWebSiteDAO(String speedTestIdentifier) {
        this(speedTestIdentifier, null);
    }

    public SpeedTestWebSiteDAO(String speedTestIdentifier, String urlAddress) {
        this(speedTestIdentifier, urlAddress, AnalyzedState.NOT_STARTED);
    }

    public SpeedTestWebSiteDAO(String speedTestIdentifier, String urlAddress, AnalyzedState analyzedState) {
        this.setSpeedTestIdentifier(speedTestIdentifier);
        this.setUrlAddress(urlAddress);
        this.downloadRateInMbps = -1;
        this.analyzedState = analyzedState;
    }
}
