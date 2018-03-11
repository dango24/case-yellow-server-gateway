package com.caseyellow.server.central.domain.test.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Dan on 12/10/2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemInfo {

    private String operatingSystem;
    private String browser;
    private String publicIP;
    private String connection; // LAN / Wifi connection
    private String infrastructure;
    private String isp;
    private int speed;

    public SystemInfo(String operatingSystem, String browser, String publicIP, String connection) {
        this(operatingSystem, browser, publicIP, connection, null, null, -1);
    }
}
