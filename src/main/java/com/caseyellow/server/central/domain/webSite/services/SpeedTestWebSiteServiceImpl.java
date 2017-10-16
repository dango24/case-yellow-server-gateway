package com.caseyellow.server.central.domain.webSite.services;

import com.caseyellow.server.central.domain.webSite.model.SpeedTestMetaData;
import com.caseyellow.server.central.persistence.website.repository.SpeedTestWebSiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dango on 9/19/17.
 */
@Service
public class SpeedTestWebSiteServiceImpl implements SpeedTestWebSiteService {

    private SpeedTestWebSiteFactory speedTestWebSiteFactory;
    private SpeedTestWebSiteRepository speedTestWebSiteRepository;

    @Autowired
    public SpeedTestWebSiteServiceImpl(SpeedTestWebSiteRepository speedTestWebSiteRepository, SpeedTestWebSiteFactory speedTestWebSiteFactory) {
        this.speedTestWebSiteRepository = speedTestWebSiteRepository;
        this.speedTestWebSiteFactory = speedTestWebSiteFactory;
    }

    @Override
    public SpeedTestMetaData getNextSpeedTestWebSite() {
        return speedTestWebSiteFactory.getSpeedTestWebSiteFromIdentifier(speedTestWebSiteRepository.findMinIdentifier());
    }
}
