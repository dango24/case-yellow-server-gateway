package com.caseyellow.server.central.controllers;

import com.caseyellow.server.central.domain.webSite.model.SpeedTestMetaData;
import com.caseyellow.server.central.domain.webSite.services.SpeedTestWebSiteService;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.file.services.FileDownloadService;
import com.caseyellow.server.central.domain.test.services.TestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

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
    public SpeedTestMetaData getNextSpeedTestWebSite() {
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


    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/upload")
    public String upload(@RequestParam("payload") String payload, MultipartRequest request/*@RequestParam("photo")@NotEmpty MultipartFile[] files*/) throws IOException {

        Map<String, MultipartFile> map = request.getFileMap();

        map.values().forEach(this::dango);
        // Get the file and save it somewhere
        SpeedTestMetaData urlWrapper = new ObjectMapper().readValue(payload, SpeedTestMetaData.class);



        return "dango&esfir";
    }

    private void dango(MultipartFile file) {

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get("/home/dango/Downloads/temp/" + file.getOriginalFilename());
            Files.write(path, bytes);
        } catch (Exception e) {
            System.out.println("dango errroe");
        }
    }

}
