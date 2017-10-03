package com.caseyellow.server.central.domain.webSite.services;

import com.caseyellow.server.central.persistence.repository.SpeedTestWebSiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created by dango on 9/19/17.
 */
@Service
public class SpeedTestWebSiteServiceImpl implements SpeedTestWebSiteService {

    private final static String DEFAULT_URL = "http://www.hot.net.il/heb/Internet/speed/";

    private Map<String, String> identifierToURLMapper;
    private SpeedTestWebSiteRepository speedTestWebSiteRepository;

    @Autowired
    public SpeedTestWebSiteServiceImpl(SpeedTestWebSiteRepository speedTestWebSiteRepository) {
        this.speedTestWebSiteRepository = speedTestWebSiteRepository;
    }

    @PostConstruct
    public void init() {
        identifierToURLMapper = speedTestWebSiteRepository.getIdentifierToURLMapper();
    }

    @Override
    public String getNextSpeedTestWebSiteURL() {
        String nextSpeedTestIdentifier = speedTestWebSiteRepository.findMinIdentifier();

        return identifierToURLMapper.getOrDefault(nextSpeedTestIdentifier, DEFAULT_URL);
    }
}
