package com.caseyellow.server.central.domain.metrics;

import com.caseyellow.server.central.persistence.test.model.LastUserTest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsersLastTest {

    @JsonProperty("users_count")
    private int usersCount;

    @JsonProperty("missing_users_count")
    private int missingUsersCount;

    private List<LastUserTest> usersLastTests;
    private List<LastUserTest> missingUsers;

    public int getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(int usersCount) {
        this.usersCount = usersCount;
    }

    public int getMissingUsersCount() {
        return missingUsersCount;
    }

    public void setMissingUsersCount(int missingUsersCount) {
        this.missingUsersCount = missingUsersCount;
    }

    public List<LastUserTest> getUsersLastTests() {
        return usersLastTests;
    }

    public void setUsersLastTests(List<LastUserTest> usersLastTests) {
        this.usersLastTests = usersLastTests;
    }

    public List<LastUserTest> getMissingUsers() {
        return missingUsers;
    }

    public void setMissingUsers(List<LastUserTest> missingUsers) {
        this.missingUsers = missingUsers;
    }

    @Override
    public String toString() {
        return "{" +
                "usersCount=" + usersCount +
                ", missingUsersCount=" + missingUsersCount +
                ", usersLastTests=" + usersLastTests +
                ", missingUsers=" + missingUsers +
                '}';
    }
}
