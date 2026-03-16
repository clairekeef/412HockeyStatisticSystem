package com.example.hockeystats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HockeyStatisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HockeyStatisticsApplication.class, args);
    }

}

// go to http://localhost:8080/ to view

/*
mvn clean compile
mvn spring-boot:run
 */