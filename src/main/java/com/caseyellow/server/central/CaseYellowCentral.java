package com.caseyellow.server.central;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class CaseYellowCentral {

	public static void main(String[] args) {
		SpringApplication.run(CaseYellowCentral.class, args);
	}

}
