package com.caseyellow.server.central.domain.analyzer.services;

import com.caseyellow.server.central.domain.test.model.Test;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

import static java.util.Objects.isNull;

@Component
public class TestPredicateFactory {

    private static final String LAN_CONNECTION = "LAN";
    private static final String WIFI_CONNECTION = "Wifi";

    public Predicate<Test> getTestPredicate(String filter) {

        if (isNull(filter) || StringUtils.isEmpty(filter)) {
            return test -> true;
        }

        switch (filter) {
            case LAN_CONNECTION:
                return test -> test.getSystemInfo().getConnection().equals(LAN_CONNECTION);

            case WIFI_CONNECTION:
                return test -> test.getSystemInfo().getConnection().equals(WIFI_CONNECTION);

            default:
                return test -> true;
        }
    }
}
