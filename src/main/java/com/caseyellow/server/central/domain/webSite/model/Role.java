package com.caseyellow.server.central.domain.webSite.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Role {

    private String identifier;
    private Command command;
    private boolean mono;
    private boolean deprecated;
}
