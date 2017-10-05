package com.caseyellow.server.central.bootstrap;

import com.caseyellow.server.central.exceptions.AppBootException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevAppBoot implements AppBoot {

    @Override
    public void init() throws AppBootException {

    }
}
