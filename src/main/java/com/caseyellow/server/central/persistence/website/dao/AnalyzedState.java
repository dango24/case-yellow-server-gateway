package com.caseyellow.server.central.persistence.website.dao;

import java.util.stream.Stream;

public enum AnalyzedState {
    SUCCESS(0),
    FAILURE(1),
    NOT_STARTED(2),
    REANALYZE(3);


    AnalyzedState(int value) {
        this.value = value;
    }

    private int value;

    public static AnalyzedState getAnalyzedState(int analyzeStateValue){

        return Stream.of(AnalyzedState.values())
                     .filter(analyzedState -> analyzedState.getValue() == analyzeStateValue)
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException(String.format("There is no AnalyzeState for giving value: %s", analyzeStateValue)));
    }

    public int getValue() {
        return value;
    }
}
