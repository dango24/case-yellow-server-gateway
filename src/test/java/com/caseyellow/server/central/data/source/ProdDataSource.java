package com.caseyellow.server.central.data.source;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ProdDataSource implements FakeDataSource {

    @Override
    public String getConnection() {
        return "We ware in production";
    }
}
