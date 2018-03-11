package com.caseyellow.server.central.domain.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails {

    private String userName;
    private int speed;
    private String isp;
    private String infrastructure;
}
