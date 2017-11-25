package com.caseyellow.server.central.services.analyze;

import java.io.File;

public interface ImageAnalyzerService {
    double analyzeImage(String identifier, File image);
    double analyzeNonFlash(String identifier, String nonFlashResult);
}
