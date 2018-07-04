package com.caseyellow.server.central.domain.analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.caseyellow.server.central.persistence.metrics.MetricAverageRepository.AVERAGE_DECIMAL_FORMAT;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDownloadRateInfo {

    private String user;
    private double actualRate;
    private int expectedRate;

    @Override
    public String toString() {
        return "{" +
                "actualRate = " + AVERAGE_DECIMAL_FORMAT.format(actualRate) +
                " Mbps, expectedRate = " + expectedRate +
                " Mbps}";
    }
}
