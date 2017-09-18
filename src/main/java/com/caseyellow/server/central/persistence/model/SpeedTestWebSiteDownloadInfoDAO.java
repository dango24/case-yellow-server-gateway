package com.caseyellow.server.central.persistence.model;

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

    private boolean isAnalyzed;
    private String urlAddress;
    private String speedTestIdentifier;
    private String S3FileAddress;
    private double downloadRateInMbps; // Mega bit per second
    private long startMeasuringTimestamp;

    public SpeedTestWebSiteDownloadInfoDAO() {}


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

    public String getS3FileAddress() {
        return S3FileAddress;
    }

    public void setS3FileAddress(String s3FileAddress) {
        this.S3FileAddress = s3FileAddress;
    }

    public double getDownloadRateInMbps() {
        return downloadRateInMbps;
    }

    public void setDownloadRateInMbps(double downloadRateInMbps) {
        this.downloadRateInMbps = downloadRateInMbps;
    }

    public boolean isAnalyzed() {
        return isAnalyzed;
    }

    public void setAnalyzed(boolean analyzed) {
        isAnalyzed = analyzed;
    }
}
