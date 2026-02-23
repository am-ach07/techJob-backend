package com;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;


@EnableScheduling
@SpringBootApplication
public class CrudsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudsApplication.class, args);
	}
	
	@Value("${spring.datasource.url}")
    private String url;

	@Value("${spring.datasource.password}")
    private String pswd;

    @Value("${spring.datasource.username}")
    private String user;

    

    @PostConstruct
    public void printEnv() {
        System.out.println("=== RAILWAY DB ENV VALUES ===");
        System.out.println("MYSQL_URL = " + url);
        System.out.println("MYSQLPASSWORD = " + pswd);
        System.out.println("MYSQLUSER = " + user);
        System.out.println("==============================");
    }
	
	

}
