package com.caseyellow.server.central.domain.analyzer.services;

import com.caseyellow.server.central.common.Utils;
import com.caseyellow.server.central.domain.analyzer.model.ImageDetails;
import com.caseyellow.server.central.exceptions.AnalyzerException;
import com.caseyellow.server.central.persistence.website.dao.AnalyzedState;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteDAO;
import com.caseyellow.server.central.persistence.website.repository.SpeedTestWebSiteRepository;
import com.caseyellow.server.central.queues.MessageProducerService;
import com.caseyellow.server.central.queues.MessageType;
import com.caseyellow.server.central.services.storage.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Service
@Slf4j
@Profile("prod")
public class ImageAnalyzerImpl implements ImageAnalyzer {

    @Value("${successful_tests_dir}")
    private String testsDir;

    private FileStorageService fileStorageService;
    private MessageProducerService messageProducerService;
    private SpeedTestWebSiteRepository speedTestWebSiteRepository;

    @Autowired
    public ImageAnalyzerImpl(MessageProducerService messageProducerService, SpeedTestWebSiteRepository speedTestWebSiteRepository, FileStorageService fileStorageService) {
        this.fileStorageService  =fileStorageService;
        this.messageProducerService = messageProducerService;
        this.speedTestWebSiteRepository = speedTestWebSiteRepository;
    }

    @Override
    @Transactional
    public void updateAnalyzedImageResult(String imagePath, double analyzedImageResult, boolean analyzedSucceed) {
        SpeedTestWebSiteDAO speedTestWebSiteDAO = speedTestWebSiteRepository.findByS3FileAddress(imagePath.replaceAll(testsDir, ""));

        if (isNull(speedTestWebSiteDAO)) {
            log.error(String.format("Failed to find speedTestWebSite record for s3 path: %s", imagePath.replaceAll(testsDir, "")));
            return;
        }

        speedTestWebSiteRepository.updateAnalyzedImageResultById(speedTestWebSiteDAO.getId(), analyzedImageResult);
        AnalyzedState analyzedState = analyzedSucceed ? AnalyzedState.SUCCESS : AnalyzedState.FAILURE;

        speedTestWebSiteRepository.updateAnalyzedState(speedTestWebSiteDAO.getId(), analyzedState);
        log.info(String.format("Successfully update speedTestWebSiteDAO: %s with result: %s", speedTestWebSiteDAO.getS3FileAddress(), analyzedImageResult));
    }

    @Override
    public void checkUnAnalyzedTests(int periodInDays, int analyzedStateCode) {
        if (periodInDays < 0) {
            periodInDays = 0;
        }

        AnalyzedState analyzedState = AnalyzedState.getAnalyzedState(analyzedStateCode);

        int periodInHours = Math.toIntExact(TimeUnit.DAYS.toHours(periodInDays));

        List<SpeedTestWebSiteDAO> unAnalyzedSpeedTests =
                speedTestWebSiteRepository.findByAnalyzedState(analyzedState)
                                          .stream()
                                          .filter(speedTest -> testNotAnalyzedOverThreshold(speedTest, periodInHours))
                                          .collect(Collectors.toList());

        unAnalyzedSpeedTests.stream()
                            .map(SpeedTestWebSiteDAO::getS3FileAddress)
                            .map(this::buildImagePathDetails)
                            .forEach(imageDetails -> messageProducerService.send(MessageType.IMAGE_ANALYSIS, imageDetails));
    }

    @Override
    public void changeImageAnalyzeState(String s3Path, int analyzedStateCode) {
        File imagesPathFile = fileStorageService.getFile(s3Path);

        try (Stream<String> stream = Files.lines(Paths.get(imagesPathFile.getAbsolutePath()))) {

            stream.map(imagePath -> speedTestWebSiteRepository.findByS3FileAddress(imagePath))
                  .filter(Objects::nonNull)
                  .map(SpeedTestWebSiteDAO::getId)
                  .peek(speedTestId -> log.info(String.format("Updating Speed test with id: %s", speedTestId)))
                  .forEach(speedTestId -> updateSpeedTestState(speedTestId, analyzedStateCode));

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AnalyzerException(e.getMessage());

        } finally {
            Utils.deleteFile(imagesPathFile);
        }
    }

    private void updateSpeedTestState(long speedTestId, int analyzedStateCode) {
        try {
            speedTestWebSiteRepository.updateAnalyzedState(speedTestId, AnalyzedState.getAnalyzedState(analyzedStateCode));
            log.info(String.format("Successfully Updated Speed test with id: %s", speedTestId));

        } catch (Exception e) {
            log.error("Failed to update speedTest with id: %s to state: %s", speedTestId, analyzedStateCode);
        }
    }

    private boolean testNotAnalyzedOverThreshold(SpeedTestWebSiteDAO speedTestWebSite, int periodInHours) {
        return TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - speedTestWebSite.getStartMeasuringTimestamp()) >= periodInHours;
    }

    private ImageDetails buildImagePathDetails(String path) {
        String[] pathArgs = path.split("-");

        if (pathArgs.length != 4) { // Total numbers of valid arguments
            String errorMessage = String.format("Failed to generate ImageDetails, path args is not in length 4 for path: %s", path);
            log.error(errorMessage);
            throw new AnalyzerException(errorMessage);
        }

        String user = pathArgs[0];
        String md5 = pathArgs[2];
        String identifier = getIdentifierFromPathArgs(pathArgs);

        return new ImageDetails(path, user, identifier, md5);
    }

    private String getIdentifierFromPathArgs(String[] pathArgs) {
        String identifier = pathArgs[pathArgs.length -1];

        return identifier.replaceAll(".png", "")
                         .replaceAll(".PNG", "");
    }
}
