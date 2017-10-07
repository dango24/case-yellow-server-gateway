package com.caseyellow.server.central.domain.webSite.model;

/**
 * Created by Dan on 12/10/2016.
 */
public class SpeedTestWebSite {

    private int key;
    private boolean succeed;
    private String urlAddress;
    private String speedTestIdentifier;
    private long startMeasuringTimestamp;

    public SpeedTestWebSite() {
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

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
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
