package com.caseyellow.server.central.controllers;

import com.caseyellow.server.central.domain.analyzer.model.IdentifierDetails;
import com.caseyellow.server.central.domain.statistics.StatisticsAnalyzer;
import com.caseyellow.server.central.domain.mail.User;
import com.caseyellow.server.central.domain.metrics.UserLastTest;
import com.caseyellow.server.central.domain.metrics.UsersLastTest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.caseyellow.server.central.domain.mail.EmailServiceImpl.DATE_FORMAT;

@Slf4j
@RestController
@RequestMapping("/statistics")
public class StatisticController {

    private StatisticsAnalyzer statisticAnalyzer;

    @Autowired
    public StatisticController(StatisticsAnalyzer statisticAnalyzer) {
        this.statisticAnalyzer = statisticAnalyzer;
    }

    @GetMapping("/count-ips")
    public Map<String, Long> countIPs() {
        log.info("Received countIPs GET request");
        return statisticAnalyzer.countIPs();
    }

    @GetMapping("/count-user-tests")
    public Map<String, Long> countUserTests() {
        log.info("Received countIPs GET request");
        return statisticAnalyzer.countUserTests();
    }

    @GetMapping("/identifiers-details")
    public Map<String, IdentifierDetails> identifiersDetails(@RequestParam(value = "filter", required = false)  String filter) {
        log.info("Received identifiersDetails GET request");
        return identifiersDetailsByUser("all", filter);
    }

    @GetMapping("/identifiers-details/{user}")
    public Map<String, IdentifierDetails> identifiersDetailsByUser(@PathVariable("user")String user,
                                                                   @RequestParam(value = "filter", required = false)  String filter) {
        log.info(String.format("Received identifiersDetails GET request for user: %s", user));

        if (StringUtils.isNotEmpty(filter)) {
            return statisticAnalyzer.createIdentifiersDetails(user, filter, null);
        } else {
            return statisticAnalyzer.getIdentifiersDetails(user);
        }
    }

    @GetMapping("/file-download-rate-mean")
    public Map<String, String> fileDownloadRateMean() {
        log.info("Received fileDownloadRateMean GET request");
        return statisticAnalyzer.meanFileDownloadRate();
    }

    @GetMapping("/user-last-test")
    public UserLastTest userLastTest(@RequestParam("user") String user) {
        log.info(String.format("Received userLastTest GET request, for user: %s", user));
        long lastTestTimestamp = statisticAnalyzer.userLastTest(user);

        return new UserLastTest(DATE_FORMAT.format(new Date(lastTestTimestamp)));
    }

    @PostMapping("/users-last-test")
    public UsersLastTest usersLastTest(@RequestParam("period") int lastTimeInHours) {
        log.info(String.format("Received usersLastTest GET request with last time in hours %s", lastTimeInHours));
        UsersLastTest usersLastTest = statisticAnalyzer.usersLastTest(lastTimeInHours);

        return usersLastTest;
    }

    @PostMapping("/users-statistics")
    public void usersStatistics(@RequestBody List<User> users) {
        log.info(String.format("Received buildUsersStatistics POST request with users %s", users.stream().map(User::getUserName).collect(Collectors.joining(", "))));
        CompletableFuture.runAsync( () -> statisticAnalyzer.buildUsersStatistics(users) );
    }

    @PostMapping("/build-all-tests")
    public void buildAllTests() {
        log.info(String.format("Received buildAllTests POST request with users"));
        CompletableFuture.runAsync( () -> statisticAnalyzer.buildAllTests() );
    }

    @GetMapping("/user-mean-rate")
    public Map<String, String> userMeanRate(@RequestParam("user") String user) {
        log.info(String.format("Received userMeanRate GET request for user %s", user));
        return statisticAnalyzer.getUserMeanRate(user);
    }

    @GetMapping("/user-last-failed-test")
    public UserLastTest userLastFailedTest(@RequestParam("user") String user) {
        log.info(String.format("Received userLastFailedTest GET request, for user: %s", user));
        long lastTestTimestamp = statisticAnalyzer.userLastFailedTest(user);

        return new UserLastTest(DATE_FORMAT.format(new Date(lastTestTimestamp)));
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/notify-last-tests")
    public void sendReports() {
        log.info(String.format("Received sendReports POST request"));
        CompletableFuture.runAsync( () -> statisticAnalyzer.sendReports() );
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/start-test",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void startTest(@RequestBody StartTestDetails startTestDetails) {
        log.info(String.format("Received startTest POST request with startTestDetails: %s", startTestDetails));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class StartTestDetails {

        private String user;
        private String identifier;
        private List<String> urls;
    }
}
