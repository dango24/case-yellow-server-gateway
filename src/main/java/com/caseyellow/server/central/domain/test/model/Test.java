package com.caseyellow.server.central.domain.test.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Created by Dan on 24/10/2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Test {

    private String testID;
    private String user;
    private String clientVersion;
    private String computerIdentifier;
    private String isp;
    private Long startTime;
    private Long endTime;
    private boolean classicTest;
    private SystemInfo systemInfo;
    private String speedTestWebsiteIdentifier;
    private List<ComparisonInfo> comparisonInfoTests;

    public Test() {
    }

    private Test(TestBuilder testBuilder) {
        this.testID = testBuilder.testID;
        this.user = testBuilder.user;
        this.isp = testBuilder.isp;
        this.startTime = testBuilder.startTime;
        this.endTime = testBuilder.endTime;
        this.classicTest = testBuilder.classicTest;
        this.systemInfo = testBuilder.systemInfo;
        this.clientVersion = testBuilder.clientVersion;
        this.speedTestWebsiteIdentifier = testBuilder.speedTestWebsiteIdentifier;
        this.comparisonInfoTests = testBuilder.comparisonInfoTests;
    }

    // TestBuilder Helper
    public static class TestBuilder {

        // Fields
        private String testID;
        private String user;
        private String clientVersion;
        private String isp;
        private Long startTime;
        private Long endTime;
        private boolean classicTest;
        private SystemInfo systemInfo;
        private String speedTestWebsiteIdentifier;
        private List<ComparisonInfo> comparisonInfoTests;

        public TestBuilder(String testID) {
            this.testID = testID;
        }

        public TestBuilder addSystemInfo(SystemInfo systemInfo) {
            this.systemInfo = systemInfo;
            return this;
        }

        public TestBuilder addSpeedTestWebsiteIdentifier(String speedTestWebsite) {
            this.speedTestWebsiteIdentifier = speedTestWebsite;
            return this;
        }

        public TestBuilder addComparisonInfoTests(List<ComparisonInfo> comparisonInfoTests) {
            this.comparisonInfoTests = comparisonInfoTests;
            return this;
        }

        public TestBuilder addClientVersion(String clientVersion) {
            this.clientVersion = clientVersion;
            return this;
        }

        public TestBuilder addUser(String user) {
            this.user = user;
            return this;
        }

        public TestBuilder addStartTime(Long startTime) {
            this.startTime = startTime;
            return this;
        }

        public TestBuilder addEndTime(Long endTime) {
            this.endTime = endTime;
            return this;
        }

        public TestBuilder isClassicTest(boolean classicTest) {
            this.classicTest = classicTest;
            return this;
        }

        public TestBuilder addISP(String isp) {
            this.isp = isp;
            return this;
        }

        public Test build() {
            return new Test(this);
        }
    }
}
