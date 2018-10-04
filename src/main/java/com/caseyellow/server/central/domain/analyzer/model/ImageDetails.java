package com.caseyellow.server.central.domain.analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDetails {

    private String path;
    private String user;
    private String identifier;
    private String md5;
}
