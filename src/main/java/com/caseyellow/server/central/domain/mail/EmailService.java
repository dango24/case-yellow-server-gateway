package com.caseyellow.server.central.domain.mail;

import com.caseyellow.server.central.persistence.test.model.LastUserTest;

import java.util.List;

public interface EmailService {
    void sendEmails(List<LastUserTest> lastUserTests);
}
