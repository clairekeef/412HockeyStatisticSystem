package com.example.hockeystats.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hockeystats.view.Dashboard;

import org.springframework.web.bind.annotation.GetMapping;
import java.io.*;
import java.util.*;

@RestController
public class DashboardController {

    Dashboard dashboard = new Dashboard();

    @GetMapping("/dashboard")
    public Map<String,Object> getTournamentOverview() {

        return dashboard.viewTournamentOverview();

    }

    @GetMapping("/players")
    public List<Map<String, Object>> getPlayers() throws Exception {
    List<Map<String, Object>> players = new ArrayList<>();
    InputStream is = getClass().getResourceAsStream("/playersforhtml.csv");
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    
    String header = reader.readLine(); // skip header row
    String line;
    while ((line = reader.readLine()) != null) {
        String[] parts = line.split(",");
        Map<String, Object> p = new HashMap<>();
        p.put("name", parts[0]);
        p.put("Country", parts[2]);
        p.put("GP",  Integer.parseInt(parts[4]));
        p.put("G",   Integer.parseInt(parts[5]));
        p.put("A",   Integer.parseInt(parts[6]));
        p.put("PTS", Integer.parseInt(parts[7]));
        p.put("PIM", Integer.parseInt(parts[8]));
        p.put("PPG", Integer.parseInt(parts[9]));
        players.add(p);
    }

    reader.close();
    return players;
}

}
// go to https://localhost:8080/ to view

/*
mvn clean compile
mvn spring-boot:run
 */