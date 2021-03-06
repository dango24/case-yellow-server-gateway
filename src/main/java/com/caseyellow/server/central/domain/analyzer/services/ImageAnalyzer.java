package com.caseyellow.server.central.domain.analyzer.services;

public interface ImageAnalyzer {
    void updateAnalyzedImageResult(String imagePath, double analyzedImageResult, boolean analyzedSucceed);
    void checkUnAnalyzedTests(int periodInDays, int analyzedStateCode);
    void changeImageAnalyzeState(String s3Path, int analyzeState);
}
