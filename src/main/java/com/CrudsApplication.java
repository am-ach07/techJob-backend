package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class CrudsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudsApplication.class, args);
	}
	
	
	
	

}
