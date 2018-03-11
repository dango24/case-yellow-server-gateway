package com.caseyellow.server.central.domain.analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    private String content; // Image In Base64;
    private String md5;

    public Image(String content) {
       this(content, null);
    }
}
