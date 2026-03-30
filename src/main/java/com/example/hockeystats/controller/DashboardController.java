package com.example.hockeystats.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hockeystats.model.DataRepository;
import com.example.hockeystats.model.Game;
import com.example.hockeystats.model.PlayerStats;
import com.example.hockeystats.view.Dashboard;

import java.util.*;

@RestController
public class DashboardController {

    private final DataRepository repo = new DataRepository();
    private final Dashboard dashboard = new Dashboard();

    private List<Map<String, Object>> buildPlayerList(List<PlayerStats> players) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (PlayerStats p : players) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name",     p.getName());
            map.put("country",  p.getCountry());
            map.put("position", p.getPosition());
            map.put("GP",       p.getGp());
            map.put("G",        p.getGoals());
            map.put("A",        p.getAssists());
            map.put("PTS",      p.getPts());
            map.put("PIM",      p.getPim());
            map.put("PPG",      p.getPpg());
            result.add(map);
        }
        return result;
    }

    @GetMapping("/players")
    public List<Map<String, Object>> getPlayers() {
        return buildPlayerList(repo.getAllPlayers());
    }

    @GetMapping("/players/women")
    public List<Map<String, Object>> getWomenPlayers() throws Exception {
        com.example.hockeystats.model.DataRepository womenRepo =
            new com.example.hockeystats.model.DataRepository(false);
        return buildPlayerList(womenRepo.getAllPlayers());
    }

    @GetMapping("/teams")
    public List<Map<String, Object>> getTeams() {
        Map<String, Map<String, Object>> teamMap = new LinkedHashMap<>();
        for (PlayerStats p : repo.getAllPlayers()) {
            String country = p.getCountry();
            if (country == null || country.isBlank()) continue;
            teamMap.computeIfAbsent(country, k -> {
                Map<String, Object> t = new LinkedHashMap<>();
                t.put("name", k);
                t.put("G", 0); t.put("A", 0); t.put("PTS", 0);
                t.put("GP", 0); t.put("PIM", 0); t.put("PPG", 0);
                return t;
            });
            Map<String, Object> t = teamMap.get(country);
            t.put("G",   (int) t.get("G")   + p.getGoals());
            t.put("A",   (int) t.get("A")   + p.getAssists());
            t.put("PTS", (int) t.get("PTS") + p.getPts());
            t.put("GP",  (int) t.get("GP")  + p.getGp());
            t.put("PIM", (int) t.get("PIM") + p.getPim());
            t.put("PPG", (int) t.get("PPG") + p.getPpg());
        }
        return new ArrayList<>(teamMap.values());
    }

    @GetMapping("/results")
    public List<Map<String, Object>> getResults() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Game g : repo.getAllResults()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("teamH",       g.getTeamH());
            map.put("teamA",       g.getTeamA());
            map.put("teamHCode",   g.getTeamHCode());
            map.put("teamACode",   g.getTeamACode());
            map.put("scoreTH",     g.getScoreTH());
            map.put("scoreTA",     g.getScoreTA());
            map.put("eventStage",  g.getEventStage());
            map.put("eventStatus", g.getEventStatus());
            map.put("time",        g.getTime());
            map.put("location",    g.getLocation());
            map.put("sex",         g.getSex());
            result.add(map);
        }
        return result;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> getTournamentOverview() {
        return dashboard.viewTournamentOverview();
    }
}