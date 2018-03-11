package com.caseyellow.server.central.domain.analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feature {

    private static final String DEFAULT_TYPE = "TEXT_DETECTION";
    private static final String DEFAULT_MAX_RESULT = "10";

    private String type;
    private String maxResults;

    public static Feature createDefaultFeature() {
        Feature feature = new Feature();
        feature.setMaxResults(DEFAULT_MAX_RESULT);
        feature.setType(DEFAULT_TYPE);

        return feature;
    }
}
