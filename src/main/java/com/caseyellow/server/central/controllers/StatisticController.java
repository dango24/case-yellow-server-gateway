package com.caseyellow.server.central.controllers;

import com.caseyellow.server.central.domain.analyzer.model.IdentifierDetails;
import com.caseyellow.server.central.domain.analyzer.services.StatisticsAnalyzer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticController {

    private final Logger logger = Logger.getLogger(StatisticController.class);

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");

    private StatisticsAnalyzer statisticAnalyzer;

    @Autowired
    public StatisticController(StatisticsAnalyzer statisticAnalyzer) {
        this.statisticAnalyzer = statisticAnalyzer;
    }

    @GetMapping("/count-ips")
    public Map<String, Long> countIPs() {
        logger.info("Received countIPs GET request");
        return statisticAnalyzer.countIPs();
    }

    @GetMapping("/identifiers-details")
    public Map<String, IdentifierDetails> identifiersDetails() {
        logger.info("Received identifiersDetails GET request");
        return statisticAnalyzer.createIdentifiersDetails(null);
    }

    @GetMapping("/identifiers-details/{user}")
    public Map<String, IdentifierDetails> identifiersDetailsByUser(@PathVariable("user")String user) {
        logger.info(String.format("Received identifiersDetails GET request for user: %s", user));
        return statisticAnalyzer.createIdentifiersDetails(user);
    }

    @GetMapping("/user-last-test")
    public UserLastTest userLastTest(@RequestParam("user") String user) {
        logger.info(String.format("Received userLastTest GET request, for user: %s", user));
        long lastTestTimestamp = statisticAnalyzer.userLastTest(user);

        return new UserLastTest(dateFormat.format(new Date(lastTestTimestamp)));
    }

    @GetMapping("/user-last-failed-test")
    public UserLastTest userLastFailedTest(@RequestParam("user") String user) {
        logger.info(String.format("Received userLastFailedTest GET request, for user: %s", user));
        long lastTestTimestamp = statisticAnalyzer.userLastFailedTest(user);

        return new UserLastTest(dateFormat.format(new Date(lastTestTimestamp)));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UserLastTest {

        private String last_test;
    }
}
