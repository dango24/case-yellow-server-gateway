package com.caseyellow.server.central.domain.analyzer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalyzedImage {

    public String message;
    public String path;
    public boolean analyzed;
    public double result;
}
