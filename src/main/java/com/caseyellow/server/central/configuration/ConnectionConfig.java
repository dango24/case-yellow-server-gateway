package com.caseyellow.server.central.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@Component
@NoArgsConstructor
@ConfigurationProperties(prefix = "connection-details")
public class ConnectionConfig {

    private List<String> speed;
    private List<String> infrastructure;

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
}
