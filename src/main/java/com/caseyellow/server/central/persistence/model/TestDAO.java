package com.caseyellow.server.central.persistence.model;

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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "test_id")
    private List<ComparisonInfoDAO> comparisonInfoDAOTests;

    public TestDAO() {}

    private TestDAO(TestBuilder testBuilder) {
        this.testID = testBuilder.testID;
        this.systemInfo = testBuilder.systemInfo;
        this.speedTestWebsiteIdentifier = testBuilder.speedTestWebsite;
        this.comparisonInfoDAOTests = testBuilder.comparisonInfoDAOTests;
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

    public List<ComparisonInfoDAO> getComparisonInfoDAOTests() {
        return comparisonInfoDAOTests;
    }

    public void setComparisonInfoDAOTests(List<ComparisonInfoDAO> speedTests) {
        this.comparisonInfoDAOTests = speedTests;
    }

    public void addComparisonInfo(ComparisonInfoDAO comparisonInfoDAO) {
        comparisonInfoDAOTests.add(comparisonInfoDAO);
    }



    public static class TestBuilder {

        private String testID;
        private SystemInfoDAO systemInfo;
        private String speedTestWebsite;
        private List<ComparisonInfoDAO> comparisonInfoDAOTests;

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

        public TestBuilder addComparisonInfoTests(List<ComparisonInfoDAO> comparisonInfoDAOTests) {
            this.comparisonInfoDAOTests = comparisonInfoDAOTests;
            return this;
        }

        public TestDAO build() {
            return new TestDAO(this);
        }
    }
}
