package com.caseyellow.server.central.persistence.test.repository;

public interface TestLifeCycleRepository {
    void updateTestLifeCycle(String user);
    int getUserTestLifeCycle(String user);
}