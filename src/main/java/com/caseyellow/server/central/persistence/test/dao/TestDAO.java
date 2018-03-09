package com.caseyellow.server.central.persistence.test.dao;

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

    @Column(name = "user_name")
    private String user;

    private String speedTestWebsiteIdentifier;

    @Column(name = "timestamp")
    private Long timestamp;

    @Column(name = "client_version")
    private String clientVersion;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "test_id")
    private List<ComparisonInfoDAO> comparisonInfoDAOTests;

    public TestDAO() {}

    private TestDAO(TestBuilder testBuilder) {
        this.testID = testBuilder.testID;
        this.user = testBuilder.user;
        this.systemInfo = testBuilder.systemInfo;
        this.clientVersion = testBuilder.clientVersion;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public static class TestBuilder {

        private String testID;
        private String user;
        private String clientVersion;
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

        public TestBuilder addUser(String user) {
            this.user = user;
            return this;
        }

        public TestBuilder addComparisonInfoTests(List<ComparisonInfoDAO> comparisonInfoDAOTests) {
            this.comparisonInfoDAOTests = comparisonInfoDAOTests;
            return this;
        }

        public TestBuilder addClientVersion(String clientVersion) {
            this.clientVersion = clientVersion;
            return this;
        }

        public TestDAO build() {
            return new TestDAO(this);
        }
    }
}
