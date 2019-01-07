package com.caseyellow.server.central.persistence.user.repository;


import com.caseyellow.server.central.persistence.user.model.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDAO, Long> {

    UserDAO findByUserName(String userName);
}
