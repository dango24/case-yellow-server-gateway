package com.caseyellow.server.central.domain.analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzedImage {

    private String message;
    private boolean analyzed;
    private double result;
}
