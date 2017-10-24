package com.caseyellow.server.central.persistence.website.dao;

import javax.persistence.*;

/**
 * Created by Dan on 12/10/2016.
 */
@Entity
@Table(name = "speed_test_web_site")
public class SpeedTestWebSiteDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int key;
    private boolean analyzed;
    private String urlAddress;
    private String speedTestIdentifier;
    private String S3FileAddress;
    private double downloadRateInMbps; // Mega bit per second
    private long startMeasuringTimestamp;

    public SpeedTestWebSiteDAO() {
        analyzed = false;
    }

    public SpeedTestWebSiteDAO(String speedTestIdentifier) {
        this(speedTestIdentifier, null);
    }

    public SpeedTestWebSiteDAO(String speedTestIdentifier, String urlAddress) {
        this(speedTestIdentifier, urlAddress, false);
    }

    public SpeedTestWebSiteDAO(String speedTestIdentifier, String urlAddress, boolean analyzed) {
        this.setSpeedTestIdentifier(speedTestIdentifier);
        this.setUrlAddress(urlAddress);
        this.analyzed = analyzed;
        this.downloadRateInMbps = -1;
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
        return analyzed;
    }

    public void setAnalyzed(boolean analyzed) {
        this.analyzed = analyzed;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "SpeedTestWebSiteDAO{" +
                "analyzed=" + analyzed +
                ", urlAddress='" + urlAddress + '\'' +
                ", speedTestIdentifier='" + speedTestIdentifier + '\'' +
                ", downloadRateInMbps=" + downloadRateInMbps +
                '}';
    }
}
