package com.caseyellow.server.central.domain.analyzer.nonflash;

import com.caseyellow.server.central.exceptions.AnalyzerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SpeedofNonFlashAnalyzer implements NonFlashAnalyzer {

    private NonFlashAnalyzerSupplier nonFlashAnalyzerSupplier;

    @Autowired
    public SpeedofNonFlashAnalyzer(NonFlashAnalyzerSupplier nonFlashAnalyzerSupplier) {
        this.nonFlashAnalyzerSupplier = nonFlashAnalyzerSupplier;
    }

    @PostConstruct
    private void init() {
        nonFlashAnalyzerSupplier.addNonFlashAnalyzer(this);
    }

    @Override
    public double analyze(String nonFlashResult) {
       try {
           String patternString = "\\d+(:?\\.\\d+)?";

           Pattern pattern = Pattern.compile(patternString);

           Matcher matcher = pattern.matcher(nonFlashResult);

           if (matcher.find()) {
               return Double.valueOf(matcher.group());

           } else {
            throw new AnalyzerException("Failed to analyze fast result with nonFlashResult: " + nonFlashResult);
           }

       } catch (Exception e) {
           throw new AnalyzerException("Failed to analyze fast result with nonFlashResult: " + nonFlashResult);
       }
    }

    @Override
    public String getIdentifier() {
        return "speedof";
    }
}
