package com.caseyellow.server.central.domain.analyzer.services;

import com.caseyellow.server.central.domain.metrics.MetricsService;
import com.caseyellow.server.central.domain.test.services.TestService;
import com.caseyellow.server.central.persistence.website.dao.AnalyzedState;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteDAO;
import com.caseyellow.server.central.persistence.website.repository.SpeedTestWebSiteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Profile("prod")
public class ImageAnalyzerImpl implements ImageAnalyzer {

    @Value("${successful_tests_dir}")
    private String testsDir;

    private MetricsService metricsService;
    private SpeedTestWebSiteRepository speedTestWebSiteRepository;

    @Autowired
    public ImageAnalyzerImpl(MetricsService metricsService, SpeedTestWebSiteRepository speedTestWebSiteRepository) {
        this.metricsService = metricsService;
        this.speedTestWebSiteRepository = speedTestWebSiteRepository;
    }

    @Override
    @Transactional
    public void updateAnalyzedImageResult(String imagePath, double analyzedImageResult, boolean analyzedSucceed) {
        SpeedTestWebSiteDAO speedTestWebSiteDAO = speedTestWebSiteRepository.findByS3FileAddress(imagePath.replaceAll(testsDir, ""));
        speedTestWebSiteRepository.updateAnalyzedImageResultById(speedTestWebSiteDAO.getId(), analyzedImageResult);
        AnalyzedState analyzedState = analyzedSucceed ? AnalyzedState.SUCCESS : AnalyzedState.FAILURE;

        speedTestWebSiteRepository.updateAnalyzedState(speedTestWebSiteDAO.getId(), analyzedState);
        log.info(String.format("Successfully update speedTestWebSiteDAO: %s with result: %s", speedTestWebSiteDAO.getS3FileAddress(), analyzedImageResult));

        if (analyzedSucceed) {
            metricsService.executeSubTestsSpeedTestMetrics(speedTestWebSiteRepository.findOne(speedTestWebSiteDAO.getId()));
        }
    }
}
