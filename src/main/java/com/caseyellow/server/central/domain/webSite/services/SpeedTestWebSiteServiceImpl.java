package com.caseyellow.server.central.domain.webSite.services;

import com.caseyellow.server.central.domain.webSite.model.SpeedTestMetaData;
import com.caseyellow.server.central.persistence.website.repository.SpeedTestWebSiteCounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dango on 9/19/17.
 */
@Service
public class SpeedTestWebSiteServiceImpl implements SpeedTestWebSiteService {

    private SpeedTestWebSiteFactory speedTestWebSiteFactory;
    private SpeedTestWebSiteCounterRepository speedTestWebSiteCounterRepository;

    @Autowired
    public SpeedTestWebSiteServiceImpl(SpeedTestWebSiteCounterRepository speedTestWebSiteCounterRepository, SpeedTestWebSiteFactory speedTestWebSiteFactory) {
        this.speedTestWebSiteCounterRepository = speedTestWebSiteCounterRepository;
        this.speedTestWebSiteFactory = speedTestWebSiteFactory;
    }

    @Override
    public SpeedTestMetaData getNextSpeedTestWebSite() {
        return speedTestWebSiteFactory.getSpeedTestWebSiteFromIdentifier(speedTestWebSiteCounterRepository.findMinIdentifier());
    }
}
