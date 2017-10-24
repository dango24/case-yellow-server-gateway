package com.caseyellow.server.central.domain.analyzer;

import com.caseyellow.server.central.CaseYellowCentral;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseYellowCentral.class)
@ActiveProfiles("dev")
public class NonFlashAnalyzerSupplierTest {

    private NonFlashAnalyzerSupplier nonFlashAnalyzerSupplier;

    @Autowired
    public void setNonFlashAnalyzerSupplier(NonFlashAnalyzerSupplier nonFlashAnalyzerSupplier) {
        this.nonFlashAnalyzerSupplier = nonFlashAnalyzerSupplier;
    }

    @Test
    public void getFastNonFlashAnalyzer() throws Exception {
        NonFlashAnalyzer nonFlashAnalyzer = nonFlashAnalyzerSupplier.getNonFlashAnalyzer("fast");
        assertEquals(nonFlashAnalyzer.getClass(), (FastNonFlashAnalyzer.class));
    }

    @Test
    public void getAtntNonFlashAnalyzer() throws Exception {
        NonFlashAnalyzer nonFlashAnalyzer = nonFlashAnalyzerSupplier.getNonFlashAnalyzer("atnt");
        assertEquals(nonFlashAnalyzer.getClass(), (AtntNonFlashAnalyzer.class));
    }

    @Test
    public void getSpeedOfNonFlashAnalyzer() throws Exception {
        NonFlashAnalyzer nonFlashAnalyzer = nonFlashAnalyzerSupplier.getNonFlashAnalyzer("speedof");
        assertEquals(nonFlashAnalyzer.getClass(), (SpeedofNonFlashAnalyzer.class));
    }

}