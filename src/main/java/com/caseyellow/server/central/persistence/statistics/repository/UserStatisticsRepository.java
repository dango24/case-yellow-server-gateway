package com.caseyellow.server.central.persistence.statistics.repository;

import com.caseyellow.server.central.domain.analyzer.model.IdentifierDetails;

import java.util.Map;

public interface UserStatisticsRepository {

    void saveUserStatistics(String user, Map<String, IdentifierDetails> identifierDetails);
    Map<String, IdentifierDetails> getLastUserStatistics(String user);
}
