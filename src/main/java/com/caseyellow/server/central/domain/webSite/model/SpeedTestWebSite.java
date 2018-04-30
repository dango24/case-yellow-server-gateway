package com.caseyellow.server.central.domain.webSite.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Dan on 12/10/2016.
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpeedTestWebSite {

    private boolean succeed;
    private String urlAddress;
    private String speedTestIdentifier;
    private String nonFlashResult;
    private String path;
    private long startMeasuringTimestamp;
    private Double downloadRateInMbps; // Mega bit per second
    private Double downloadRateInKBps; // Mega bit per second

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


}
