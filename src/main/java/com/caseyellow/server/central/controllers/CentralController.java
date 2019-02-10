package com.caseyellow.server.central.controllers;

import com.caseyellow.server.central.domain.analyzer.services.ImageAnalyzer;
import com.caseyellow.server.central.domain.file.model.FileDownloadProperties;
import com.caseyellow.server.central.domain.logger.LogData;
import com.caseyellow.server.central.domain.logger.LoggerService;
import com.caseyellow.server.central.domain.statistics.StatisticsAnalyzer;
import com.caseyellow.server.central.domain.test.model.FailedTest;
import com.caseyellow.server.central.domain.test.model.PreSignedUrl;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.test.model.UserDetails;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestMetaData;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestNonFlashMetaData;
import com.caseyellow.server.central.domain.webSite.model.WordIdentifier;
import com.caseyellow.server.central.domain.webSite.services.SpeedTestWebSiteService;
import com.caseyellow.server.central.domain.file.services.FileDownloadService;
import com.caseyellow.server.central.domain.test.services.TestService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Created by writeToFile on 6/25/17.
 */
@RestController
@RequestMapping("/central")
public class CentralController {

    private Logger logger = Logger.getLogger(CentralController.class);

    private TestService testService;
    private ImageAnalyzer imageAnalyzer;
    private LoggerService loggerService;
    private FileDownloadService fileDownloadService;
    private StatisticsAnalyzer statisticsAnalyzer;
    private SpeedTestWebSiteService speedTestWebSiteService;

    @Autowired
    public CentralController(TestService testService,
                             FileDownloadService fileDownloadService,
                             StatisticsAnalyzer statisticsAnalyzer,
                             ImageAnalyzer imageAnalyzer,
                             LoggerService loggerService,
                             SpeedTestWebSiteService speedTestWebSiteService) {

        this.testService = testService;
        this.statisticsAnalyzer = statisticsAnalyzer;
        this.fileDownloadService = fileDownloadService;
        this.imageAnalyzer =imageAnalyzer;
        this.loggerService = loggerService;
        this.speedTestWebSiteService = speedTestWebSiteService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/health")
    public Payload health() {
        return new Payload("For fame & glory");
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/next-web-site",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public SpeedTestMetaData getNextSpeedTestWebSite() {
        logger.info("Received getNextSpeedTestWebSite GET request");
        return speedTestWebSiteService.getNextSpeedTestWebSite();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/is-user-exist",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean isUserExist(@RequestParam("user_name") String userName) {
        logger.info("Received isUserExist GET request");
        return testService.isUserExist(userName);
    }

    @GetMapping(value = "/test-life-cycle",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public int getTestLifeCycle(@RequestParam("user_name") String userName) {
        logger.info("Received getTestLifeCycle GET request for user: " + userName);
        return testService.getTestLifeCycle(userName);
    }

    @PostMapping(value = "/update-test-life-cycle",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTestLifeCycle(@RequestParam("user_name") String userName) {
        logger.info("Received updateTestLifeCycle GET request for user: " + userName);
        testService.updateTestLifeCycle(userName);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/next-urls",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FileDownloadProperties> getFileDownloadMetaData() {
        logger.info("Received getFileDownloadMetaData GET request");
        return fileDownloadService.getNextFileDownloadMetaData();
    }

    @GetMapping(value = "/chrome-options-arguments",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getChromeOptionsArguments(@RequestParam("user_name") String user) {
        logger.info(String.format("Received getChromeOptionsArguments GET request from user: %s", user));
        return speedTestWebSiteService.getChromeOptionsArguments(user);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/save-test",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveTest(@RequestBody Test test) throws IOException {
        logger.info("Received saveTest POST request with test : " + test);
        testService.saveTest(test);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping(value = "/all-tests",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Test> getAllTests() {
        logger.info("Received getAllTests GET request");
        return statisticsAnalyzer.getAllTests();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/pre-signed-url",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public PreSignedUrl generatePreSignedUrl(@RequestParam("file_key")String fileKey) {
        logger.info("Received generatePreSignedUrl GET request for file key: " + fileKey);
        return testService.generatePreSignedUrl(fileKey);
    }

    @PostMapping("/investigate-test-ratio")
    public void investigateSuspiciousTestRatio(@RequestParam("outliar_ratio")String outliarRatio,
                                               @RequestParam("hours")String hours) {
        logger.info(String.format("Received investigate-test-ratio POST request with ratioOutliar: %s, hours: %s", outliarRatio, hours));
        speedTestWebSiteService.investigateSuspiciousTestRatio(outliarRatio, hours);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/failed-test",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public void failedTest(@RequestBody FailedTest failedTest) throws IOException {
        logger.info("Received saveFailedTest POST request with failed test : " + failedTest);
        testService.saveFailedTest(failedTest);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/connection-details",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    private Map<String, List<String>> connectionDetails() {
        logger.info("Received getConnectionDetails GET request with");
        return testService.getConnectionDetails();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/save-user-details",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveUserDetails(@RequestBody UserDetails userDetails) {
        logger.info("Received saveConnectionDetails POST request with user details: " + userDetails);
        testService.saveUserDetails(userDetails);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/all-user-tests",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Test> getAllUserTests(@RequestParam("user") String user) {
        logger.info(String.format("Received getAllTestsByUser GET request for user: %s", user));
        return testService.getAllTestsByUser(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/all-user-failed-tests",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FailedTest> getAllUserFailedTests(@RequestParam("user") String user) {
        logger.info(String.format("Received getAllUserFailedTests GET request for user: %s", user));
        return testService.getAllUserFailedTests(user);
    }

    @PostMapping("/upload-log-data")
    public void uploadLogData(@RequestParam("user")String user, @RequestBody LogData logData) {
        logger.info(String.format("Received uploadLogData POST request for user: %s, with logData: %s", user, logData));
        loggerService.uploadLogData(user, logData);
    }

    @GetMapping(value = "/text-identifiers",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<WordIdentifier> getTextIdentifiers(@RequestParam("identifier") String identifier, @RequestParam("startTest") boolean startTest) {
        logger.info(String.format("Received getTextIdentifiers GET request with identifier: %s", identifier));
        return speedTestWebSiteService.getTextIdentifiers(identifier, startTest);
    }

    @GetMapping(value = "/speedtest-non-flash-meta-data",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public SpeedTestNonFlashMetaData getSpeedTestNonFlashMetaData(@RequestParam("identifier")String identifier) {
        logger.info(String.format("Received getSpeedTestNonFlashMetaData GET request with identifier: %s", identifier));
        return speedTestWebSiteService.getSpeedTestNonFlashMetaData(identifier);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/unanalyzed-tests",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void unAnalyzedTests(@RequestParam("period_in_hours")int periodInDays, @RequestParam("analyzed_state_code")int analyzedStateCode) {
        logger.info("Received unAnalyzedTests POST request");
        CompletableFuture.runAsync( () -> imageAnalyzer.checkUnAnalyzedTests(periodInDays, analyzedStateCode) );
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/change-image-analyze-state",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void changeImageAnalyzeState(@RequestParam("s3_path")String s3Path, @RequestParam("analyze_state")int analyzeState) {
        logger.info("Received changeImageAnalyzeState POST request");
        CompletableFuture.runAsync( () -> imageAnalyzer.changeImageAnalyzeState(s3Path, analyzeState) );
    }
}