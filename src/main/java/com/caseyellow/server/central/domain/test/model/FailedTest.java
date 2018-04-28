package com.caseyellow.server.central.domain.test.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FailedTest {

    private String ip;
    private String path;
    private String user;
    private String identifier;
    private String errorMessage;
    private String clientVersion;
}
