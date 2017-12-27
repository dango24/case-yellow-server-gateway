package com.caseyellow.server.central.persistence.test.repository;

import com.caseyellow.server.central.persistence.test.dao.FailedTestDAO;
import com.caseyellow.server.central.persistence.test.dao.TestDAO;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by dango on 9/18/17.
 */
public interface FailedTestRepository extends JpaRepository<FailedTestDAO, Long> {

}
