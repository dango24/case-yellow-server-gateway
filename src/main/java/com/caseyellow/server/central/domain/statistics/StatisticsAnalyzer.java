package com.caseyellow.server.central.domain.statistics;

import com.caseyellow.server.central.domain.analyzer.model.IdentifierDetails;
import com.caseyellow.server.central.domain.mail.User;
import com.caseyellow.server.central.domain.metrics.UsersLastTest;
import com.caseyellow.server.central.domain.test.model.Test;

import java.util.List;
import java.util.Map;

public interface StatisticsAnalyzer {
    Map<String, Long> countIPs();
    Map<String, Long> countUserTests();
    Map<String, IdentifierDetails> createIdentifiersDetails(String user, String filter, List<Test> allTests);
    Map<String, String> meanFileDownloadRate();
    long userLastTest(String user);
    long userLastFailedTest(String user);
    void notifyLastTests();
    void buildUsersStatistics(List<User> users);
    UsersLastTest usersLastTest(int lastTimeInHours);
    Map<String, String> getUserMeanRate(String user);
    Map<String,IdentifierDetails> getIdentifiersDetails(String user);
    void buildAllTests();
    List<Test> getAllTests();
}
