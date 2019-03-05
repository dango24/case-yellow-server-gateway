package com.caseyellow.server.central.domain.statistics;

import com.caseyellow.server.central.common.Utils;
import com.caseyellow.server.central.domain.analyzer.model.IdentifierDetails;
import com.caseyellow.server.central.domain.analyzer.model.UserDownloadRateInfo;
import com.caseyellow.server.central.domain.analyzer.services.TestPredicateFactory;
import com.caseyellow.server.central.domain.file.model.FileDownloadInfo;
import com.caseyellow.server.central.domain.mail.EmailService;
import com.caseyellow.server.central.domain.mail.User;
import com.caseyellow.server.central.domain.metrics.UsersLastTest;
import com.caseyellow.server.central.domain.test.model.ComparisonInfo;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.test.services.TestService;
import com.caseyellow.server.central.domain.users.UserService;
import com.caseyellow.server.central.exceptions.AnalyzerException;
import com.caseyellow.server.central.persistence.file.dao.FileDownloadInfoDAO;
import com.caseyellow.server.central.persistence.file.repository.FileDownloadInfoRepository;
import com.caseyellow.server.central.persistence.statistics.repository.UserInfoRepository;
import com.caseyellow.server.central.persistence.test.dao.UserDetailsDAO;
import com.caseyellow.server.central.persistence.test.model.LastUserTest;
import com.caseyellow.server.central.persistence.test.repository.UserDetailsRepository;
import com.caseyellow.server.central.persistence.user.model.UserDAO;
import com.caseyellow.server.central.services.storage.FileStorageService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.caseyellow.server.central.common.Utils.calculateDownloadRateFromKBpsToMbps;
import static com.caseyellow.server.central.common.Utils.calculateDownloadRateFromMbpsToKBps;
import static com.caseyellow.server.central.common.Utils.log;
import static com.caseyellow.server.central.persistence.metrics.MetricAverageRepository.AVERAGE_DECIMAL_FORMAT;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.*;

@Service
@Profile("prod")
public class StatisticsAnalyzerImpl implements StatisticsAnalyzer {

    @Value("${all_test_dir}")
    private String allTestsDir;

    private EmailService emailService;
    private TestService testService;
    private FileStorageService fileStorageService;
    private TestPredicateFactory testPredicateFactory;
    private FileDownloadInfoRepository fileDownloadInfoRepository;
    private UserDetailsRepository userDetailsRepository;
    private UserInfoRepository userInfoRepository;
    private UserService userService;

    @Autowired
    public StatisticsAnalyzerImpl(TestService testService,
                                  UserDetailsRepository userDetailsRepository,
                                  EmailService emailService,
                                  FileStorageService fileStorageService,
                                  FileDownloadInfoRepository fileDownloadInfoRepository,
                                  UserInfoRepository userInfoRepository,
                                  UserService userService,
                                  TestPredicateFactory testPredicateFactory) {

        this.testService = testService;
        this.emailService = emailService;
        this.fileStorageService = fileStorageService;
        this.fileDownloadInfoRepository = fileDownloadInfoRepository;
        this.testPredicateFactory = testPredicateFactory;
        this.userDetailsRepository = userDetailsRepository;
        this.userInfoRepository = userInfoRepository;
        this.userService = userService;
    }

    @Override
    public Map<String, Long> countIPs(){

        return testService.getAllTests()
                          .stream()
                          .map(s -> s.getSystemInfo().getPublicIP())
                          .collect(groupingBy(Function.identity(), counting()));
    }

    @Override
    public Map<String, Long> countUserTests() {
        return testService.countUserTests();
    }

    @Override
    @Retryable(value =  {IOException.class, JDBCConnectionException.class} , maxAttempts = 5, backoff = @Backoff(delay = 3000))
    public Map<String, IdentifierDetails> createIdentifiersDetails(String user, String filter, List<Test> allTests) {
        log.info(String.format("create identifiers details for user: %s", user));
        Map<String, List<ComparisonInfo>> testComparisons = getComparisons(user, filter, allTests);

        return testComparisons.entrySet()
                              .stream()
                              .map(entry -> createIdentifierDetails(entry.getKey(), entry.getValue()))
                              .filter(this::isValidIdentifierDetails)
                              .collect(toMap(IdentifierDetails::getIdentifier, Function.identity()));
    }

    private Map<String, IdentifierDetails> createIdentifiersDetails(String user, List<Test> allTests) {
        try {
            return createIdentifiersDetails(user, null, allTests);
        } catch (Exception e) {
            log.error(String.format("Failed to create identifiers details, for user: %s, cause: %s", user, e.getMessage(), e));
            return Collections.emptyMap();
        }
    }

    @Override
    public long userLastTest(String user) {
        if (nonNull(userDetailsRepository.findByUserName(user))) {
            return testService.userLastTest(user);

        } else {
            throw new AnalyzerException(String.format("User: %s not exist", user));
        }
    }

    @Override
    public Map<String, String> meanFileDownloadRate() {

        Map<String, List<FileDownloadInfoDAO>> fileDownloadInfo =
                fileDownloadInfoRepository.findAll()
                                          .stream()
                                          .collect(groupingBy(FileDownloadInfoDAO::getFileName));

        return fileDownloadInfo.entrySet()
                               .stream()
                               .collect(toMap(Map.Entry::getKey, entry -> AVERAGE_DECIMAL_FORMAT.format(calcFileDownloadInfoMean(entry.getValue())) + " Mbps") );
    }

    @Override
    public long userLastFailedTest(String user) {
        if (nonNull(userDetailsRepository.findByUserName(user))) {
            return testService.userLastFailedTest(user);

        } else {
            throw new AnalyzerException(String.format("User: %s not exist", user));
        }
    }

    @Override
    public void sendReports() {
        finishUsersTestReport();
        notifyLastTests();
    }

    private void notifyLastTests() {

        List<UserDAO> users =
                userService.getAllUsers()
                        .stream()
                        .filter(UserDAO::isEnabled)
                        .collect(Collectors.toList());


        List<LastUserTest> allUsersTests = testService.lastUserTests();
        List<LastUserTest> lastUserTests = createLastUserTest(users, allUsersTests, 12);

        if (!lastUserTests.isEmpty()) {
            emailService.sendEmails(lastUserTests);
        }
    }

    @Override
    public UsersLastTest usersLastTest(int lastTimeInHours) {
        List<UserDAO> users =
                userService.getAllUsers()
                        .stream()
                        .filter(UserDAO::isEnabled)
                        .collect(Collectors.toList());

        List<LastUserTest> allUsersTests = testService.lastUserTests();
        List<LastUserTest> missingUsers = createLastUserTest(users, allUsersTests, lastTimeInHours);

        Map<String, UserDAO> activeUsers =
                users.stream()
                        .collect(toMap(UserDAO::getUserName, Function.identity()));

        List<LastUserTest> lastUserTestsByLastTime =
                CollectionUtils.subtract(allUsersTests, missingUsers)
                               .stream()
                               .filter(user -> nonNull(activeUsers.get(user.getUser()))) // True indicate the user is active
                               .filter(user -> activeUsers.get(user.getUser()).isEnabled())
                               .sorted(Comparator.comparing(LastUserTest::getTimestamp))
                               .collect(toList());

        Collections.sort(missingUsers, Comparator.comparing(LastUserTest::getTimestamp));

        UsersLastTest usersLastTest = new UsersLastTest();
        usersLastTest.setUsersLastTests(lastUserTestsByLastTime);
        usersLastTest.setUsersCount(lastUserTestsByLastTime.size());
        usersLastTest.setMissingUsers(missingUsers);
        usersLastTest.setMissingUsersCount(missingUsers.size());

        return usersLastTest;
    }

    @Override
    public Map<String, String> getUserMeanRate(String user) {
        Map<String, UserDownloadRateInfo> userDownloadRateInfo = userInfoRepository.getMeanRate(user);

        return userDownloadRateInfo.entrySet()
                                   .stream()
                                   .map(Map.Entry::getValue)
                                   .sorted(Comparator.comparing(UserDownloadRateInfo::getActualRate))
                                   .collect(toMap(UserDownloadRateInfo::getUser, UserDownloadRateInfo::toString, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    public Map<String, UserDownloadRateInfo> buildUserMeanRate(String user, List<Test> allTests) {
        List<Test> tests;
        Map<String, List<Test>> userToDownloadRateTests;
        Map<String, UserDetailsDAO> userDetails;

        userDetails =
                userDetailsRepository.findAll()
                                     .stream()
                                     .collect(toMap(UserDetailsDAO::getUserName, Function.identity()));

        if (user.equals("all")) {
            tests = allTests;
        } else {
            tests = getAllUserTests(allTests, user);
        }

        userToDownloadRateTests = tests.stream().collect(groupingBy(Test::getUser));

        if (user.equals("all")) { // Add all users test rate
            addAllUsersTestRate(tests, userToDownloadRateTests, userDetails);
        }

        return buildUserMeanRate(userToDownloadRateTests, userDetails);
    }

    public void finishUsersTestReport() {

        List<UserTestsStats> users =
                userService.getAllUsers()
                           .stream()
                           .map(userDAO -> UserTestsStats.createUserTestsCountBuilder(userDAO.getUserName(), userDAO.isEnabled()))
                           .map(userTestBuilder -> userTestBuilder.addLanCount(testService.userConnectionCount(userTestBuilder.getName(), "LAN")))
                           .map(userTestBuilder -> userTestBuilder.addWifiCount(testService.userConnectionCount(userTestBuilder.getName(), "Wifi")))
                           .map(UserTestsStats.UserTestsCountBuilder::build)
                           .collect(toList());

        List<UserTestsStats> doneUsers =
            users.stream()
                 .filter(user -> user.getLanCount() > 3000)
                 .filter(user -> user.getWifiCount() > 3000)
                 .sorted()
                 .collect(toList());

        List<UserTestsStats> activeRunningUsers =
            users.stream()
                 .filter(UserTestsStats::isActive)
                 .sorted()
                 .collect(toList());

        emailService.sendUsersDoneTests(doneUsers, activeRunningUsers);
    }

    @Override
    public void buildUsersStatistics(List<User> users) {
        log.info("Start build all users statistics");

        List<Test> allTests = getAllTests();
        users.add(new User("all", true));

        users.stream()
             .filter(User::isEnabled)
             .map(User::getUserName)
             .map(userName -> Pair.of(userName, createIdentifiersDetails(userName, allTests)))
             .forEach(userPair -> userInfoRepository.saveIdentifiersDetails(userPair.getKey(), userPair.getValue()));

        log.info("Successfully build all users identifiers details");

        users.stream()
             .filter(User::isEnabled)
             .map(User::getUserName)
             .map(userName -> Pair.of(userName, buildUserMeanRate(userName, allTests)))
             .forEach(userPair -> userInfoRepository.saveUserMeanRate(userPair.getKey(), userPair.getValue()));

        log.info("Successfully build all users statistics");
    }

    @Override
    public Map<String, IdentifierDetails> getIdentifiersDetails(String user) {
        return userInfoRepository.getLastUserStatistics(user);
    }

    @Override
    public void buildAllTests() {
        File tmpFile = null;

        try {
            log.info("Fetching all tests");
            List<Test> tests = testService.getAllTests();
            log.info("Successfully fetched all tests");

            String tmpFilePath = String.format("all_tests_%s.json", System.currentTimeMillis());
            String s3path = String.format("%s/%s", allTestsDir, tmpFilePath);
            tmpFile = new File(Utils.getTmpDir(), tmpFilePath);

            new ObjectMapper().writeValue(tmpFile, tests);
            fileStorageService.uploadFile(s3path, tmpFile);
            userInfoRepository.saveUserPath("all-tests", s3path);

            log.info("Successfully build all tests");

        } catch (IOException e) {
            log.error(String.format("Failed to build all test file: %s", e.getMessage(), e));
        } finally {
            Utils.deleteFile(tmpFile);
        }
    }

    @Override
    public List<Test> getAllTests(){
        File allTestsFile = null;

        try {
            String path = userInfoRepository.getLastUserPath("all-tests");
            allTestsFile = fileStorageService.getFile(path);
            List<Test> tests = new ObjectMapper().readValue(allTestsFile, new TypeReference<List<Test>>() {});

            return tests;

        } catch (Exception e) {
            log.error(String.format("failed to get all tests: %s", e.getMessage(), e));
            return Collections.emptyList();

        } finally {
            Utils.deleteFile(allTestsFile);
        }
    }

    private void addAllUsersTestRate(List<Test> tests, Map<String, List<Test>> userToDownloadRateTests, Map<String, UserDetailsDAO> userDetails) {
        userToDownloadRateTests.put("all", tests);
        UserDetailsDAO userDetailsDAO = new UserDetailsDAO("all");

        double meanAllUserRate =
                userDetails.values()
                           .stream()
                           .mapToInt(UserDetailsDAO::getSpeed)
                           .average()
                           .orElse(-1);

        userDetailsDAO.setSpeed((int)meanAllUserRate);

        userDetails.put("all", userDetailsDAO);
    }

    private Map<String, UserDownloadRateInfo> buildUserMeanRate(Map<String, List<Test>> userToDownloadRateTests, Map<String, UserDetailsDAO> userDetails) {
        Map<String, Double> userToDownloadRate;

        userToDownloadRate =
                userToDownloadRateTests.entrySet()
                                       .stream()
                                       .collect(toMap(Map.Entry::getKey, entry -> getMeanRateInMbps(entry.getValue())));

        return userToDownloadRate.entrySet()
                .stream()
                .map(entry -> new UserDownloadRateInfo(entry.getKey(), entry.getValue(), userDetails.get(entry.getKey()).getSpeed(), userDetails.get(entry.getKey()).getInfrastructure(), userToDownloadRateTests.get(entry.getKey()).size()))
                .collect(toMap(UserDownloadRateInfo::getUser, Function.identity()));
    }

    private double getMeanRateInMbps(List<Test> tests) {
        double meanDownloadRateInKBps = getDownloadRateFromTests(tests);
        return calculateDownloadRateFromKBpsToMbps(meanDownloadRateInKBps);
    }

    private double getDownloadRateFromTests(List<Test> tests) {
        return tests.stream()
                    .flatMap(test -> test.getComparisonInfoTests().stream())
                    .map(ComparisonInfo::getFileDownloadInfo)
                    .mapToDouble(FileDownloadInfo::getFileDownloadRateKBPerSec)
                    .average()
                    .orElse(-1);
    }

    private List<LastUserTest> createLastUserTest(List<UserDAO> users, List<LastUserTest> allUsersTests, int thresholdInHours) {
        Map<String, UserDAO> activeUsers =
                users.stream()
                        .collect(toMap(UserDAO::getUserName, Function.identity()));

        List<LastUserTest> lastUserTests =
                allUsersTests.stream()
                             .filter(user -> nonNull(activeUsers.get(user.getUser()))) // True indicate the user is active
                             .filter(user -> activeUsers.get(user.getUser()).isEnabled()) // True indicate the user is active
                             .filter(user -> nonNull(activeUsers.get(user.getUser()).getHasComputer()))
                             .filter(user -> activeUsers.get(user.getUser()).getHasComputer().booleanValue())
                             .filter(user -> isLastTestOverThreshold(user, thresholdInHours))
                             .sorted(Comparator.comparing(LastUserTest::getTimestamp))
                             .collect(Collectors.toList());

        lastUserTests.forEach(user -> user.setPhone(activeUsers.get(user.getUser()).getPhone()));

        return lastUserTests;
    }

    private boolean isLastTestOverThreshold(LastUserTest lastUserTest, int thresholdInHours) {
        if (isNull(lastUserTest)) {
            return false;
        }

        return (System.currentTimeMillis() - lastUserTest.getTimestamp()) > TimeUnit.HOURS.toMillis(thresholdInHours);
    }

    private IdentifierDetails createIdentifierDetails(String identifier, List<ComparisonInfo> comparisonInfos) {
        int size = comparisonInfos.size();
        double meanRatio = getMeanRatio(comparisonInfos);

        return new IdentifierDetails(identifier, meanRatio, size);
    }

    private boolean isValidIdentifierDetails(IdentifierDetails identifierDetails) {
        return identifierDetails.getMeanRatio() > 0;
    }

    private Map<String, List<ComparisonInfo>> getComparisons(String user, String filter, List<Test> allTests) {
        List<Test> tests;
        Predicate<Test> testPredicate = testPredicateFactory.getTestPredicate(filter);

        if (CollectionUtils.isEmpty(allTests)) {
            tests = getAllTests();
        } else {
            tests = allTests;
        }

        if (StringUtils.isNotEmpty(user) && !user.equals("all")) {
            tests = getAllUserTests(tests, user);
        }

        return tests.stream()
                    .filter(testPredicate::test)
                    .flatMap(test -> test.getComparisonInfoTests().stream())
                    .collect(groupingBy(c -> c.getSpeedTestWebSite().getSpeedTestIdentifier()));
    }

    private  List<Test> getAllUserTests(List<Test> allTests, String user) {

        return allTests.stream()
                       .filter(test -> test.getUser().equals(user))
                       .collect(toList());
    }

    private double getMeanRatio(List<ComparisonInfo> comparisons){

        return comparisons.stream()
                          .filter(this::isValidSubTest)
                          .mapToDouble(this::getRatio)
                          .filter(this::isNotOutlier)
                          .average()
                          .orElse(-1);
    }

    private boolean isValidSubTest(ComparisonInfo comparisonInfo) {
        if (isNull(comparisonInfo.getSpeedTestWebSite()) ||
            isNull(comparisonInfo.getSpeedTestWebSite())) {
            return false;
        }

        double speedTestRate = comparisonInfo.getSpeedTestWebSite().getDownloadRateInMbps();
        double downloadRate = comparisonInfo.getFileDownloadInfo().getFileDownloadRateKBPerSec();

        return speedTestRate > 0 && downloadRate > 0;
    }

    private double getRatio(ComparisonInfo comparisonInfo){
        double speedTestRate = calculateDownloadRateFromMbpsToKBps(comparisonInfo.getSpeedTestWebSite().getDownloadRateInMbps());
        double downloadRate = comparisonInfo.getFileDownloadInfo().getFileDownloadRateKBPerSec();
        double ratio = downloadRate / speedTestRate;

        return ratio;
    }

    private double calcFileDownloadInfoMean(List<FileDownloadInfoDAO> fileDownloadInfo) {

        return fileDownloadInfo.stream()
                               .mapToDouble(FileDownloadInfoDAO::getFileDownloadRateKBPerSec)
                               .map(Utils::calculateDownloadRateFromKBpsToMbps)
                               .average()
                               .orElse(-1);
    }

    private boolean isNotOutlier(double ratio) {
        return ratio < 100 && ratio > 0.01;
    }
}
