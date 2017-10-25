package com.caseyellow.server.central;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;



@EnableScheduling
@SpringBootApplication
public class CaseYellowCentral extends SpringBootServletInitializer {

	private static Logger logger = Logger.getLogger(CaseYellowCentral.class);

	/**
	 *	Running as a jar
	 */
	public static void main(String[] args) {
		SpringApplication.run(CaseYellowCentral.class, args);
	}

	/**
	 *	Running as a war
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(CaseYellowCentral.class);
	}
}
