package com.caseyellow.server.central.persistence.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class MetricAverage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String bucket;
    private double avg;
    private int count;

    public MetricAverage(String bucket, double avg) {
        this(bucket, avg, -1);
    }

    public MetricAverage(String bucket, double avg, int count) {
        this.bucket = bucket;
        this.avg = avg;
        this.count = count;
    }
}
