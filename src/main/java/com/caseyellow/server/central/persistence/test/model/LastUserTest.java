package com.caseyellow.server.central.persistence.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.caseyellow.server.central.domain.mail.EmailServiceImpl.DATE_FORMAT;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LastUserTest {

    private String user;
    private long timestamp;

    @Override
    public String toString() {
        return String.format("%s last test: %s", user, DATE_FORMAT.format(timestamp));
    }
}