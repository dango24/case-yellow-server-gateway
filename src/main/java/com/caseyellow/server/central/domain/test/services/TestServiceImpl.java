package com.caseyellow.server.central.domain.test.services;

import com.caseyellow.server.central.common.Converter;
import com.caseyellow.server.central.common.Validator;
import com.caseyellow.server.central.configuration.ConnectionConfig;
import com.caseyellow.server.central.domain.counter.CounterService;
import com.caseyellow.server.central.domain.test.model.*;
import com.caseyellow.server.central.persistence.test.dao.FailedTestDAO;
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
import java.util.function.Function;

import static com.caseyellow.server.central.common.Validator.validateTest;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class TestServiceImpl implements TestService {

    private Logger logger = Logger.getLogger(TestServiceImpl.class);

    private ConnectionConfig connectionConfig;
    private CounterService counterService;
    private FileStorageService fileStorageService;
    private TestRepository testRepository;
    private FailedTestRepository failedTestRepository;
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    public TestServiceImpl(ConnectionConfig connectionConfig,
                           CounterService counterService,
                           FileStorageService fileStorageService,
                           TestRepository testRepository,
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
        List<TestDAO> tests = testRepository.findAll();
        return convertToTestModel(tests);
    }

    @Override
    public List<Test> getAllTestsByUser(String user) {
        List<TestDAO> userTests = testRepository.findByUser(user);
        return convertToTestModel(userTests);
    }

    private List<Test> convertToTestModel(List<TestDAO> testsDao) {

        List<Test> tests =
                testsDao.stream()
                        .map(Converter::convertTestDAOToModel)
                        .filter(Validator::isSuccessfulTest)
                        .collect(toList());

        Map<String, UserDetailsDAO> userDetails =
                userDetailsRepository.findAll()
                        .stream()
                        .collect(toMap(UserDetailsDAO::getUserName, Function.identity()));

        tests.forEach(test -> insertUserDetails(test.getSystemInfo(), userDetails.get(test.getUser())));

        return tests;
    }

    @Override
    public List<FailedTest> getAllUserFailedTests(String user) {

        return failedTestRepository.findByUser(user)
                                   .stream()
                                   .map(Converter::convertFailedTestDAOToModel)
                                   .collect(toList());
    }

    @Override
    public Map<String, List<String>> getConnectionDetails() {
        return connectionConfig.getAllConnectionDetails();
    }

    @Override
    public PreSignedUrl generatePreSignedUrl(String fileKey) {
        return fileStorageService.generatePreSignedUrl(fileKey);
    }

    @Override
    public void saveUserDetails(UserDetails userDetails) {
        UserDetailsDAO userDetailsDAO = Converter.convertUserDetailsModelToDAO(userDetails);
        userDetailsRepository.save(userDetailsDAO);
    }

    @Override
    public long userLastTest(String user) {
        return testRepository.lastUserFailedTest(user);
    }

    @Override
    public long userLastFailedTest(String user) {
        return failedTestRepository.lastUserFailedTest(user);
    }

    @Override
    public void saveFailedTest(FailedTest failedTestDetails) {
        FailedTestDAO failedTest = Converter.convertFailedTestModelToDAO(failedTestDetails);
        failedTest.setTimestamp(System.currentTimeMillis());

        failedTestRepository.save(failedTest);
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

            if (nonNull(testDAO)) {
                testDAO.setTimestamp(System.currentTimeMillis());
                testRepository.save(testDAO);
            }

        } catch (Exception e) {
            logger.error("Failed to save test, " + e.getMessage(), e);
            decreaseComparisonInfoCounters(testDAO);
        }
    }

    private Test increaseComparisonInfoCounters(Test test) {

        test.getComparisonInfoTests()
            .stream()
            .map(ComparisonInfoIdentifiers::new)
            .forEach(identifiers -> counterService.increaseComparisionInfoDetails(identifiers.getSpeedTestIdentifier(), identifiers.getFileDownloadIdentifier()));

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

    private void insertUserDetails(SystemInfo systemInfo, UserDetailsDAO userDetails) {
        systemInfo.setIsp(userDetails.getIsp());
        systemInfo.setInfrastructure(userDetails.getInfrastructure());
        systemInfo.setSpeed(userDetails.getSpeed());
    }
}