package com.caseyellow.server.central.domain.test.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FailedTest {

    private String ip;
    private String path;
    private String errorMessage;
    private String user;

    public FailedTest() {
    }

    private FailedTest(String ip, String path, String errorMessage, String user) {
        this.path = path;
        this.errorMessage = errorMessage;
        this.ip = ip;
        this.user = user;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "FailedTest{" +
                "ip='" + ip + '\'' +
                ", path='" + path + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", user='" + user + '\'' +
                '}';
    }

    public static class FailedTestDetailsBuilder {

        private String ip;
        private String path;
        private String errorMessage;
        private String user;

        public FailedTestDetailsBuilder() {
        }

        public FailedTestDetailsBuilder addIp(String ip) {
            this.ip = ip;
            return this;
        }

        public FailedTestDetailsBuilder addPath(String path) {
            this.path = path;
            return this;
        }

        public FailedTestDetailsBuilder addUser(String user) {
            this.user = user;
            return this;
        }

        public FailedTestDetailsBuilder addErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public FailedTest build() {
            return new FailedTest(ip, path, errorMessage, user);
        }
    }
}
