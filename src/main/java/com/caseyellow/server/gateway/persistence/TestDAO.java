package com.caseyellow.server.gateway.persistence;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Dan on 24/10/2016.
 */
@Entity
@Table(name = "test")
public class TestDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String testID;

    @Embedded
    private SystemInfoDAO systemInfo;

    private String speedTestWebsiteIdentifier;

    private List<ComparisonInfo> comparisonInfoTests;

    public TestDAO() {}

    private TestDAO(TestBuilder testBuilder) {
        this.testID = testBuilder.testID;
        this.systemInfo = testBuilder.systemInfo;
        this.speedTestWebsiteIdentifier = testBuilder.speedTestWebsite;
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

    public SystemInfoDAO getSystemInfo() {
        return systemInfo;
    }

    public void setSystemInfo(SystemInfoDAO systemInfo) {
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



    public static class TestBuilder {

        private String testID;
        private SystemInfoDAO systemInfo;
        private String speedTestWebsite;
        private List<ComparisonInfo> comparisonInfoTests;

        public TestBuilder(String testID) {
            this.testID = testID;
        }

        public TestBuilder addSystemInfo(SystemInfoDAO systemInfo) {
            this.systemInfo = systemInfo;
            return this;
        }

        public TestBuilder addSpeedTestWebsite(String speedTestWebsite) {
            this.speedTestWebsite = speedTestWebsite;
            return this;
        }

        public TestBuilder addComparisonInfoTests(List<ComparisonInfo> comparisonInfoTests) {
            this.comparisonInfoTests = comparisonInfoTests;
            return this;
        }

        public TestDAO build() {
            return new TestDAO(this);
        }
    }
}
