package com.caseyellow.server.central.domain.test.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by Dan on 24/10/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Test {

    private String testID;
    private String user;
    private String clientVersion;
    private Long startTime;
    private Long endTime;
    private SystemInfo systemInfo;
    private String speedTestWebsiteIdentifier;
    private List<ComparisonInfo> comparisonInfoTests;

    public Test() {
    }

    private Test(TestBuilder testBuilder) {
        this.testID = testBuilder.testID;
        this.user = testBuilder.user;
        this.startTime = testBuilder.startTime;
        this.endTime = testBuilder.endTime;
        this.systemInfo = testBuilder.systemInfo;
        this.clientVersion = testBuilder.clientVersion;
        this.speedTestWebsiteIdentifier = testBuilder.speedTestWebsiteIdentifier;
        this.comparisonInfoTests = testBuilder.comparisonInfoTests;
    }


    public String getTestID() {
        return testID;
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }

    public String getSpeedTestWebsiteIdentifier() {
        return speedTestWebsiteIdentifier;
    }

    public void setSpeedTestWebsiteIdentifier(String speedTestWebsiteIdentifier) {
        this.speedTestWebsiteIdentifier = speedTestWebsiteIdentifier;
    }

    public SystemInfo getSystemInfo() {
        return systemInfo;
    }

    public void setSystemInfo(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    public List<ComparisonInfo> getComparisonInfoTests() {
        return comparisonInfoTests;
    }

    public void setComparisonInfoTests(List<ComparisonInfo> speedTests) {
        this.comparisonInfoTests = speedTests;
    }

    public void addComparisonInfo(ComparisonInfo comparisonInfo) {
        comparisonInfoTests.add(comparisonInfo);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Test{" +
                "testID='" + testID + '\'' +
                ", user='" + user + '\'' +
                ", clientVersion='" + clientVersion + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", systemInfo=" + systemInfo +
                ", speedTestWebsiteIdentifier='" + speedTestWebsiteIdentifier + '\'' +
                ", comparisonInfoTests=" + comparisonInfoTests +
                '}';
    }

    // TestBuilder Helper
    public static class TestBuilder {

        // Fields
        private String testID;
        private String user;
        private String clientVersion;
        private Long startTime;
        private Long endTime;
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

        public Test build() {
            return new Test(this);
        }
    }
}
