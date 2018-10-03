package com.caseyellow.server.central.domain.webSite.services;


import com.caseyellow.server.central.domain.webSite.model.SpeedTestMetaData;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestNonFlashMetaData;
import com.caseyellow.server.central.domain.webSite.model.WordIdentifier;

import java.util.List;
import java.util.Set;


/**
 * Created by dango on 9/19/17.
 */
public interface SpeedTestWebSiteService {
    SpeedTestMetaData getNextSpeedTestWebSite();
    Set<WordIdentifier> getTextIdentifiers(String identifier, boolean startTest);
    SpeedTestNonFlashMetaData getSpeedTestNonFlashMetaData(String identifier);
    void investigateSuspiciousTestRatio(String outliarRatio, String hours);
    List<String> getChromeOptionsArguments(String user);
}
