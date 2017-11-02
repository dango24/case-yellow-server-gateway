package com.caseyellow.server.central.domain.analyzer;

import com.caseyellow.server.central.domain.analyzer.nonflash.AtntNonFlashAnalyzer;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class AtntNonFlashAnalyzerTest {

    private static AtntNonFlashAnalyzer atntNonFlashAnalyzer;

    @BeforeClass
    public static void setUp() throws Exception {
        atntNonFlashAnalyzer = new AtntNonFlashAnalyzer(null);
    }

    @Test
    public void analyze() throws Exception {
        double actualResult = atntNonFlashAnalyzer.analyze("Your Download speed is 124.9 Mega bits per second and your Upload speed is 6.07Mega bits per second");

        assertEquals("124.9", String.valueOf(actualResult));
    }

}