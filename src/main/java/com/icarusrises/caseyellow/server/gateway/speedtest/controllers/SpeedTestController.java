package com.icarusrises.caseyellow.server.gateway.speedtest.controllers;

import com.icarusrises.caseyellow.server.gateway.speedtest.services.SpeedTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dango on 6/4/17.
 */
@RestController
public class SpeedTestController {

    // Fields

    @Value("${pageController.msg}")
    private String message;
    private SpeedTestService speedTestService;

    // Constructor

    @Autowired
    public SpeedTestController(SpeedTestService speedTestService) {
        this.speedTestService = speedTestService;
    }

    // Methods

    @RequestMapping("/")
    public String get1() {
        return message + " : " + speedTestService.print("dang");
    }
}
