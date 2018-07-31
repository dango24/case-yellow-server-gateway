package com.caseyellow.server.central.persistence.statistics.repository;

import com.caseyellow.server.central.domain.analyzer.model.IdentifierDetails;

import java.util.Map;

public interface UserInfoRepository {

    void saveUserStatistics(String user, Map<String, IdentifierDetails> identifierDetails);
    void saveUserPath(String user, String path);
    String getLastUserPath(String user);
    Map<String, IdentifierDetails> getLastUserStatistics(String user);
}
