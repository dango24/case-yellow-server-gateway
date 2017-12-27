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
}
