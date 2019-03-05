package com.caseyellow.server.central.persistence.test.repository;

import com.caseyellow.server.central.persistence.test.dao.TestDAO;
import com.caseyellow.server.central.persistence.test.model.LastUserTest;
import com.caseyellow.server.central.persistence.test.model.UserTestCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by dango on 9/18/17.
 */
public interface TestRepository extends JpaRepository<TestDAO, Long> {

    String LAST_USER_TEST_QUERY = "select max(t.timestamp) from TestDAO t where t.user = :user";
    String LAST_USERS_TEST_QUERY = "select new com.caseyellow.server.central.persistence.test.model.LastUserTest(t.user, max(t.timestamp)) from TestDAO t group by user";
    String COUNT_USER_TESTS = "select new com.caseyellow.server.central.persistence.test.model.UserTestCount(t.user, COUNT(t)) FROM TestDAO t GROUP BY t.user";
    String COUNT_USER_CONNECTION = "select  count(*) from TestDAO t where t.user = :user and connection = :connection";

    @Query(LAST_USER_TEST_QUERY)
    long lastUserFailedTest(@Param("user") String user);

    @Query(COUNT_USER_TESTS)
    List<UserTestCount> countUserTests();

    @Query(COUNT_USER_CONNECTION)
    long userConnectionCount(@Param("user")String user, @Param("connection")String connection);

    @Query(LAST_USERS_TEST_QUERY)
    List<LastUserTest> getAllUsersLastTests();


    TestDAO findByTestID(String testId);

    List<TestDAO> findByUser(String user);

    Page<TestDAO> findByUser(String user, Pageable pageRequest);
}