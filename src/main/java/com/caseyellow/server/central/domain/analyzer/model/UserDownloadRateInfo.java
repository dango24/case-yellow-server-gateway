package com.caseyellow.server.central.domain.analyzer.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.caseyellow.server.central.persistence.metrics.MetricAverageRepository.AVERAGE_DECIMAL_FORMAT;
import static java.util.Objects.nonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBDocument
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDownloadRateInfo {

    private String user;
    private double actualRate;
    private int expectedRate;
    private String infra;
    private int testCount;

    private String generateTestCountStr() {
        String subTestCountStr = String.valueOf(testCount * 3);

        if (subTestCountStr.length() <= 3 ) {
            return subTestCountStr;
        }

        String reverse = new StringBuilder(subTestCountStr).reverse().toString();
        String reverseResult = reverse.substring(0, 3) + "," + reverse.substring(3);

        return new StringBuilder(reverseResult).reverse().toString();
    }

    @Override
    public String toString() {
        String infaStr = nonNull(infra) ? infra : "UNKNOWN";

        return String.format("{actualRate = %s Mbps, expectedRate = %s Mbps, infra: %s, total number of sub-tests: %s}",
                             AVERAGE_DECIMAL_FORMAT.format(actualRate),
                             expectedRate,
                             infaStr,
                             generateTestCountStr());
    }
}
