package com.caseyellow.server.central.domain.webSite.services;

import com.caseyellow.server.central.persistence.repository.SpeedTestWebSiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dango on 9/19/17.
 */
@Service
public class SpeedTestWebSiteServiceImpl implements SpeedTestWebSiteService {

    private SpeedTestWebSiteRepository speedTestWebSiteRepository;

    @Autowired
    public SpeedTestWebSiteServiceImpl(SpeedTestWebSiteRepository speedTestWebSiteRepository) {
        this.speedTestWebSiteRepository = speedTestWebSiteRepository;
    }

    @Override
    public String getNextSpeedTestWebSite() {
        return speedTestWebSiteRepository.findMinIdentifier();
    }
}
