package com.caseyellow.server.central.domain.analyzer.nonflash;

import com.caseyellow.server.central.exceptions.AnalyzerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class FastNonFlashAnalyzer implements NonFlashAnalyzer {

    private NonFlashAnalyzerSupplier nonFlashAnalyzerSupplier;

    @Autowired
    public FastNonFlashAnalyzer(NonFlashAnalyzerSupplier nonFlashAnalyzerSupplier) {
        this.nonFlashAnalyzerSupplier = nonFlashAnalyzerSupplier;
    }

    @PostConstruct
    private void init() {
        nonFlashAnalyzerSupplier.addNonFlashAnalyzer(this);
    }

    @Override
    public double analyze(String nonFlashResult) {
       try {
           return Double.valueOf(nonFlashResult);

       } catch (Exception e) {
           throw new AnalyzerException("Failed to analyze fast result with nonFlashResult: " + nonFlashResult);
       }
    }

    @Override
    public String getIdentifier() {
        return "fast";
    }
}
