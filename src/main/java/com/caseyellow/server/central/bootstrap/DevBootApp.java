package com.caseyellow.server.central.bootstrap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Profile("dev")
public class DevBootApp {

    @PostConstruct
    public void init() {
        System.out.println("********************************************");
        System.out.println("********************************************");
        System.out.println("********************************************");
        System.out.println("********************************************");
        System.out.println("********************************************");
        System.out.println("*******          dango               *******");
        System.out.println("********************************************");
        System.out.println("********************************************");
        System.out.println("********************************************");
        System.out.println("********************************************");
        System.out.println("********************************************");
    }
}
