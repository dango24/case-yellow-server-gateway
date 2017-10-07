package com.caseyellow.server.central.domain.webSite.services;

import com.caseyellow.server.central.domain.webSite.model.SpeedTestMetaData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Component
public class SpeedTestWebSiteFactory {

    private final String SPEED_TEST_METADATA_LOCATION = "/speed_test_meta_data.json";

    private Map<String, SpeedTestMetaData> speedTestMetaDataMap;

    @PostConstruct
    private void init() throws URISyntaxException, IOException {
        Path speedTestMetaData = Paths.get(SpeedTestWebSiteFactory.class.getResource(SPEED_TEST_METADATA_LOCATION).toURI());
        SpeedTestMetaDataWrapper speedTestMetaDataWrapper = new ObjectMapper().readValue(speedTestMetaData.toFile(), SpeedTestMetaDataWrapper.class);

        speedTestMetaDataMap = speedTestMetaDataWrapper.getSpeedTestMetaData()
                                                       .stream()
                                                       .collect(toMap(SpeedTestMetaData::getIdentifier, Function.identity()));
    }

    public SpeedTestMetaData getSpeedTestWebSiteFromIdentifier(String identifier) {
        return speedTestMetaDataMap.get(identifier);
    }

    private static class SpeedTestMetaDataWrapper {

        private List<SpeedTestMetaData> speedTestMetaData;

        public SpeedTestMetaDataWrapper() {
        }

        public List<SpeedTestMetaData> getSpeedTestMetaData() {
            return speedTestMetaData;
        }

        public void setSpeedTestMetaData(List<SpeedTestMetaData> speedTestMetaData) {
            this.speedTestMetaData = speedTestMetaData;
        }
    }
}
