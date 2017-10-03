package com.caseyellow.server.central.data.source;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "default"})
public class DevDataSource implements FakeDataSource {

    @Override
    public String getConnection() {
        return "Dev data source";
    }
}
