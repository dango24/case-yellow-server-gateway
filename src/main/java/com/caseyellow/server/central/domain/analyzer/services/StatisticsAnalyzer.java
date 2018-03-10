package com.caseyellow.server.central.domain.analyzer.services;

import com.caseyellow.server.central.domain.analyzer.model.IdentifierDetails;

import java.util.Map;

public interface StatisticsAnalyzer {
    Map<String, Long> countIPs();
    Map<String, IdentifierDetails> createIdentifiersDetails(String user);
    long userLastTest(String user);
    long userLastFailedTest(String user);
}
