package com.caseyellow.server.central.persistence.metrics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;

import static java.util.Objects.isNull;

public interface MetricAverageRepository extends JpaRepository<MetricAverage, Long> {

    DecimalFormat AVERAGE_DECIMAL_FORMAT = new DecimalFormat("#.##");

    String UPDATE_COUNTER_QUERY = "UPDATE MetricAverage a set a.count = a.count+1 where a.id = :id";
    String UPDATE_AVERAGE_QUERY = "UPDATE MetricAverage a set a.avg = :avg where a.id = :id";

    @Modifying
    @Transactional
    @Query(UPDATE_COUNTER_QUERY)
    void updateCounter(@Param("id")long id);

    @Modifying
    @Transactional
    @Query(UPDATE_AVERAGE_QUERY)
    void updateAverage(@Param("id")long id, @Param("avg")double avg);

    MetricAverage findByBucket(String bucketName);

    default MetricAverage updateAverageMetric(String bucketName, double testAvgTime) {
        MetricAverage metricAverage = findByBucket(bucketName);

        if (isNull(metricAverage)) {
            metricAverage = new MetricAverage(bucketName, testAvgTime, 1);
            save(metricAverage);

            return metricAverage;
        }

        double currentAvg = metricAverage.getAvg();
        int currentCount = metricAverage.getCount();
        double newAvg = (currentAvg*currentCount + testAvgTime) / (currentCount+1);
        newAvg = Double.valueOf(AVERAGE_DECIMAL_FORMAT.format(newAvg));

        updateAverage(metricAverage.getId(), newAvg);
        updateCounter(metricAverage.getId());

        return new MetricAverage(bucketName, newAvg);
    }
}
