package com.caseyellow.server.central.controllers;

import com.caseyellow.server.central.configuration.GoogleVisionConfiguration;
import com.caseyellow.server.central.domain.analyzer.model.GoogleVisionKey;
import com.caseyellow.server.central.domain.file.model.FileDownloadMetaData;
import com.caseyellow.server.central.domain.test.model.PreSignedUrl;
import com.caseyellow.server.central.domain.test.model.Test;
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
    @GetMapping(value = "/google-vision-key",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public GoogleVisionKey googleVisionKey() {
        logger.info("Received GoogleVisionKey GET request");
        return new GoogleVisionKey(googleVisionConfiguration.googleVisionKey());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/next-urls",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FileDownloadMetaData> getFileDownloadMetaData(@RequestParam("num_of_comparison_per_test") int numOfComparisonPerTest) {
        logger.info("Received getFileDownloadMetaData GET request with num_of_comparison_per_test: " + numOfComparisonPerTest);
        return fileDownloadService.getNextFileDownloadMetaData(numOfComparisonPerTest);
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
    @GetMapping("/pre-signed-url")
    public PreSignedUrl generatePreSignedUrl(@RequestParam("user_ip")String userIP, @RequestParam("file_name")String fileName) {
        return testService.generatePreSignedUrl(userIP, fileName);
    }

}