package com.caseyellow.server.central.persistence.statistics.repository;

import com.caseyellow.server.central.domain.analyzer.model.IdentifierDetails;
import com.caseyellow.server.central.domain.analyzer.model.UserDownloadRateInfo;

import java.util.Map;

public interface UserInfoRepository {

    void saveIdentifiersDetails(String user, Map<String, IdentifierDetails> identifierDetails);
    void saveUserMeanRate(String user, Map<String, UserDownloadRateInfo> meanRate);
    void saveUserPath(String user, String path);
    String getLastUserPath(String user);
    Map<String, IdentifierDetails> getLastUserStatistics(String user);
    Map<String, UserDownloadRateInfo> getMeanRate(String user);
}
