package com.caseyellow.server.central.domain.metrics;

import com.caseyellow.server.central.persistence.file.dao.FileDownloadInfoDAO;
import com.caseyellow.server.central.persistence.metrics.AverageMetric;
import com.caseyellow.server.central.persistence.metrics.AverageMetricRepository;
import com.caseyellow.server.central.persistence.test.dao.ComparisonInfoDAO;
import com.caseyellow.server.central.persistence.test.dao.TestDAO;
import com.caseyellow.server.central.persistence.website.dao.AnalyzedState;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteDAO;
import com.timgroup.statsd.StatsDClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.caseyellow.server.central.common.Utils.calculateDownloadRateFromMbpsToKBps;

@Slf4j
@Service
public class MetricsServiceImpl implements MetricsService {

    private StatsDClient statsDClient;
    private AverageMetricRepository averageMetricRepository;

    @Autowired
    public MetricsServiceImpl(StatsDClient statsDClient, AverageMetricRepository averageMetricRepository) {
        this.statsDClient = statsDClient;
        this.averageMetricRepository = averageMetricRepository;
    }

    @Override
    public void addMetrics(TestDAO test) {
        executeTestMetrics(test, test.getUser());
        test.getComparisonInfoDAOTests().forEach(comparisonInfo -> executeSubTestsMetrics(comparisonInfo, test.getUser(), test.getSpeedTestWebsiteIdentifier()));
    }

    @Override
    public void executeSubTestsSpeedTestMetrics(SpeedTestWebSiteDAO speedTestWebSite) {
        String user = speedTestWebSite.getS3FileAddress().split("-")[0];
        String identifier = speedTestWebSite.getSpeedTestIdentifier();
        String testBucket = String.format("end-sub-test.%s.%s", user, identifier);

        executeSubTestSpeedTestMetrics(speedTestWebSite, testBucket);
    }

    private void executeTestMetrics(TestDAO test, String user) {
        String testBucket = String.format("end-test.%s.%s", user, test.getSpeedTestWebsiteIdentifier());
        long avg = test.getEndTime() - test.getStartTime();

        AverageMetric averageMetric = averageMetricRepository.updateAverageMetric(testBucket, avg);
        statsDClient.increment(testBucket);
        statsDClient.recordExecutionTime(String.format("%s.%s", testBucket, "time"), avg);
        statsDClient.recordGaugeValue(String.format("%s.%s", testBucket, "time.average"), averageMetric.getAvg());
    }

    private void executeSubTestsMetrics(ComparisonInfoDAO comparisonInfo, String user, String identifier) {
        String testBucket = String.format("end-sub-test.%s.%s", user, identifier);

        executeSubTestSpeedTestMetrics(comparisonInfo.getSpeedTestWebSiteDAO(), testBucket);
        executeSubTestFileDownloadInfoMetrics(comparisonInfo.getFileDownloadInfoDAO(), testBucket);
        executeSubTestRatio(comparisonInfo, testBucket);
    }

    public void executeSubTestSpeedTestMetrics(SpeedTestWebSiteDAO speedTestWebSite, String testBucket) {
        if (speedTestWebSite.getAnalyzedState() == AnalyzedState.SUCCESS) {

            double downloadRateInKB = calculateDownloadRateFromMbpsToKBps(speedTestWebSite.getDownloadRateInMbps());
            AverageMetric averageMetric = averageMetricRepository.updateAverageMetric(testBucket, downloadRateInKB);

            statsDClient.increment(testBucket);
            statsDClient.recordGaugeValue(String.format("%s.%s", testBucket, "download.rate"), downloadRateInKB);
            statsDClient.recordGaugeValue(String.format("%s.%s", testBucket, "download.rate.average"), averageMetric.getAvg());
        }
    }

    private void executeSubTestFileDownloadInfoMetrics(FileDownloadInfoDAO fileDownloadInfoDAO, String testBucket) {
        double downloadRateInKB = fileDownloadInfoDAO.getFileDownloadRateKBPerSec();

        if (downloadRateInKB > 0) {

            testBucket = String.format("%s.%s", testBucket, fileDownloadInfoDAO.getFileName());
            AverageMetric averageMetric = averageMetricRepository.updateAverageMetric(testBucket, downloadRateInKB);

            statsDClient.increment(testBucket);
            statsDClient.recordGaugeValue(String.format("%s.%s", testBucket, "download.rate"), downloadRateInKB);
            statsDClient.recordGaugeValue(String.format("%s.%s", testBucket, "download.rate.average"), averageMetric.getAvg());
        }
    }

    private void executeSubTestRatio(ComparisonInfoDAO comparisonInfo, String testBucket) {
        testBucket = String.format("%s.%s", testBucket, "ratio");
        double ratio = getRatio(comparisonInfo);

        AverageMetric averageMetric = averageMetricRepository.updateAverageMetric(testBucket, ratio);

        statsDClient.recordGaugeValue(testBucket, ratio);
        statsDClient.recordGaugeValue(String.format("%s.%s", testBucket, "average"), averageMetric.getAvg());
    }

    private double getRatio(ComparisonInfoDAO comparisonInfo){
        double speedTestRate = calculateDownloadRateFromMbpsToKBps(comparisonInfo.getSpeedTestWebSiteDAO().getDownloadRateInMbps());
        double downloadRate = comparisonInfo.getFileDownloadInfoDAO().getFileDownloadRateKBPerSec();
        double ratio = downloadRate / speedTestRate;

        return ratio;
    }
}
