package com.caseyellow.server.central.domain.mail;

import java.util.List;

public interface EmailService {
    void sendNotification(List<User> users);
}
