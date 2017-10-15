package com.caseyellow.server.central.domain.test.services;

import com.caseyellow.server.central.common.DAOConverter;
import com.caseyellow.server.central.domain.test.model.ComparisonInfo;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.test.model.TestWrapper;
import com.caseyellow.server.central.persistence.model.TestDAO;
import com.caseyellow.server.central.persistence.repository.TestRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Service
public class TestServiceImpl implements TestService {

    private Logger logger = Logger.getLogger(TestServiceImpl.class);

    private TestRepository testRepository;

    @Autowired
    public TestServiceImpl(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @Override
    public void saveTest(TestWrapper test) {
        CompletableFuture.supplyAsync(() -> test)
                         .thenApply(this::removeUnsuccessfulTests)
                         .thenApply(this::uploadToS3)
                         .thenApply(DAOConverter::convertTestToTestDAO)
                         .exceptionally(this::handleSaveTestException)
                         .thenAccept(testRepository::save);
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

    private Test uploadToS3(TestWrapper testWrapper) {

        System.out.println(
                testWrapper.getSnapshotLocalLocation()
                           .values()
                           .stream()
                           .map(file -> new StringBuilder(String.valueOf(System.currentTimeMillis())).reverse().toString() + file.getName())
                           .collect(joining(", "))
        );


        return testWrapper.getTest();
    }

    private void notifyComparisonInfoFailures(List<ComparisonInfo> comparisonInfoFailures) {
        CompletableFuture.supplyAsync(() -> comparisonInfoFailures)
                         .thenAccept(this::notifyFailedTests);
    }

    private TestDAO handleSaveTestException(Throwable throwable) {
        logger.error("Failed To save test, " + throwable.getMessage(), throwable);
        return null;
    }

    private void notifyFailedTests(List<ComparisonInfo> comparisonInfoFailures) {
        // TODO dango handle failed tests
    }
}
