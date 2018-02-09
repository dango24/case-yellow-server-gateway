package com.caseyellow.server.central.domain.test.services;

import com.caseyellow.server.central.common.Converter;
import com.caseyellow.server.central.common.Validator;
import com.caseyellow.server.central.configuration.ConnectionConfig;
import com.caseyellow.server.central.domain.counter.CounterService;
import com.caseyellow.server.central.domain.test.model.*;
import com.caseyellow.server.central.persistence.test.dao.TestDAO;
import com.caseyellow.server.central.persistence.test.dao.UserDetailsDAO;
import com.caseyellow.server.central.persistence.test.repository.FailedTestRepository;
import com.caseyellow.server.central.persistence.test.repository.TestRepository;
import com.caseyellow.server.central.persistence.test.repository.UserDetailsRepository;
import com.caseyellow.server.central.services.storage.FileStorageService;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.caseyellow.server.central.common.Validator.validateTest;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

@Service
public class TestServiceImpl implements TestService {

    private Logger logger = Logger.getLogger(TestServiceImpl.class);

    private CounterService counterService;
    private ConnectionConfig connectionConfig;
    private TestRepository testRepository;
    private FailedTestRepository failedTestRepository;
    private UserDetailsRepository userDetailsRepository;
    private FileStorageService fileStorageService;

    @Autowired
    public TestServiceImpl(TestRepository testRepository,
                           CounterService counterService,
                           ConnectionConfig connectionConfig,
                           FileStorageService fileStorageService,
                           FailedTestRepository failedTestRepository,
                           UserDetailsRepository userDetailsRepository) {

        this.testRepository = testRepository;
        this.counterService = counterService;
        this.connectionConfig = connectionConfig;
        this.fileStorageService = fileStorageService;
        this.failedTestRepository = failedTestRepository;
        this.userDetailsRepository = userDetailsRepository;
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

    public List<TestDAO> getAllDAOTests() {
        return testRepository.findAll();
    }

    @Override
    public Map<String, List<String>> getConnectionDetails() {
        return connectionConfig.getAllConnectionDetails();
    }

    @Override
    public PreSignedUrl generatePreSignedUrl(String userIP, String fileName) {
        return fileStorageService.generatePreSignedUrl(userIP, fileName);
    }

    @Override
    public void saveUserDetails(UserDetails userDetails) {
        UserDetailsDAO userDetailsDAO = Converter.convertUserDetailsModelToDAO(userDetails);
        userDetailsRepository.save(userDetailsDAO);
    }

    @Override
    public void failedTest(FailedTestDetails failedTestDetails) {
        failedTestRepository.save(Converter.convertFailedTestModelToDAO(failedTestDetails));
    }

    @Override
    @CacheEvict(value = { "tests", "countIPs", "identifiersDetails" }, allEntries = true)
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

    @Override
    public boolean isUserExist(String userName) {
        UserDetailsDAO userDetailsDAO = userDetailsRepository.findByUserName(userName);

        return nonNull(userDetailsDAO);
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