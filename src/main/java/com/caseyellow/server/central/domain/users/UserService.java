package com.caseyellow.server.central.domain.users;


import com.caseyellow.server.central.persistence.user.model.UserDAO;
import java.util.List;

public interface UserService {
    List<UserDAO> getAllUsers();
    UserDAO getUser(String userName);
}
