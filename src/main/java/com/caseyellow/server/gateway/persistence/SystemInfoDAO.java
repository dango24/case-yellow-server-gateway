package com.caseyellow.server.gateway.persistence;

import javax.persistence.Embeddable;

/**
 * Created by Dan on 12/10/2016.
 */

@Embeddable
public class SystemInfoDAO {

    private String operatingSystem;
    private String browser;
    private String publicIP;
    private String connection; // LAN / Wifi connection

    public SystemInfoDAO() {}

    public SystemInfoDAO(String operatingSystem, String browser, String publicIP, String connection) {
        this.operatingSystem = operatingSystem;
        this.browser = browser;
        this.publicIP = publicIP;
        this.connection = connection;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getBrowser() {
        return browser;
    }

    public String getPublicIP() {
        return publicIP;
    }

    public String getConnection() {
        return connection;
    }

    @Override
    public String toString() {
        return "SystemInfoDAO{" +
                "operatingSystem='" + operatingSystem + '\'' +
                ", browser='" + browser + '\'' +
                ", publicIP='" + publicIP + '\'' +
                ", connection='" + connection + '\'' +
                '}';
    }
}
