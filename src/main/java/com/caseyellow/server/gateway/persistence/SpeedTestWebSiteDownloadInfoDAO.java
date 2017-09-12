package com.caseyellow.server.gateway.persistence;

import javax.persistence.*;

/**
 * Created by Dan on 12/10/2016.
 */
@Entity
@Table(name = "speed_test_webSite_download_info")
public class SpeedTestWebSiteDownloadInfoDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String urlAddress;
    private String speedTestIdentifier;
    private long startMeasuringTimestamp;

    public SpeedTestWebSiteDownloadInfoDAO() {}

    public SpeedTestWebSiteDownloadInfoDAO(String urlAddress, String speedTestIdentifier, long startMeasuringTimestamp) {
        this.urlAddress = urlAddress;
        this.speedTestIdentifier = speedTestIdentifier;
        this.startMeasuringTimestamp = startMeasuringTimestamp;
    }

    public String getUrlAddress() {
        return urlAddress;
    }

    public void setUrlAddress(String urlAddress) {
        this.urlAddress = urlAddress;
    }

    public String getSpeedTestIdentifier() {
        return speedTestIdentifier;
    }

    public void setSpeedTestIdentifier(String speedTestIdentifier) {
        this.speedTestIdentifier = speedTestIdentifier;
    }

    public long getStartMeasuringTimestamp() {
        return startMeasuringTimestamp;
    }

    public void setStartMeasuringTimestamp(long startMeasuringTimestamp) {
        this.startMeasuringTimestamp = startMeasuringTimestamp;
    }
}
