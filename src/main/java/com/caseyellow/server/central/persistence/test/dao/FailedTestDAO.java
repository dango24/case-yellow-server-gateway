package com.caseyellow.server.central.persistence.test.dao;

import javax.persistence.*;

@Entity
@Table(name = "failed_test")
public class FailedTestDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String ip;
    private String path;
    private String errorMessage;

    @Column(name = "client_version")
    private String clientVersion;

    @Column(name = "user_name")
    private String user;

    @Column(name = "timestamp")
    private Long timestamp;

    public FailedTestDAO() {
    }

    private FailedTestDAO(String ip, String path, String errorMessage) {
        this.path = path;
        this.errorMessage = errorMessage;
        this.ip = ip;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
