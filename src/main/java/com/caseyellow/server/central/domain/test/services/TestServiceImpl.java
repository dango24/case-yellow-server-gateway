package com.caseyellow.server.central.domain.test.services;

import com.caseyellow.server.central.common.Converter;
import com.caseyellow.server.central.common.Validator;
import com.caseyellow.server.central.configuration.ConnectionConfig;
import com.caseyellow.server.central.domain.counter.CounterService;
import com.caseyellow.server.central.domain.metrics.MetricsService;
import com.caseyellow.server.central.domain.test.model.*;
import com.caseyellow.server.central.persistence.test.dao.FailedTestDAO;
import com.caseyellow.server.central.persistence.test.dao.TestDAO;
import com.caseyellow.server.central.persistence.test.dao.UserDetailsDAO;
import com.caseyellow.server.central.persistence.test.model.LastUserTest;
import com.caseyellow.server.central.persistence.test.model.UserTestCount;
import com.caseyellow.server.central.persistence.test.repository.FailedTestRepository;
import com.caseyellow.server.central.persistence.test.repository.TestRepository;
import com.caseyellow.server.central.persistence.test.repository.UserDetailsRepository;
import com.caseyellow.server.central.services.storage.FileStorageService;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private MetricsService metricsService;
    private TestRepository testRepository;
    private FailedTestRepository failedTestRepository;
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    public TestServiceImpl(ConnectionConfig connectionConfig,
                           CounterService counterService,
                           FileStorageService fileStorageService,
                           MetricsService metricsService,
                           TestRepository testRepository,
                           FailedTestRepository failedTestRepository,
                           UserDetailsRepository userDetailsRepository) {

        this.testRepository = testRepository;
        this.counterService = counterService;
        this.connectionConfig = connectionConfig;
        this.fileStorageService = fileStorageService;
        this.metricsService = metricsService;
        this.failedTestRepository = failedTestRepository;
        this.userDetailsRepository = userDetailsRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Test> getAllTests() {
        int page = 0;
        int size = 1000;
        Page<TestDAO> testsPage = null;
        List<TestDAO> tests = new ArrayList<>();

        do {
            testsPage = testRepository.findAll(new PageRequest(page, size));
            tests.addAll(testsPage.getContent());
            page++;

        } while (nonNull(testsPage) && testsPage.hasNext());

        return convertToTestModel(tests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Test> getAllTestsByUser(String user) {
        int page = 0;
        int size = 500;
        Page<TestDAO> testsPage = null;
        List<TestDAO> userTests = new ArrayList<>();

        do {
            testsPage = testRepository.findByUser(user, new PageRequest(page, size));
            userTests.addAll(testsPage.getContent());
            page++;

        } while (nonNull(testsPage) && testsPage.hasNext());

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
    public Map<String, Long> countUserTests() {
        List<UserTestCount> userTestsCount = testRepository.countUserTests();

        return userTestsCount.stream()
                             .collect(toMap(UserTestCount::getUserName, UserTestCount::getCount));
    }

    @Override
    public List<LastUserTest> lastUserTests() {
        return testRepository.getAllUsersLastTests();
    }

    @Override
    public int getTestLifeCycle(String userName) {
        return counterService.getUserTestLifeCycle(userName);
    }

    @Override
    public void updateTestLifeCycle(String userName) {
        counterService.updateTestLifeCycle(userName);
    }

    @Override
    public long userConnectionCount(String user, String connection) {
        return testRepository.userConnectionCount(user, connection);
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

        if (StringUtils.isNotEmpty(failedTest.getErrorMessage()) && failedTest.getErrorMessage().length() >= 250) {
            failedTest.setErrorMessage(failedTest.getErrorMessage().substring(0, 250));
        }

        failedTestRepository.save(failedTest);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @CacheEvict(value = { "tests", "countIPs", "identifiersDetails" }, allEntries = true)
    public void saveTest(Test test) {
        if (!validateTest(test)) {
           throw new IllegalArgumentException("Test is not valid, test: " + new Gson().toJson(test));
        }

        CompletableFuture.supplyAsync(() -> test)
                         .thenApply(this::removeUnsuccessfulTests)
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
                                                         .filter(comparisonInfo -> !test.isClassicTest() || comparisonInfo.getSpeedTestWebSite().isSucceed())
                                                         .collect(toList());

        test.setComparisonInfoTests(comparisonInfoSucceed);

        return test;
    }

    @Retryable(value =  Exception.class , maxAttempts = 5, backoff = @Backoff(delay = 3000))
    private void save(TestDAO testDAO) {
        if (nonNull(testDAO)) {
            logger.info(String.format("Saving test: %s, for user: %s", testDAO.getTestID(), testDAO.getUser()));
            testDAO.setTimestamp(System.currentTimeMillis());
            testRepository.save(testDAO);
            logger.info(String.format("Successfully save test: %s, for user: %s", testDAO.getTestID(), testDAO.getUser()));
        }
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

    private TestDAO addMetrics(TestDAO test) {
        metricsService.addMetrics(test);
        return test;
    }

}