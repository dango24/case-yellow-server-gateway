package com.caseyellow.server.central.domain.analyzer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.caseyellow.server.central.persistence.metrics.MetricAverageRepository.AVERAGE_DECIMAL_FORMAT;
import static java.util.Objects.nonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDownloadRateInfo {

    private String user;
    private double actualRate;
    private int expectedRate;
    private String infra;

    @Override
    public String toString() {
        String infaStr = nonNull(infra) ? infra : "UNKNOWN";

        return String.format("{actualRate = %s Mbps, expectedRate = %s Mbps, infra: %s}",
                             AVERAGE_DECIMAL_FORMAT.format(actualRate),
                             expectedRate,
                             infaStr);
    }
}
