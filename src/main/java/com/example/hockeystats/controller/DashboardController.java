package com.example.hockeystats.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    @GetMapping("/dashboard")
    public String getDashboard() {
        return "Dashboard loaded!";
    }
}

/*
mvn clean compile
mvn spring-boot:run
 */