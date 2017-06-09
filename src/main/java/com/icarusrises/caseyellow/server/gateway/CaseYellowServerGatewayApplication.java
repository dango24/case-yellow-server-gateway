package com.icarusrises.caseyellow.server.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class CaseYellowServerGatewayApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(CaseYellowServerGatewayApplication.class, args);
	}
}
