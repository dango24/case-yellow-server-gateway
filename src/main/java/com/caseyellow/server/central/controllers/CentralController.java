package com.caseyellow.server.central.controllers;

import com.caseyellow.server.central.domain.webSite.services.SpeedTestWebSiteService;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.file.services.FileDownloadService;
import com.caseyellow.server.central.domain.test.services.TestService;
import com.caseyellow.server.central.exceptions.ErrorResponse;
import com.caseyellow.server.central.exceptions.InternalException;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.caseyellow.server.central.exceptions.ErrorResponse.INTERNAL_ERROR_CODE;

/**
 * Created by dango on 6/25/17.
 */
@RestController
@RequestMapping("/central")
public class CentralController {

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
    public String getNextSpeedTestWebSite() {
        return speedTestWebSiteService.getNextSpeedTestWebSite();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/next-urls",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getNextUrls(@RequestParam("comparison-count") int numOfComparisonPerTest) {
        return fileDownloadService.getNextUrls(numOfComparisonPerTest);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/save-test",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveTest(@RequestBody @NotEmpty Test test) {
        testService.saveTest(test);
    }
}
