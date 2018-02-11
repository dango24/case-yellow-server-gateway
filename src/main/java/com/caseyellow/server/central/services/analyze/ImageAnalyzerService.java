package com.caseyellow.server.central.services.analyze;

import com.caseyellow.server.central.domain.analyzer.model.GoogleVisionRequest;

public interface ImageAnalyzerService {
    double analyzeImage(String identifier, GoogleVisionRequest googleVisionRequest);
}
