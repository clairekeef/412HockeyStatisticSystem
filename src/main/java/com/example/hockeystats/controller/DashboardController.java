package com.example.hockeystats.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hockeystats.view.Dashboard;

@RestController
public class DashboardController {

    Dashboard dashboard = new Dashboard();

    @GetMapping("/dashboard")
    public Map<String,Object> getTournamentOverview() {

        return dashboard.viewTournamentOverview();

    }
}

// go to https://localhost:8080/ to view

/*
mvn clean compile
mvn spring-boot:run
 */