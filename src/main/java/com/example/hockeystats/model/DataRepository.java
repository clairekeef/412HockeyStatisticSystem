package com.example.hockeystats.model;

import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataRepository {

    private final Map<String, PlayerStats> playersByName;        // lowercase key
    private final Map<String, List<PlayerStats>> playersByCountry; // lowercase key
    private final Map<String, List<PlayerStats>> playersByPosition; // lowercase key
    private final List<PlayerStats> allPlayers;

    public DataRepository() {
        List<PlayerStats> players = fetchPlayersFromSupabase();
        this.allPlayers = new ArrayList<>(players);
        this.playersByName     = new HashMap<>();
        this.playersByCountry  = new HashMap<>();
        this.playersByPosition = new HashMap<>();

        for (PlayerStats p : players) {
            playersByName.put(p.getName().toLowerCase(), p);
            playersByCountry
                .computeIfAbsent(p.getCountry().toLowerCase(), k -> new ArrayList<>())
                .add(p);
            if (p.getPosition() != null && !p.getPosition().isBlank()) {
                playersByPosition
                    .computeIfAbsent(p.getPosition().toLowerCase(), k -> new ArrayList<>())
                    .add(p);
            }
        }
    }

    private List<PlayerStats> fetchPlayersFromSupabase() {
        try {
            String json = SupabaseService.getAllPlayers();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            List<PlayerStats> players = new ArrayList<>();
            for (JsonNode node : root) {
                String name     = node.path("Name").asText();
                String country  = node.path("team").asText();
                String position = node.path("Position").asText();
                int goals   = (int) node.path("G").asDouble(0);
                int assists = (int) node.path("A").asDouble(0);
                int pts     = (int) node.path("P").asDouble(0);
                int pim     = (int) node.path("PIM").asDouble(0);
                players.add(new PlayerStats(name, "", country, position, 0, goals, assists, pts, pim, 0));
            }
            return players;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // ── Basic lookups ────────────────────────────────────────────

    public List<PlayerStats> getAllPlayers() {
        return Collections.unmodifiableList(allPlayers);
    }

    /** Case-insensitive lookup by name. Returns null if not found. */
    public PlayerStats getPlayerByName(String name) {
        if (name == null) return null;
        return playersByName.get(name.toLowerCase());
    }

    /** Case-insensitive lookup by country. Returns empty list if not found. */
    public List<PlayerStats> getPlayersByCountry(String country) {
        if (country == null) return List.of();
        return playersByCountry.getOrDefault(country.toLowerCase(), List.of());
    }

    /** Case-insensitive lookup by position (e.g. "Forward", "Defence"). */
    public List<PlayerStats> getPlayersByPosition(String position) {
        if (position == null) return List.of();
        return playersByPosition.getOrDefault(position.toLowerCase(), List.of());
    }

    // ── Top scorers ──────────────────────────────────────────────

    public List<PlayerStats> getTopScorers(int limit) {
        return allPlayers.stream()
            .sorted((a, b) -> Integer.compare(b.getPts(), a.getPts()))
            .limit(limit)
            .toList();
    }

    public List<PlayerStats> getTopScorersByCountry(String country, int limit) {
        return getPlayersByCountry(country).stream()
            .sorted((a, b) -> Integer.compare(b.getPts(), a.getPts()))
            .limit(limit)
            .toList();
    }

    // ── Country-level aggregations ───────────────────────────────

    /** Returns a sorted list of all distinct country names. */
    public List<String> getAvailableCountries() {
        return playersByCountry.keySet().stream()
            .map(k -> playersByCountry.get(k).get(0).getCountry()) // original casing
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    /** Returns total goals per country (original country name as key). */
    public Map<String, Integer> getGoalsByCountry() {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (List<PlayerStats> players : playersByCountry.values()) {
            String country = players.get(0).getCountry();
            int total = players.stream().mapToInt(PlayerStats::getGoals).sum();
            result.put(country, total);
        }
        return result;
    }

    /** Returns total points per country (original country name as key). */
    public Map<String, Integer> getPointsByCountry() {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (List<PlayerStats> players : playersByCountry.values()) {
            String country = players.get(0).getCountry();
            int total = players.stream().mapToInt(PlayerStats::getPts).sum();
            result.put(country, total);
        }
        return result;
    }

    // ── Metric helpers ───────────────────────────────────────────

    /**
     * Returns a numeric metric for a single player by name.
     * Supported keys: G, Goals, A, Assists, P, PTS, Points, PIM, PPG, GP
     */
    public double getMetricForPlayer(String name, String metric) {
        PlayerStats p = getPlayerByName(name);
        if (p == null || metric == null) return 0.0;
        return extractMetric(p, metric);
    }

    /**
     * Returns the sum of a metric across all players from a given country.
     * Same metric keys as getMetricForPlayer.
     */
    public double getMetricForCountry(String country, String metric) {
        if (country == null || metric == null) return 0.0;
        return getPlayersByCountry(country).stream()
            .mapToDouble(p -> extractMetric(p, metric))
            .sum();
    }

    private double extractMetric(PlayerStats p, String metric) {
        return switch (metric) {
            case "G", "Goals"           -> p.getGoals();
            case "A", "Assists"         -> p.getAssists();
            case "P", "PTS", "Points"   -> p.getPts();
            case "PIM"                  -> p.getPim();
            case "PPG"                  -> p.getPpg();
            case "GP"                   -> p.getGp();
            default                     -> 0.0;
        };
    }
}