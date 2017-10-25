package com.caseyellow.server.central.domain.analyzer.nonflash;

import com.caseyellow.server.central.exceptions.AnalyzerException;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AtntNonFlashAnalyzer implements NonFlashAnalyzer {

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
}
