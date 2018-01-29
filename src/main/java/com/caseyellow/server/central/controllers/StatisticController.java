package com.caseyellow.server.central.controllers;

import com.caseyellow.server.central.domain.analyzer.model.IdentifierDetails;
import com.caseyellow.server.central.domain.analyzer.services.StatisticsAnalyzer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticController {

    private Logger logger = Logger.getLogger(StatisticController.class);

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
        return statisticAnalyzer.createIdentifiersDetails();
    }
}
