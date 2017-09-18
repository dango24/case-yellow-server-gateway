package com.caseyellow.server.central.domain.test.services;

import com.caseyellow.server.central.domain.test.model.Test;

import java.util.List;

/**
 * Created by dango on 8/15/17.
 */
public interface TestService {

    void saveTest(Test test);
    String getNextSpeedTestWebSite();
    List<String> getNextUrls(int numOfComparisonPerTest);
}
