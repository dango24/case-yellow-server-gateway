package com.caseyellow.server.central.persistence.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class AverageMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String bucket;
    private double avg;
    private int count;

    public AverageMetric(String bucket, double avg) {
        this(bucket, avg, -1);
    }

    public AverageMetric(String bucket, double avg, int count) {
        this.bucket = bucket;
        this.avg = avg;
        this.count = count;
    }
}
