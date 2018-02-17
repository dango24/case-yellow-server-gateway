package com.caseyellow.server.central.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@ConfigurationProperties(prefix = "connection-details")
public class ConnectionConfig {

    private List<String> speed;
    private List<String> infrastructure;

    public ConnectionConfig() {
    }

    public ConnectionConfig(List<String> speed, List<String> infrastructure) {
        this.speed = speed;
        this.infrastructure = infrastructure;
    }

    public Map<String, List<String>> getAllConnectionDetails() {
        Map<String, List<String>> allConnectionDetails = new HashMap<>();
        allConnectionDetails.put("speed", speed);
        allConnectionDetails.put("infrastructure", infrastructure);

        return allConnectionDetails;
    }

    public List<String> getSpeed() {
        return speed;
    }

    public void setSpeed(List<String> speed) {
        this.speed = speed;
    }

    public List<String> getInfrastructure() {
        return infrastructure;
    }

    public void setInfrastructure(List<String> infrastructure) {
        this.infrastructure = infrastructure;
    }
}
