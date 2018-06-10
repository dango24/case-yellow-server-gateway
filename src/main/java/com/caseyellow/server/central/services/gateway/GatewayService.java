package com.caseyellow.server.central.services.gateway;

import com.caseyellow.server.central.services.gateway.model.User;

import java.util.List;

public interface GatewayService {

    List<User> getUsers();
}
