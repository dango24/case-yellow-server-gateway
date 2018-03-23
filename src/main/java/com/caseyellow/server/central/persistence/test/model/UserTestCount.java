package com.caseyellow.server.central.persistence.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTestCount {

    private String userName;
    private long count;
}
