package com.caseyellow.server.central.domain.test.services;

import com.caseyellow.server.central.common.Converter;
import com.caseyellow.server.central.common.Validator;
import com.caseyellow.server.central.domain.counter.CounterService;
import com.caseyellow.server.central.domain.test.model.*;
import com.caseyellow.server.central.persistence.test.dao.TestDAO;
import com.caseyellow.server.central.persistence.test.repository.FailedTestRepository;
import com.caseyellow.server.central.persistence.test.repository.TestRepository;
import com.caseyellow.server.central.services.storage.FileStorageService;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.caseyellow.server.central.common.Validator.validateTest;
import static java.util.stream.Collectors.toList;

@Service
public class TestServiceImpl implements TestService {

    private Logger logger = Logger.getLogger(TestServiceImpl.class);

    private TestRepository testRepository;
    private CounterService counterService;
    private FailedTestRepository failedTestRepository;
    private FileStorageService fileStorageService;

    @Autowired
    public TestServiceImpl(TestRepository testRepository,
                           CounterService counterService,
                           FileStorageService fileStorageService,
                           FailedTestRepository failedTestRepository) {

        this.testRepository = testRepository;
        this.counterService = counterService;
        this.fileStorageService = fileStorageService;
        this.failedTestRepository = failedTestRepository;
    }

    @Override
    @Cacheable("tests")
    public List<Test> getAllTests() {
        return testRepository.findAll()
                             .stream()
                             .map(Converter::convertTestDAOToModel)
                             .filter(Validator::isSuccessfulTest)
                             .collect(toList());
    }

    @Override
    public PreSignedUrl generatePreSignedUrl(String userIP, String fileName) {
        return fileStorageService.generatePreSignedUrl(userIP, fileName);
    }

    @Override
    public void failedTest(FailedTestDetails failedTestDetails) {
        failedTestRepository.save(Converter.convertFailedTestModelToDAO(failedTestDetails));
    }

    @Override
    @CacheEvict(value = "tests", allEntries = true)
    public void saveTest(Test test) {
        if (!validateTest(test)) {
           throw new IllegalArgumentException("Test is not valid, test: " + new Gson().toJson(test));
        }

        CompletableFuture.supplyAsync(() -> test)
                         .thenApply(this::removeUnsuccessfulTests)
                         .thenApply(this::increaseComparisonInfoCounters)
                         .thenApply(Converter::convertTestModelToDAO)
                         .exceptionally(this::handleSaveTestException)
                         .thenAccept(this::save);
    }

    private Test removeUnsuccessfulTests(Test test) {

        List<ComparisonInfo> comparisonInfoSucceed = test.getComparisonInfoTests()
                                                         .stream()
                                                         .filter(comparisonInfo -> comparisonInfo.getSpeedTestWebSite().isSucceed())
                                                         .collect(toList());

        test.setComparisonInfoTests(comparisonInfoSucceed);

        return test;
    }

    private void save(TestDAO testDAO) {
        try {
            testRepository.save(testDAO);

        } catch (Exception e) {
            logger.error("Failed to save test, " + e.getMessage(), e);
            decreaseComparisonInfoCounters(testDAO);
        }
    }

    private Test increaseComparisonInfoCounters(Test test) {

        test.getComparisonInfoTests()
            .stream()
            .map(ComparisonInfoIdentifiers::new)
            .forEach(identifiers -> counterService.addComparisionInfoDetails(identifiers.getSpeedTestIdentifier(), identifiers.getFileDownloadIdentifier()));

        return test;
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
}