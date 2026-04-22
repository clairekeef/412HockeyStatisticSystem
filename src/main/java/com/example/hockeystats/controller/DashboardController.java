package com.example.hockeystats.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.hockeystats.model.DataObserver;
import com.example.hockeystats.model.DataRepository;
import com.example.hockeystats.model.GameCompare;
import com.example.hockeystats.model.Lineup;
import com.example.hockeystats.model.PlayerStats;
import com.example.hockeystats.view.Dashboard;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@RestController
public class DashboardController implements DataObserver {

    private static final String SUPABASE_URL     = "https://obgfwjsuexlqhsmrbpju.supabase.co";
    private static final String SERVICE_ROLE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9iZ2Z3anN1ZXhscWhzbXJicGp1Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3NDUzMjYyOCwiZXhwIjoyMDkwMTA4NjI4fQ.UvDvb78HjswlhNVoDpe8nVeBCMPjKxYPel6QahIPiCE";

    private final DataRepository repo;
    private final Dashboard dashboard = new Dashboard();

    private int lastPlayerCount = 0;
    private String lastDataSource = "unknown";

    public DashboardController() {
        this.repo = new DataRepository();
        this.repo.addObserver(this);
    }

    @Override
    public void onDataLoaded(int playerCount, String source) {
        this.lastPlayerCount = playerCount;
        this.lastDataSource  = source;
        System.out.println("DashboardController notified: "
            + playerCount + " players loaded from " + source);
    }

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
    public List<Map<String, Object>> getWomenPlayers() {
        DataRepository womenRepo = new DataRepository(false);
        womenRepo.addObserver(this);
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

    @GetMapping("/dashboard")
    public Map<String, Object> getTournamentOverview() {
        return dashboard.viewTournamentOverview();
    }

    @GetMapping("/data-status")
    public Map<String, Object> getDataStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("lastPlayerCount", lastPlayerCount);
        status.put("lastDataSource",  lastDataSource);
        status.put("observerPattern", "DashboardController is a DataObserver of DataRepository");
        return status;
    }

    @GetMapping("/lineups/sample")
    public Map<String, Object> getSampleLineup() {
        return Lineup.getSampleLineup().toMap();
    }

    @GetMapping("/game-compare/sample")
    public Map<String, Object> getSampleGameCompare() {
        return GameCompare.getSampleData().toMap();
    }

    /**
     * DELETE /delete-account
     * Deletes the user's profile, lineups, and auth account from Supabase.
     * Requires the user's JWT token in the Authorization header.
     */
    @DeleteMapping("/delete-account")
    public Map<String, Object> deleteAccount(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            // Extract the user token from the header
            String userToken = authHeader.replace("Bearer ", "").trim();
            HttpClient client = HttpClient.newHttpClient();

            // Step 1 — Get the user's ID from Supabase Auth
            HttpRequest userReq = HttpRequest.newBuilder()
                .uri(URI.create(SUPABASE_URL + "/auth/v1/user"))
                .header("apikey", SERVICE_ROLE_KEY)
                .header("Authorization", "Bearer " + userToken)
                .GET()
                .build();
            HttpResponse<String> userRes = client.send(userReq, HttpResponse.BodyHandlers.ofString());
            String userId = extractJsonField(userRes.body(), "id");

            if (userId == null || userId.isBlank()) {
                result.put("success", false);
                result.put("error", "Could not identify user");
                return result;
            }

            // Step 2 — Delete lineups
            HttpRequest deleteLineups = HttpRequest.newBuilder()
                .uri(URI.create(SUPABASE_URL + "/rest/v1/lineups?created_by=eq." + userId))
                .header("apikey", SERVICE_ROLE_KEY)
                .header("Authorization", "Bearer " + SERVICE_ROLE_KEY)
                .DELETE()
                .build();
            client.send(deleteLineups, HttpResponse.BodyHandlers.ofString());

            // Step 3 — Delete profile
            HttpRequest deleteProfile = HttpRequest.newBuilder()
                .uri(URI.create(SUPABASE_URL + "/rest/v1/profiles?id=eq." + userId))
                .header("apikey", SERVICE_ROLE_KEY)
                .header("Authorization", "Bearer " + SERVICE_ROLE_KEY)
                .DELETE()
                .build();
            client.send(deleteProfile, HttpResponse.BodyHandlers.ofString());

            // Step 4 — Delete auth user (requires service role key)
            HttpRequest deleteAuth = HttpRequest.newBuilder()
                .uri(URI.create(SUPABASE_URL + "/auth/v1/admin/users/" + userId))
                .header("apikey", SERVICE_ROLE_KEY)
                .header("Authorization", "Bearer " + SERVICE_ROLE_KEY)
                .DELETE()
                .build();
            HttpResponse<String> deleteRes = client.send(deleteAuth, HttpResponse.BodyHandlers.ofString());

            if (deleteRes.statusCode() == 200 || deleteRes.statusCode() == 204) {
                result.put("success", true);
                result.put("message", "Account fully deleted");
            } else {
                result.put("success", false);
                result.put("error", "Auth deletion failed: " + deleteRes.statusCode());
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    /** Simple JSON field extractor for flat JSON objects */
    private String extractJsonField(String json, String field) {
        String key = "\"" + field + "\":\"";
        int start = json.indexOf(key);
        if (start == -1) return null;
        start += key.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return null;
        return json.substring(start, end);
    }
}