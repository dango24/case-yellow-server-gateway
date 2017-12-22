package com.caseyellow.server.central.domain.webSite.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Dan on 12/10/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpeedTestWebSite {

    private boolean succeed;
    private String urlAddress;
    private String speedTestIdentifier;
    private String nonFlashResult;
    private String path;
    private long startMeasuringTimestamp;
    private Double downloadRateInMbps; // Mega bit per second
    private Double downloadRateInKBps; // Mega bit per second

    public SpeedTestWebSite() {
    }

    public SpeedTestWebSite(String speedTestIdentifier) {
        this.speedTestIdentifier = speedTestIdentifier;
        this.succeed = true;
    }

    public SpeedTestWebSite(boolean succeed) {
        this.succeed = succeed;
    }

    public SpeedTestWebSite(String urlAddress, String speedTestIdentifier, long startMeasuringTimestamp) {
        this(false, urlAddress, speedTestIdentifier, startMeasuringTimestamp);
    }

    public SpeedTestWebSite(boolean succeed, String urlAddress, String speedTestIdentifier, long startMeasuringTimestamp) {
        this.succeed = succeed;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Double getDownloadRateInMbps() {
        return downloadRateInMbps;
    }

    public void setDownloadRateInMbps(Double downloadRateInMbps) {
        this.downloadRateInMbps = downloadRateInMbps;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public String getNonFlashResult() {
        return nonFlashResult;
    }

    public void setNonFlashResult(String nonFlashResult) {
        this.nonFlashResult = nonFlashResult;
    }

    public Double getDownloadRateInKBps() {
        return downloadRateInKBps;
    }

    public void setDownloadRateInKBps(Double downloadRateInKBps) {
        this.downloadRateInKBps = downloadRateInKBps;
    }

    @Override
    public String toString() {
        return "SpeedTestWebSite{" +
                "succeed=" + succeed +
                ", urlAddress='" + urlAddress + '\'' +
                ", speedTestIdentifier='" + speedTestIdentifier + '\'' +
                ", startMeasuringTimestamp=" + startMeasuringTimestamp +
                '}';
    }
}
