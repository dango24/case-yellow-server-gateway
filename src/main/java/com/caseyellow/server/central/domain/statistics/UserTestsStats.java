package com.caseyellow.server.central.domain.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTestsStats implements Comparable<UserTestsStats> {

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
        return name.toLowerCase().compareTo(o.name.toLowerCase());
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
