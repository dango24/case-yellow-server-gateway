package com.caseyellow.server.central.domain.users;

import com.caseyellow.server.central.persistence.user.model.UserDAO;
import com.caseyellow.server.central.persistence.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Profile("prod")
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDAO> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDAO getUser(String userName) {
        return userRepository.findByUserName(userName);
    }

}
