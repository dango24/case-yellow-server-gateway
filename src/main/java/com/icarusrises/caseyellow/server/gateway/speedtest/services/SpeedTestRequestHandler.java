package com.icarusrises.caseyellow.server.gateway.speedtest.services;

import org.springframework.stereotype.Service;

/**
 * Created by dango on 6/4/17.
 */
@Service
public class SpeedTestRequestHandler implements SpeedTestService {

    @Override
    public String print(String name) {
        return getClass().getSimpleName() + " : " + name;
    }
}
