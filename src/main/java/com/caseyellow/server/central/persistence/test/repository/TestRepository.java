package com.caseyellow.server.central.persistence.test.repository;

import com.caseyellow.server.central.persistence.test.dao.TestDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by dango on 9/18/17.
 */
public interface TestRepository extends JpaRepository<TestDAO, Long> {

    String LAST_USER_TEST_QUERY = "select max(t.timestamp) from TestDAO t where t.user = :user";

    @Query(LAST_USER_TEST_QUERY)
    long lastUserFailedTest(@Param("user") String user);

    TestDAO findByTestID(String testId);
}
