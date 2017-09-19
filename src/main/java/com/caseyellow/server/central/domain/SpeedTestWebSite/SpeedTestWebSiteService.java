package com.caseyellow.server.central.domain.SpeedTestWebSite;

import java.util.List;

/**
 * Created by dango on 9/19/17.
 */
public interface SpeedTestWebSiteService {

    String getNextSpeedTestWebSite();
    List<String> getNextUrls(int numOfComparisonPerTest);
}
