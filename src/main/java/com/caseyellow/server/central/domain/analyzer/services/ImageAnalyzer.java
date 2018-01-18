package com.caseyellow.server.central.domain.analyzer.services;

import com.caseyellow.server.central.domain.analyzer.model.GoogleVisionRequest;
import com.caseyellow.server.central.persistence.website.dao.AnalyzedState;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteDAO;
import com.caseyellow.server.central.persistence.website.repository.SpeedTestWebSiteRepository;
import com.caseyellow.server.central.services.analyze.ImageAnalyzerService;
import com.caseyellow.server.central.services.storage.FileStorageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class ImageAnalyzer {

    private Logger logger = Logger.getLogger(ImageAnalyzer.class);

    private static final int SCHEDULED_TASK_INTERVAL = 120_000; // 2 minutes
    private static final int INITIAL_SCHEDULED_TASK = 120_000; // 2 minutes

    private FileStorageService fileStorageService;
    private ImageAnalyzerService imageAnalyzerService;
    private SpeedTestWebSiteRepository speedTestWebSiteRepository;

    @Autowired
    public ImageAnalyzer(FileStorageService fileStorageService, ImageAnalyzerService imageAnalyzerService, SpeedTestWebSiteRepository speedTestWebSiteRepository) {
        this.fileStorageService = fileStorageService;
        this.imageAnalyzerService = imageAnalyzerService;
        this.speedTestWebSiteRepository = speedTestWebSiteRepository;
    }

    @Scheduled(fixedDelay = SCHEDULED_TASK_INTERVAL, initialDelay = INITIAL_SCHEDULED_TASK)
    public void analyzeImageScheduler() {
        List<SpeedTestWebSiteDAO> speedTestWebSiteNonAnalyzed = speedTestWebSiteRepository.findByAnalyzedState(AnalyzedState.NOT_STARTED);
        speedTestWebSiteNonAnalyzed.forEach(this::analyzeImage);
    }

    private void analyzeImage(SpeedTestWebSiteDAO speedTestWebSiteDAO) {
        try {
            logger.info("Start analyzing image: " + speedTestWebSiteDAO.getS3FileAddress());
            File imageSnapshot = fileStorageService.getFile(speedTestWebSiteDAO.getS3FileAddress());
            GoogleVisionRequest googleVisionRequest = new GoogleVisionRequest(imageSnapshot.getAbsolutePath());
            double analyzedImageResult = imageAnalyzerService.analyzeImage(speedTestWebSiteDAO.getSpeedTestIdentifier(), googleVisionRequest);
            speedTestWebSiteRepository.updateAnalyzedImageResult(speedTestWebSiteDAO.getId(), analyzedImageResult);
            speedTestWebSiteRepository.updateAnalyzedState(speedTestWebSiteDAO.getId(), AnalyzedState.SUCCESS);

        } catch (Exception e) {
            speedTestWebSiteRepository.updateAnalyzedState(speedTestWebSiteDAO.getId(), AnalyzedState.FAILURE);
            logger.error("AnalyzeImage failed for image: " + speedTestWebSiteDAO.getS3FileAddress() + " cause: " + e.getMessage(), e);
        }
    }
}
