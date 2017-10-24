package com.caseyellow.server.central.domain.analyzer;

public interface NonFlashAnalyzerService {
    double analyze(String identifier, String nonFlashResult);
    boolean isNonFlashAble(String identifier);
}
