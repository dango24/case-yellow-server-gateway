package com.caseyellow.server.gateway.domain.interfaces;

import com.caseyellow.server.gateway.domain.Test;

import java.util.List;

/**
 * Created by dango on 8/15/17.
 */
public interface CentralService {

    void saveTest(Test test);
    String getNextSpeedTestWebSite();
    List<String> getNextUrls(int numOfComparisonPerTest);
}
