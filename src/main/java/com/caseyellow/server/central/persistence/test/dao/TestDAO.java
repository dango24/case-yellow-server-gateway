package com.caseyellow.server.central.persistence.test.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Dan on 24/10/2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "start_time")
    private Long startTime;

    @Column(name = "end_time")
    private Long endTime;

    @Column(name = "client_version")
    private String clientVersion;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "test_id")
    private List<ComparisonInfoDAO> comparisonInfoDAOTests;

    private TestDAO(TestBuilder testBuilder) {
        this.testID = testBuilder.testID;
        this.user = testBuilder.user;
        this.startTime = testBuilder.startTime;
        this.endTime = testBuilder.endTime;
        this.systemInfo = testBuilder.systemInfo;
        this.clientVersion = testBuilder.clientVersion;
        this.speedTestWebsiteIdentifier = testBuilder.speedTestWebsite;
        this.comparisonInfoDAOTests = testBuilder.comparisonInfoDAOTests;
    }

    public static class TestBuilder {

        private String testID;
        private String user;
        private String clientVersion;
        private Long startTime;
        private Long endTime;
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

        public TestBuilder addStartTime(Long startTime) {
            this.startTime = startTime;
            return this;
        }

        public TestBuilder addEndTime(Long endTime) {
            this.endTime = endTime;
            return this;
        }

        public TestDAO build() {
            return new TestDAO(this);
        }
    }
}
