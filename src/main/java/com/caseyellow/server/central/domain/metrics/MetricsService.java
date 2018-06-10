package com.caseyellow.server.central.domain.metrics;

import com.caseyellow.server.central.persistence.test.dao.TestDAO;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteDAO;

public interface MetricsService {
    void addMetrics(TestDAO test);
    void executeSubTestsSpeedTestMetrics(SpeedTestWebSiteDAO speedTestWebSite);
}
