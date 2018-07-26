package com.caseyellow.server.central.domain.mail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private String userName;
    private boolean enabled;
    private String phone;

    public User(String user, boolean enabled) {
        this(user, enabled, null);
    }
}
