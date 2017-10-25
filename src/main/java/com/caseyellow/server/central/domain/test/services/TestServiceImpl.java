package com.caseyellow.server.central.domain.test.services;

import com.caseyellow.server.central.common.Converter;
import com.caseyellow.server.central.domain.counter.CounterService;
import com.caseyellow.server.central.domain.analyzer.nonflash.NonFlashAnalyzerService;
import com.caseyellow.server.central.domain.test.model.ComparisonInfo;
import com.caseyellow.server.central.domain.test.model.ComparisonInfoIdentifiers;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.test.model.TestWrapper;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestWebSite;
import com.caseyellow.server.central.persistence.test.dao.ComparisonInfoDAO;
import com.caseyellow.server.central.persistence.test.dao.TestDAO;
import com.caseyellow.server.central.persistence.test.repository.TestRepository;
import com.caseyellow.server.central.services.storage.FileStorageService;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.caseyellow.server.central.common.Validator.validateTest;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class TestServiceImpl implements TestService {

    private Logger logger = Logger.getLogger(TestServiceImpl.class);

    private TestRepository testRepository;
    private CounterService counterService;
    private FileStorageService fileUploadService;
    private NonFlashAnalyzerService nonFlashAnalyzerService;

    @Autowired
    public TestServiceImpl(NonFlashAnalyzerService nonFlashAnalyzerService, TestRepository testRepository, CounterService counterService, FileStorageService fileUploadService) {
        this.nonFlashAnalyzerService = nonFlashAnalyzerService;
        this.testRepository = testRepository;
        this.counterService = counterService;
        this.fileUploadService = fileUploadService;
    }

    @Override
    public List<Test> getAllTests() {
        return testRepository.findAll()
                             .stream()
                             .map(Converter::convertTestDAOToModel)
                             .collect(toList());
    }

    @Override
    public void saveTest(TestWrapper test) {
        if (!validateTest(test.getTest())) {
           throw new IllegalArgumentException("Test is not valid, test: " + new Gson().toJson(test.getTest()));
        }

        increaseComparisonInfoCounters(test.getTest());

        CompletableFuture.supplyAsync(() -> test)
                         .thenApply(this::removeUnsuccessfulTests)
                         .thenApply(this::analyzeNonFlashResult)
                         .thenApply(this::uploadSnapshots)
                         .exceptionally(this::handleSaveTestException)
                         .thenAccept(this::save);
    }

    private TestWrapper removeUnsuccessfulTests(TestWrapper testWrapper) {
        Test test = testWrapper.getTest();

        List<ComparisonInfo> comparisonInfoSucceed = test.getComparisonInfoTests()
                                                         .stream()
                                                         .filter(comparisonInfo -> comparisonInfo.getSpeedTestWebSite().isSucceed())
                                                         .collect(toList());

        List<ComparisonInfo> comparisonInfoFailures = test.getComparisonInfoTests()
                                                          .stream()
                                                          .filter(comparisonInfo -> !comparisonInfo.getSpeedTestWebSite().isSucceed())
                                                          .collect(toList());

        test.setComparisonInfoTests(comparisonInfoSucceed);
        notifyComparisonInfoFailures(comparisonInfoFailures);

        return testWrapper;
    }

    private TestWrapper analyzeNonFlashResult(TestWrapper testWrapper) {

        testWrapper.getTest()
                   .getComparisonInfoTests()
                   .stream()
                   .map(ComparisonInfo::getSpeedTestWebSite)
                   .filter(speedTest -> nonFlashAnalyzerService.isNonFlashAble(speedTest.getSpeedTestIdentifier()))
                   .forEach(this::updateNonFlashSpeedTestResult);

        return testWrapper;
    }

    private void updateNonFlashSpeedTestResult(SpeedTestWebSite speedTestWebSite) {
        double downloadRateInMbps = nonFlashAnalyzerService.analyze(speedTestWebSite.getSpeedTestIdentifier(), speedTestWebSite.getNonFlashResult());
        speedTestWebSite.setDownloadRateInMbps(downloadRateInMbps);
    }

    private TestDAO uploadSnapshots(TestWrapper testWrapper) {
        String userIP = testWrapper.getTest().getSystemInfo().getPublicIP();
        TestDAO testDAO = Converter.convertTestModelToDAO(testWrapper.getTest());

        Map<Integer, String> fileLocation =
            testWrapper.getSnapshotLocalLocation()
                       .entrySet()
                       .stream()
                       .map(snapshot -> uploadFile(userIP, snapshot))
                       .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        testDAO.getComparisonInfoDAOTests()
               .stream()
               .map(ComparisonInfoDAO::getSpeedTestWebSiteDAO)
               .forEach(speedTestWebSite -> speedTestWebSite.setS3FileAddress(fileLocation.get(speedTestWebSite.getKey())));

        return testDAO;
    }

    private Map.Entry<Integer, String> uploadFile(String userIP, Map.Entry<String, File> snapshot) {
        int fileKey = Integer.valueOf(snapshot.getKey());
        String snapshotLocation = fileUploadService.uploadFile(userIP, snapshot.getValue());

        return new AbstractMap.SimpleEntry<>(fileKey, snapshotLocation);
    }

    private void save(TestDAO testDAO) {
        try {
            testRepository.save(testDAO);

        } catch (Exception e) {
            logger.error("Failed to save test, " + e.getMessage(), e);
            decreaseComparisonInfoCounters(testDAO);
        }
    }

    private void increaseComparisonInfoCounters(Test test) {

        test.getComparisonInfoTests()
                .stream()
                .map(ComparisonInfoIdentifiers::new)
                .forEach(identifiers -> counterService.addComparisionInfoDetails(identifiers.getSpeedTestIdentifier(), identifiers.getFileDownloadIdentifier()));
    }

    private TestDAO decreaseComparisonInfoCounters(TestDAO test) {

        test.getComparisonInfoDAOTests()
                .stream()
                .map(ComparisonInfoIdentifiers::new)
                .forEach(identifiers -> counterService.decreaseComparisionInfoDetails(identifiers.getSpeedTestIdentifier(), identifiers.getFileDownloadIdentifier()));

        return test;
    }

    private TestDAO handleSaveTestException(Throwable throwable) {
        logger.error("Failed To save test, " + throwable.getMessage(), throwable);
        return null;
    }

    private void notifyComparisonInfoFailures(List<ComparisonInfo> comparisonInfoFailures) {
        CompletableFuture.supplyAsync(() -> comparisonInfoFailures)
                .thenAccept(this::notifyFailedTests);
    }

    private void notifyFailedTests(List<ComparisonInfo> comparisonInfoFailures) {
        // TODO dango handle failed tests
    }
}


