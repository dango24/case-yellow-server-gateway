package com.caseyellow.server.central.domain.statistics;

import com.caseyellow.server.central.domain.analyzer.model.IdentifierDetails;
import com.caseyellow.server.central.domain.mail.User;
import com.caseyellow.server.central.domain.metrics.UsersLastTest;

import java.util.List;
import java.util.Map;

public interface StatisticsAnalyzer {
    Map<String, Long> countIPs();
    Map<String, Long> countUserTests();
    Map<String, IdentifierDetails> createIdentifiersDetails(String user, String filter);
    Map<String, String> meanFileDownloadRate();
    long userLastTest(String user);
    long userLastFailedTest(String user);
    void notifyLastTests(List<User> users);
    UsersLastTest usersLastTest(List<User> users, int lastTimeInHours);
    Map<String, String> getUserMeanRate(String user);
    void usersStatistics(List<User> users);
    Map<String,IdentifierDetails> getIdentifiersDetails(String user);
    void buildAllTests();
}
