package com.caseyellow.server.central.domain.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailUser {

    private String name;
    private String email;
    private String role;
}
