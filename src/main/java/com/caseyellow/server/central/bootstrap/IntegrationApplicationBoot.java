package com.caseyellow.server.central.bootstrap;

import com.caseyellow.server.central.common.DAOConverter;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.exceptions.AppBootException;
import com.caseyellow.server.central.persistence.repository.TestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
@Profile("integration")
public class IntegrationApplicationBoot {

    private Logger logger = Logger.getLogger(IntegrationApplicationBoot.class);

    private TestRepository testRepository;

    @Autowired
    public IntegrationApplicationBoot(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @PostConstruct
    private void init() throws AppBootException {
        try {
            addDummyTests();

        } catch (URISyntaxException | IOException e) {
            logger.error("Failed to initialized app, " + e.getMessage(), e);
            throw new AppBootException("Failed to initialized app, " + e.getMessage());
        }
    }

    private void addDummyTests() throws URISyntaxException, IOException {
        Path testsPath = Paths.get(IntegrationApplicationBoot.class.getResource("/dummy_tests.json").toURI());

        new ObjectMapper().readValue(testsPath.toFile(), DummyTestsWrapper.class)
                          .getTests()
                          .stream()
                          .map(DAOConverter::convertTestToTestDAO)
                          .forEach(testRepository::save);
    }

    private static class DummyTestsWrapper {

        private List<Test> tests;

        public DummyTestsWrapper() {
        }

        public List<Test> getTests() {
            return tests;
        }

        public void setTests(List<Test> tests) {
            this.tests = tests;
        }
    }
}
