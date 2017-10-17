package com.caseyellow.server.central.domain.test.services;

import com.caseyellow.server.central.CaseYellowCentral;
import com.caseyellow.server.central.domain.test.model.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseYellowCentral.class)
@ActiveProfiles("dev")
public class CentralServiceImpTest {

    private static final String DUMMY_TEST_LOCATION = "/dummy_tests.json";

    @org.junit.Test
    public void saveTest() throws Exception {
        Path testPath = Paths.get(CentralServiceImpTest.class.getResource(DUMMY_TEST_LOCATION).toURI());
        TestWrapper testWrapper = new ObjectMapper().readValue(testPath.toFile(), TestWrapper.class);

        assertNotNull(testWrapper.getTests());
    }

    public static class TestWrapper {

        private List<Test> tests;

        public TestWrapper() {}

        public List<Test> getTests() {
            return tests;
        }

        public void setTests(List<Test> tests) {
            this.tests = tests;
        }
    }

}