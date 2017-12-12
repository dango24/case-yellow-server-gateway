package com.caseyellow.server.central.controllers;

import com.caseyellow.server.central.domain.file.model.FileDownloadMetaData;
import com.caseyellow.server.central.domain.test.model.PreSignedUrl;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.test.model.TestWrapper;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestMetaData;
import com.caseyellow.server.central.domain.webSite.services.SpeedTestWebSiteService;
import com.caseyellow.server.central.domain.file.services.FileDownloadService;
import com.caseyellow.server.central.domain.test.services.TestService;
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartRequest;

import java.io.IOException;
import java.util.List;

import static com.caseyellow.server.central.common.Utils.prepareTest;

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

    @Autowired
    public CentralController(TestService testService, FileDownloadService fileDownloadService, SpeedTestWebSiteService speedTestWebSiteService) {
        this.fileDownloadService = fileDownloadService;
        this.speedTestWebSiteService = speedTestWebSiteService;
        this.testService = testService;
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
    @GetMapping(value = "/next-urls",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FileDownloadMetaData> getFileDownloadMetaData(@RequestParam("num_of_comparison_per_test") int numOfComparisonPerTest) {
        logger.info("Received getFileDownloadMetaData GET request with num_of_comparison_per_test: " + numOfComparisonPerTest);
        return fileDownloadService.getNextFileDownloadMetaData(numOfComparisonPerTest);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/save-test")
    public void saveTest(@RequestParam("payload") String payload, @NotEmpty MultipartRequest request) throws IOException {
        logger.info("Received saveTest POST request with test payload: " + payload);
        TestWrapper testWrapper = prepareTest(payload, request);

        testService.saveTest(testWrapper);
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