package com.caseyellow.server.central.domain.webSite.services;

import com.caseyellow.server.central.domain.webSite.model.SpeedTestMetaData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import org.apache.commons.io.IOUtils;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Component
public class SpeedTestWebSiteFactory {

    private final String SPEED_TEST_METADATA_LOCATION = "/speed_test_meta_data.json";

    private Map<String, SpeedTestMetaData> speedTestMetaDataMap;

    @PostConstruct
    private void init() throws URISyntaxException, IOException {
        String speedTestMetaData = IOUtils.toString(SpeedTestWebSiteFactory.class.getResourceAsStream(SPEED_TEST_METADATA_LOCATION), Charset.forName("UTF-8"));
        SpeedTestMetaDataWrapper speedTestMetaDataWrapper = new ObjectMapper().readValue(speedTestMetaData, SpeedTestMetaDataWrapper.class);

        speedTestMetaDataMap = speedTestMetaDataWrapper.getSpeedTestMetaData()
                                                       .stream()
                                                       .collect(toMap(SpeedTestMetaData::getIdentifier, Function.identity()));
    }

    public SpeedTestMetaData getSpeedTestWebSiteFromIdentifier(String identifier) {
        return speedTestMetaDataMap.get(identifier);
    }

    @Data
    @NoArgsConstructor
    private static class SpeedTestMetaDataWrapper {

        private List<SpeedTestMetaData> speedTestMetaData;
    }
}
