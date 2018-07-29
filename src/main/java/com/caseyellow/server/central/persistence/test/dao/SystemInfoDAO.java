package com.caseyellow.server.central.persistence.test.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

/**
 * Created by Dan on 12/10/2016.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class SystemInfoDAO {

    private String operatingSystem;
    private String browser;
    private String publicIP;
    private String connection; // LAN / Wifi connection

    @Transient
    private String infrastructure;

    @Transient
    private String isp;

    @Transient
    private int speed;

    public SystemInfoDAO(String operatingSystem, String browser, String publicIP, String connection) {
        this.operatingSystem = operatingSystem;
        this.browser = browser;
        this.publicIP = publicIP;
        this.connection = connection;
    }
}
