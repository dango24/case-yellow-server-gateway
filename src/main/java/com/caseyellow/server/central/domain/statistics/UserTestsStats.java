package com.caseyellow.server.central.domain.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTestsStats implements Comparable<UserTestsStats> {

    public static final int TEST_COUNT_PER_CONNECTION = 2000;

    private String name;
    private boolean isActive;
    private long lanCount;
    private long wifiCount;

    private UserTestsStats(UserTestsCountBuilder userTestsCountBuilder) {
        this.name = userTestsCountBuilder.getName();
        this.isActive = userTestsCountBuilder.isActive();
        this.lanCount = userTestsCountBuilder.getLanCount();
        this.wifiCount = userTestsCountBuilder.getWifiCount();
    }

    public static UserTestsCountBuilder createUserTestsCountBuilder(String user, boolean enable) {
        return UserTestsCountBuilder.createUserTestsCountBuilder(user, enable);
    }

    @Override
    public int compareTo(UserTestsStats o) {
        return getRemainingTests(this) - getRemainingTests(o);
    }

    private int getRemainingTests(UserTestsStats userTestsStats) {
        long remainingTests = (TEST_COUNT_PER_CONNECTION*2) -
                (Math.min(TEST_COUNT_PER_CONNECTION, userTestsStats.getLanCount()) + Math.min(TEST_COUNT_PER_CONNECTION, userTestsStats.getWifiCount()));
        return Math.toIntExact(remainingTests);
    }

    @Data
    public static class UserTestsCountBuilder {

        private String name;
        private boolean isActive;
        private long lanCount;
        private long wifiCount;

        private static UserTestsCountBuilder createUserTestsCountBuilder(String user, boolean enable) {
            UserTestsCountBuilder userTestsCountBuilder = new UserTestsCountBuilder();
            userTestsCountBuilder.setName(user);
            userTestsCountBuilder.setActive(enable);

            return userTestsCountBuilder;
        }

        public UserTestsCountBuilder addLanCount(long lanCount) {
            this.lanCount = lanCount;
            return this;
        }

        public UserTestsCountBuilder addWifiCount(long wifiCount) {
            this.wifiCount = wifiCount;
            return this;
        }

        public UserTestsStats build() {
            return new UserTestsStats(this);
        }
    }
}
