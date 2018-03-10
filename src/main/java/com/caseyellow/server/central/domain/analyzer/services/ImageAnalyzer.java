package com.caseyellow.server.central.domain.analyzer.services;

import com.caseyellow.server.central.common.Utils;
import com.caseyellow.server.central.domain.analyzer.model.GoogleVisionRequest;
import com.caseyellow.server.central.persistence.website.dao.AnalyzedState;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteDAO;
import com.caseyellow.server.central.persistence.website.repository.SpeedTestWebSiteRepository;
import com.caseyellow.server.central.services.analyze.ImageAnalyzerService;
import com.caseyellow.server.central.services.storage.FileStorageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

import static com.caseyellow.server.central.common.Utils.convertToMD5;

@Service
@Profile("prod")
public class ImageAnalyzer {

    private Logger logger = Logger.getLogger(ImageAnalyzer.class);

    private static final int SCHEDULED_TASK_INTERVAL = 120_000; // 2 minutes
    private static final int INITIAL_SCHEDULED_TASK = 120_000; // 2 minutes

    @Value("${successful_tests_dir}")
    private String testsDir;

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
        File imageSnapshot = null;
        logger.info("Start analyzing image: " + speedTestWebSiteDAO.getS3FileAddress());

        try {
            String s3Path = getS3Path(speedTestWebSiteDAO.getS3FileAddress());
            imageSnapshot = fileStorageService.getFile(s3Path);
            GoogleVisionRequest googleVisionRequest = new GoogleVisionRequest(imageSnapshot.getAbsolutePath(), convertToMD5(imageSnapshot));
            double analyzedImageResult = imageAnalyzerService.analyzeImage(speedTestWebSiteDAO.getSpeedTestIdentifier(), googleVisionRequest);
            speedTestWebSiteRepository.updateAnalyzedImageResult(speedTestWebSiteDAO.getId(), analyzedImageResult);
            speedTestWebSiteRepository.updateAnalyzedState(speedTestWebSiteDAO.getId(), AnalyzedState.SUCCESS);

        } catch (Exception e) {
            speedTestWebSiteRepository.updateAnalyzedState(speedTestWebSiteDAO.getId(), AnalyzedState.FAILURE);
            logger.error("AnalyzeImage failed for image: " + speedTestWebSiteDAO.getS3FileAddress() + " cause: " + e.getMessage(), e);
        } finally {
            Utils.deleteFile(imageSnapshot);
        }
    }

    private String getS3Path(String s3FileAddress) {
        if (s3FileAddress.startsWith(testsDir)) {
            return s3FileAddress;
        } else {
            return testsDir + s3FileAddress;
        }
    }
}
