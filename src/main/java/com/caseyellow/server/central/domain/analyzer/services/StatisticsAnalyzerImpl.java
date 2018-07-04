package com.caseyellow.server.central.domain.analyzer.services;

import com.caseyellow.server.central.domain.analyzer.model.IdentifierDetails;
import com.caseyellow.server.central.domain.analyzer.model.UserDownloadRateInfo;
import com.caseyellow.server.central.domain.file.model.FileDownloadInfo;
import com.caseyellow.server.central.domain.mail.EmailService;
import com.caseyellow.server.central.domain.mail.User;
import com.caseyellow.server.central.domain.metrics.UsersLastTest;
import com.caseyellow.server.central.domain.test.model.ComparisonInfo;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.test.services.TestService;
import com.caseyellow.server.central.exceptions.AnalyzerException;
import com.caseyellow.server.central.persistence.test.dao.UserDetailsDAO;
import com.caseyellow.server.central.persistence.test.model.LastUserTest;
import com.caseyellow.server.central.persistence.test.repository.UserDetailsRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.caseyellow.server.central.common.Utils.calculateDownloadRateFromKBpsToMbps;
import static com.caseyellow.server.central.common.Utils.calculateDownloadRateFromMbpsToKBps;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.*;

@Service
public class StatisticsAnalyzerImpl implements StatisticsAnalyzer {

    private EmailService emailService;
    private TestService testService;
    private TestPredicateFactory testPredicateFactory;
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    public StatisticsAnalyzerImpl(TestService testService,
                                  UserDetailsRepository userDetailsRepository,
                                  EmailService emailService,
                                  TestPredicateFactory testPredicateFactory) {

        this.testService = testService;
        this.emailService = emailService;
        this.testPredicateFactory = testPredicateFactory;
        this.userDetailsRepository = userDetailsRepository;
    }

    @Override
    @Cacheable("countIPs")
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
    @Cacheable("identifiersDetails")
    public Map<String, IdentifierDetails> createIdentifiersDetails(String user, String filter) {
        Map<String, List<ComparisonInfo>> testComparisons = getComparisons(user, filter);

        return testComparisons.entrySet()
                              .stream()
                              .map(entry -> createIdentifierDetails(entry.getKey(), entry.getValue()))
                              .filter(this::isValidIdentifierDetails)
                              .collect(toMap(IdentifierDetails::getIdentifier, Function.identity()));
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
    public long userLastFailedTest(String user) {
        if (nonNull(userDetailsRepository.findByUserName(user))) {
            return testService.userLastFailedTest(user);

        } else {
            throw new AnalyzerException(String.format("User: %s not exist", user));
        }
    }

    @Override
    public void notifyLastTests(List<User> users) {
        List<LastUserTest> allUsersTests = testService.lastUserTests();
        List<LastUserTest> lastUserTests = createLastUserTest(users, allUsersTests, 12);

        if (!lastUserTests.isEmpty()) {
            emailService.sendEmails(lastUserTests);
        }
    }

    @Override
    public UsersLastTest usersLastTest(List<User> users, int lastTimeInHours) {
        List<LastUserTest> allUsersTests = testService.lastUserTests();
        List<LastUserTest> missingUsers = createLastUserTest(users, allUsersTests, lastTimeInHours);

        List<LastUserTest> lastUserTestsByLastTime =
                CollectionUtils.subtract(allUsersTests, missingUsers)
                               .stream()
                               .collect(toList());

        UsersLastTest usersLastTest = new UsersLastTest();
        usersLastTest.setUsersLastTests(lastUserTestsByLastTime);
        usersLastTest.setUsersCount(lastUserTestsByLastTime.size());
        usersLastTest.setMissingUsers(missingUsers);
        usersLastTest.setUsersCount(missingUsers.size());

        return usersLastTest;
    }

    @Override
    public Map<String, String> getUserMeanRate(String user) {
        List<Test> tests;
        Map<String, List<Test>> userToDownloadRateTests;
        Map<String, UserDetailsDAO> userDetails;

        userDetails =
                userDetailsRepository.findAll()
                                     .stream()
                                     .collect(toMap(UserDetailsDAO::getUserName, Function.identity()));

        if (user.equals("all")) {
            tests = testService.getAllTests();
        } else {
            tests = testService.getAllTestsByUser(user);
        }

        userToDownloadRateTests = tests.stream().collect(groupingBy(Test::getUser));

        if (user.equals("all")) { // Add all users test rate
            addAllUsersTestRate(tests, userToDownloadRateTests, userDetails);
        }

        return getUserMeanRate(userToDownloadRateTests, userDetails);
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

    private Map<String, String> getUserMeanRate(Map<String, List<Test>> userToDownloadRateTests, Map<String, UserDetailsDAO> userDetails) {
        Map<String, Double> userToDownloadRate;

        userToDownloadRate =
                userToDownloadRateTests.entrySet()
                                       .stream()
                                       .collect(toMap(Map.Entry::getKey, entry -> getMeanRateInMbps(entry.getValue())));

        return userToDownloadRate.entrySet()
                                 .stream()
                                 .map(entry -> new UserDownloadRateInfo(entry.getKey(), entry.getValue(), userDetails.get(entry.getKey()).getSpeed()))
                                 .collect(toMap(UserDownloadRateInfo::getUser, UserDownloadRateInfo::toString));
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

    private List<LastUserTest> createLastUserTest(List<User> users, List<LastUserTest> allUsersTests, int thresholdInHours) {
        Map<String, User> activeUsers =
                users.stream()
                        .collect(toMap(User::getUserName, Function.identity()));

        List<LastUserTest> lastUserTests =
                allUsersTests.stream()
                             .filter(user -> activeUsers.get(user.getUser()).isEnabled()) // True indicate the user is active
                             .filter(user -> isLastTestOverThreshold(user, thresholdInHours))
                             .sorted(Comparator.comparing(LastUserTest::getTimestamp))
                             .collect(Collectors.toList());

        lastUserTests.forEach(user -> user.setPhone(activeUsers.get(user.getUser()).getPhone()));

        return lastUserTests;
    }

    private boolean isLastTestOverThreshold(LastUserTest lastUserTest, int thresholdInHours) {
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

    private Map<String, List<ComparisonInfo>> getComparisons(String user, String filter) {
        List<Test> tests;
        Predicate<Test> testPredicate = testPredicateFactory.getTestPredicate(filter);

        if (isNull(user)) {
            tests = testService.getAllTests();
        } else {
            tests = testService.getAllTestsByUser(user);
        }

        return tests.stream()
                    .filter(testPredicate::test)
                    .flatMap(test -> test.getComparisonInfoTests().stream())
                    .collect(groupingBy(c -> c.getSpeedTestWebSite().getSpeedTestIdentifier()));
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

    private boolean isNotOutlier(double ratio) {
        return ratio < 100 && ratio > 0.01;
    }
}
