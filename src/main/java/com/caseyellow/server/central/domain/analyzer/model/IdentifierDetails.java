package com.caseyellow.server.central.domain.analyzer.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBDocument
public class IdentifierDetails {

    private String identifier;
    private double meanRatio;
    private int size;
}
