package com.caseyellow.server.central.domain.test.services;

import com.caseyellow.server.central.common.DAOConverter;
import com.caseyellow.server.central.domain.test.model.ComparisonInfo;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.exceptions.SaveTestException;
import com.caseyellow.server.central.persistence.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;

@Service
public class TestServiceImpl implements TestService {

    private TestRepository testRepository;

    @Autowired
    public TestServiceImpl(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @Override
    public void saveTest(Test test) {
        CompletableFuture.supplyAsync(() -> test)
                         .exceptionally(this::handleSaveTestException)
                         .thenApply(this::removeUnsuccessfulTests)
                         .thenApply(DAOConverter::convertTestToTestDAO)
                         .thenAccept(testRepository::save);
    }

    private Test handleSaveTestException(Throwable throwable) throws SaveTestException {
        throw new SaveTestException(throwable.getMessage());
    }

    private Test removeUnsuccessfulTests(Test test) {
        List<ComparisonInfo> comparisonInfoSucceed = test.getComparisonInfoTests()
                                                         .stream()
                                                         .filter(comparisonInfo -> comparisonInfo.getSpeedTestWebSite().isSucceed())
                                                         .collect(toList());

        List<ComparisonInfo> comparisonInfoFailures = test.getComparisonInfoTests()
                                                          .stream()
                                                          .filter(comparisonInfo -> !comparisonInfo.getSpeedTestWebSite().isSucceed())
                                                          .collect(toList());

        notifyComparisonInfoFailures(comparisonInfoFailures);
        test.setComparisonInfoTests(comparisonInfoSucceed);

        return test;
    }

    private void notifyComparisonInfoFailures(List<ComparisonInfo> comparisonInfoFailures) {
        CompletableFuture.supplyAsync(() -> comparisonInfoFailures)
                         .thenAccept(this::notifyFailedTests);
    }

    private void notifyFailedTests(List<ComparisonInfo> comparisonInfoFailures) {
        // TODO dango handle failed tests
    }
}
