package com.caseyellow.server.central.domain.test.services;

import com.caseyellow.server.central.common.Converter;
import com.caseyellow.server.central.common.CounterService;
import com.caseyellow.server.central.domain.test.model.ComparisonInfo;
import com.caseyellow.server.central.domain.test.model.ComparisonInfoIdentifiers;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.test.model.TestWrapper;
import com.caseyellow.server.central.persistence.test.dao.TestDAO;
import com.caseyellow.server.central.persistence.test.repository.TestRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Service
public class TestServiceImpl implements TestService {

    private Logger logger = Logger.getLogger(TestServiceImpl.class);

    private TestRepository testRepository;
    private CounterService counterService;

    @Autowired
    public TestServiceImpl(TestRepository testRepository, CounterService counterService) {
        this.testRepository = testRepository;
        this.counterService = counterService;
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
        CompletableFuture.supplyAsync(() -> test)
                         .thenApply(this::removeUnsuccessfulTests)
                         .thenApply(this::uploadSnapshot)
                         .thenApply(Converter::convertTestModelToDAO)
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

    private Test uploadSnapshot(TestWrapper testWrapper) {

        logger.info(
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

    private void save(TestDAO test) {
        if (isNull(test)) {

        }

        test.getComparisonInfoDAOTests()
            .stream()
            .map(ComparisonInfoIdentifiers::new)
            .forEach(identifiers -> counterService.addComparisionInfoDetails(identifiers.getSpeedTestIdentifier(), identifiers.getFileDownloadIdentifier()));

        testRepository.save(test);
    }
}


