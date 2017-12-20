package com.caseyellow.server.central.configuration;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class GoogleVisionConfigurationStub implements GoogleVisionConfiguration {

    @Override
    public String googleVisionKey() {
        return null;
    }
}
