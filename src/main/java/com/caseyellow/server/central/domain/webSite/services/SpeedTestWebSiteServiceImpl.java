package com.caseyellow.server.central.domain.webSite.services;

import com.caseyellow.server.central.configuration.UrlConfig;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestMetaData;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestNonFlashMetaData;
import com.caseyellow.server.central.domain.webSite.model.WordIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by dango on 9/19/17.
 */
@Service
public class SpeedTestWebSiteServiceImpl implements SpeedTestWebSiteService {

    private UrlConfig urlMapper;
    private SpeedTestWebSiteFactory speedTestWebSiteFactory;

    @Autowired
    public SpeedTestWebSiteServiceImpl(UrlConfig urlMapper, SpeedTestWebSiteFactory speedTestWebSiteFactory) {
        this.urlMapper = urlMapper;
        this.speedTestWebSiteFactory = speedTestWebSiteFactory;
    }

    @Override
    public SpeedTestMetaData getNextSpeedTestWebSite() {
        List<String> speedTestIdentifiers = new ArrayList<>(urlMapper.getSpeedTestIdentifiers());
        int random = new Random().nextInt(speedTestIdentifiers.size());

        return speedTestWebSiteFactory.getSpeedTestWebSiteFromIdentifier(speedTestIdentifiers.get(random));
    }

    @Override
    public Set<WordIdentifier> getTextIdentifiers(String identifier, boolean startTest) {
        SpeedTestMetaData speedTestMetaData = speedTestWebSiteFactory.getSpeedTestWebSiteFromIdentifier(identifier);

        if (startTest) {
            return speedTestMetaData.getSpeedTestFlashMetaData().getButtonIds();
        } else {
            return speedTestMetaData.getSpeedTestFlashMetaData().getFinishIdentifiers();
        }
    }

    @Override
    public SpeedTestNonFlashMetaData getSpeedTestNonFlashMetaData(String identifier) {
        SpeedTestMetaData speedTestMetaData = speedTestWebSiteFactory.getSpeedTestWebSiteFromIdentifier(identifier);
        return speedTestMetaData.getSpeedTestNonFlashMetaData();
    }
}
