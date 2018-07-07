package com.caseyellow.server.central.domain.analyzer.services;

import com.caseyellow.server.central.domain.test.model.Test;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Objects.isNull;

@Component
public class TestPredicateFactory {

    private static final String LAN_CONNECTION = "LAN";
    private static final String WIFI_CONNECTION = "Wifi";

    public Predicate<Test> getTestPredicate(String filter) {
        String[] filterArgs;

        if (isNull(filter) || StringUtils.isEmpty(filter)) {
            return test -> true;
        }

        filterArgs = filter.split("-");

        switch (filterArgs[0]) {
            case LAN_CONNECTION:
                return test -> test.getSystemInfo().getConnection().equals(LAN_CONNECTION);

            case WIFI_CONNECTION:
                return test -> test.getSystemInfo().getConnection().equals(WIFI_CONNECTION);

            case "exclude":
                return test -> !getUsers(filterArgs[1]).contains(test.getUser());
            default:
                return test -> true;
        }
    }

    private List<String> getUsers(String usersArg) {
        String[] excludeUsers = usersArg.split("_");

        return Arrays.asList(excludeUsers);
    }
}
