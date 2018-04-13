package com.caseyellow.server.central.domain.analyzer.services;

public interface ImageAnalyzer {
    void updateAnalyzedImageByPath(String imagePath, double analyzedImageResult);
}
