package com.caseyellow.server.central.domain.SpeedTestWebSite;

import com.caseyellow.server.central.persistence.test.repository.SpeedTestWebSiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<String> getNextUrls(int numOfComparisonPerTest) {
        return null;
    }
}
