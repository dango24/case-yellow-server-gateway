package com.caseyellow.server.central.domain.analyzer.nonflash;

import com.caseyellow.server.central.exceptions.AnalyzerException;
import org.springframework.stereotype.Component;

@Component
public class FastNonFlashAnalyzer implements NonFlashAnalyzer {

    @Override
    public double analyze(String nonFlashResult) {
       try {
           return Double.valueOf(nonFlashResult);

       } catch (Exception e) {
           throw new AnalyzerException("Failed to analyze fast result with nonFlashResult: " + nonFlashResult);
       }
    }
}
