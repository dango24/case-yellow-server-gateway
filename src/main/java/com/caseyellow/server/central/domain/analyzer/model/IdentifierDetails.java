package com.caseyellow.server.central.domain.analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdentifierDetails {

    private String identifier;
    private double meanRatio;
    private int size;
}
