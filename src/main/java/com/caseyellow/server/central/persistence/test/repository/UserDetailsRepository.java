package com.caseyellow.server.central.persistence.test.repository;

import com.caseyellow.server.central.persistence.test.dao.UserDetailsDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailsRepository extends JpaRepository<UserDetailsDAO, Long> {

    UserDetailsDAO findByUserName(String userName);
}
