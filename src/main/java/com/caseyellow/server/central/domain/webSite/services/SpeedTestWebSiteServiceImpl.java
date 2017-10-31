package com.caseyellow.server.central.domain.webSite.services;

import com.caseyellow.server.central.common.UrlMapper;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestMetaData;
import com.caseyellow.server.central.persistence.website.repository.SpeedTestWebSiteCounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by dango on 9/19/17.
 */
@Service
public class SpeedTestWebSiteServiceImpl implements SpeedTestWebSiteService {

    private UrlMapper urlMapper;
    private SpeedTestWebSiteFactory speedTestWebSiteFactory;

    @Autowired
    public SpeedTestWebSiteServiceImpl(UrlMapper urlMapper, SpeedTestWebSiteFactory speedTestWebSiteFactory) {
        this.urlMapper = urlMapper;
        this.speedTestWebSiteFactory = speedTestWebSiteFactory;
    }

    @Override
    public SpeedTestMetaData getNextSpeedTestWebSite() {
        List<String> speedTestIdentifiers = new ArrayList<>(urlMapper.getSpeedTestIdentifiers());
        int random = new Random().nextInt(speedTestIdentifiers.size());

        return speedTestWebSiteFactory.getSpeedTestWebSiteFromIdentifier(speedTestIdentifiers.get(random));
    }
}
