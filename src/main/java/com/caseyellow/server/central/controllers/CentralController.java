package com.caseyellow.server.central.controllers;

import com.caseyellow.server.central.configuration.GoogleVisionConfiguration;
import com.caseyellow.server.central.domain.analyzer.model.GoogleVisionKey;
import com.caseyellow.server.central.domain.file.model.FileDownloadProperties;
import com.caseyellow.server.central.domain.test.model.FailedTestDetails;
import com.caseyellow.server.central.domain.test.model.PreSignedUrl;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.test.model.UserDetails;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestMetaData;
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

/**
 * Created by writeToFile on 6/25/17.
 */
@RestController
@RequestMapping("/central")
public class CentralController {

    private Logger logger = Logger.getLogger(CentralController.class);

    private TestService testService;
    private FileDownloadService fileDownloadService;
    private SpeedTestWebSiteService speedTestWebSiteService;
    private GoogleVisionConfiguration googleVisionConfiguration;

    @Autowired
    public CentralController(TestService testService,
                             FileDownloadService fileDownloadService,
                             SpeedTestWebSiteService speedTestWebSiteService,
                             GoogleVisionConfiguration googleVisionConfiguration) {
        this.testService = testService;
        this.fileDownloadService = fileDownloadService;
        this.speedTestWebSiteService = speedTestWebSiteService;
        this.googleVisionConfiguration = googleVisionConfiguration;
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

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/next-urls",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FileDownloadProperties> getFileDownloadMetaData() {
        logger.info("Received getFileDownloadMetaData GET request");
        return fileDownloadService.getNextFileDownloadMetaData();
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
    @GetMapping("/all-tests")
    public List<Test> getAllTests() {
        logger.info("Received getAllTests GET request");
        return testService.getAllTests();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/google-vision-key",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public GoogleVisionKey googleVisionKey() {
        logger.info("Received GoogleVisionKey GET request");
        return new GoogleVisionKey(googleVisionConfiguration.googleVisionKey());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/pre-signed-url")
    public PreSignedUrl generatePreSignedUrl(@RequestParam("file_key")String fileKey) {
        logger.info("Received generatePreSignedUrl GET request for file key: " + fileKey);
        return testService.generatePreSignedUrl(fileKey);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/failed-test")
    public void failedTest(@RequestBody FailedTestDetails failedTestDetails) throws IOException {
        logger.info("Received failedTest POST request with failed test : " + failedTestDetails);
        testService.failedTest(failedTestDetails);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/connection-details")
    private Map<String, List<String>> connectionDetails() {
        logger.info("Received getConnectionDetails GET request with");
        return testService.getConnectionDetails();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/save-user-details")
    public void saveUserDetails(@RequestBody UserDetails userDetails) {
        logger.info("Received saveConnectionDetails POST request with user details: " + userDetails);
        testService.saveUserDetails(userDetails);
    }

}