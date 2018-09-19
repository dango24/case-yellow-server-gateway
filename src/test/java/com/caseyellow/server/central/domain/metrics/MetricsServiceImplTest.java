package com.caseyellow.server.central.domain.metrics;

import com.caseyellow.server.central.CaseYellowCentral;
import com.caseyellow.server.central.persistence.file.dao.FileDownloadInfoDAO;
import com.caseyellow.server.central.persistence.metrics.MetricAverage;
import com.caseyellow.server.central.persistence.metrics.MetricAverageRepository;
import com.caseyellow.server.central.persistence.test.dao.ComparisonInfoDAO;
import com.caseyellow.server.central.persistence.test.dao.TestDAO;
import com.caseyellow.server.central.persistence.website.dao.AnalyzedState;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteDAO;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseYellowCentral.class)
@ActiveProfiles("dev")
public class MetricsServiceImplTest {

    @Autowired
    private MetricsService metricsService;

    @Autowired
    private MetricAverageRepository metricAverageRepository;

    @Test
    public void addMetrics() throws Exception {

        List<SpeedTestWebSiteDAO> speedTestWebSiteDAOList =
            Arrays.asList(0.0546875, 0.0625, 0.0703125) // Will be 7, 8, 9 KB 0.0546875
                  .stream()
                  .map(this::createSpeedTestWebSiteDAO)
                  .collect(Collectors.toList());

        List<FileDownloadInfoDAO> fileDownloadInfoDAOList =
                Arrays.asList(org.apache.commons.lang3.tuple.Pair.of("amazon-workSpaces", 7),
                              org.apache.commons.lang3.tuple.Pair.of("my-sql", 8),
                              org.apache.commons.lang3.tuple.Pair.of("vlc", 9))
                      .stream()
                      .map(this::createFileDownloadInfoDAO)
                      .collect(Collectors.toList());

        List<ComparisonInfoDAO> comparisonInfoDAOList =
            IntStream.range(0, 3)
                     .mapToObj(index -> new ComparisonInfoDAO(speedTestWebSiteDAOList.get(index), fileDownloadInfoDAOList.get(index)))
                     .collect(Collectors.toList());

        TestDAO test = new TestDAO.TestBuilder("mock_test")
                                  .addUser("Amazon01")
                                  .addEndTime(1000L)
                                  .addStartTime(500L)
                                  .addSpeedTestWebsite("hot")
                                  .addComparisonInfoTests(comparisonInfoDAOList)
                                  .build();

        metricsService.addMetrics(test);

        MetricAverage endSubTestMetricAverage = metricAverageRepository.findByBucket("end-sub-test.Amazon01.hot");
        MetricAverage endSubTestRatioMetricAverage = metricAverageRepository.findByBucket("end-sub-test.Amazon01.hot.ratio");
        assertEquals(8.0, endSubTestMetricAverage.getAvg(), 0.1);
        assertEquals(1.0, endSubTestRatioMetricAverage.getAvg(), 0.1);
    }

    private SpeedTestWebSiteDAO createSpeedTestWebSiteDAO(double Mbps) {
        SpeedTestWebSiteDAO speedTestWebSiteDAO = new SpeedTestWebSiteDAO();
        speedTestWebSiteDAO.setSpeedTestIdentifier("hot");
        speedTestWebSiteDAO.setAnalyzedState(AnalyzedState.SUCCESS);
        speedTestWebSiteDAO.setDownloadRateInMbps(Mbps);

        return speedTestWebSiteDAO;
    }

    private FileDownloadInfoDAO createFileDownloadInfoDAO(org.apache.commons.lang3.tuple.Pair pair) {
        FileDownloadInfoDAO fileDownloadInfoDAO = new FileDownloadInfoDAO(String.valueOf(pair.getLeft()), null);
        fileDownloadInfoDAO.setFileDownloadRateKBPerSec((int)(pair.getRight()));

        return fileDownloadInfoDAO;
    }

}