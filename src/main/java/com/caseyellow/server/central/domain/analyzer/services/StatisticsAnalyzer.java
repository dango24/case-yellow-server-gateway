package com.caseyellow.server.central.domain.analyzer.services;

import com.caseyellow.server.central.domain.analyzer.model.IdentifierDetails;

import java.util.Map;

public interface StatisticsAnalyzer {
    Map<String, Long> countIPs();
    Map<String, Long> countUserTests();
    Map<String, IdentifierDetails> createIdentifiersDetails(String user, String filter);
    long userLastTest(String user);
    long userLastFailedTest(String user);
    void notifyLastTests();
}
