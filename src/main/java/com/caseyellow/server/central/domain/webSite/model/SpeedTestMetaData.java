package com.caseyellow.server.central.domain.webSite.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpeedTestMetaData {

    private String webSiteUrl;
    private String identifier;
    private int centralized;
    private boolean flashAble;
    private boolean haveStartButton;
    private SpeedTestFlashMetaData speedTestFlashMetaData;
    private SpeedTestNonFlashMetaData speedTestNonFlashMetaData;
    private List<Role> roles;
    private List<Role> preStartButtonRoles;
}
