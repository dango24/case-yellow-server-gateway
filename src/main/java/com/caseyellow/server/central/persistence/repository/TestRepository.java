package com.caseyellow.server.central.persistence.repository;

import com.caseyellow.server.central.persistence.model.TestDAO;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by dango on 9/18/17.
 */
public interface TestRepository extends CrudRepository<TestDAO, Long> {
}
