package com.caseyellow.server.central.domain.webSite.services;

import com.caseyellow.server.central.common.UrlMapper;
import com.caseyellow.server.central.persistence.repository.SpeedTestWebSiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by dango on 9/19/17.
 */
@Service
public class SpeedTestWebSiteServiceImpl implements SpeedTestWebSiteService {

    private UrlMapper urlMapper;
    private SpeedTestWebSiteRepository speedTestWebSiteRepository;

    @Autowired
    public SpeedTestWebSiteServiceImpl(SpeedTestWebSiteRepository speedTestWebSiteRepository, UrlMapper urlMapper) {
        this.speedTestWebSiteRepository = speedTestWebSiteRepository;
        this.urlMapper = urlMapper;
    }

    @Override
    public String getNextSpeedTestWebSiteURL() {
        String nextSpeedTestIdentifier = speedTestWebSiteRepository.findMinIdentifier();

        return urlMapper.getSpeedTest(nextSpeedTestIdentifier);
    }
}
