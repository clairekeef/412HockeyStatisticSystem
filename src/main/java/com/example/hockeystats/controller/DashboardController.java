package com.example.hockeystats.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hockeystats.model.DataRepository;
import com.example.hockeystats.model.PlayerStats;
import com.example.hockeystats.view.Dashboard;

import java.util.*;

@RestController
public class DashboardController {

    // Single shared instance — fetches from Supabase once on startup
    private final DataRepository repo = new DataRepository();
    private final Dashboard dashboard = new Dashboard();

    /**
     * GET /players
     * Returns all players from Supabase as a JSON array.
     * Each object has: name, country, position, gp, goals, assists, pts, pim, ppg
     */
    @GetMapping("/players")
    public List<Map<String, Object>> getPlayers() {
        List<Map<String, Object>> players = new ArrayList<>();
        for (PlayerStats p : repo.getAllPlayers()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name",     p.getName());
            map.put("country",  p.getCountry());   // lowercase key — matches frontend
            map.put("position", p.getPosition());
            map.put("GP",       p.getGp());
            map.put("G",        p.getGoals());
            map.put("A",        p.getAssists());
            map.put("PTS",      p.getPts());
            map.put("PIM",      p.getPim());
            map.put("PPG",      p.getPpg());
            players.add(map);
        }
        return players;
    }

    /**
     * GET /teams
     * Returns aggregated stats per country.
     */
    @GetMapping("/teams")
    public List<Map<String, Object>> getTeams() {
        // Build country → aggregated stats map
        Map<String, Map<String, Object>> teamMap = new LinkedHashMap<>();
        for (PlayerStats p : repo.getAllPlayers()) {
            String country = p.getCountry();
            if (country == null || country.isBlank()) continue;
            teamMap.computeIfAbsent(country, k -> {
                Map<String, Object> t = new LinkedHashMap<>();
                t.put("name", k);
                t.put("G",   0); t.put("A",  0); t.put("PTS", 0);
                t.put("GP",  0); t.put("PIM", 0); t.put("PPG", 0);
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

    /**
     * GET /dashboard
     * Returns tournament overview summary.
     */
    @GetMapping("/dashboard")
    public Map<String, Object> getTournamentOverview() {
        return dashboard.viewTournamentOverview();
    }
}