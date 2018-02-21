package com.caseyellow.server.central.persistence.test.repository;

import com.caseyellow.server.central.persistence.test.dao.FailedTestDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by dango on 9/18/17.
 */
public interface FailedTestRepository extends JpaRepository<FailedTestDAO, Long> {

    String LAST_USER_FAILED_TEST_QUERY = "select max(ft.timestamp) from FailedTestDAO ft where ft.user = :user";

    @Query(LAST_USER_FAILED_TEST_QUERY)
    long lastUserFailedTest(@Param("user") String user);
}
