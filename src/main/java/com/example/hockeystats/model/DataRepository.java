package com.example.hockeystats.model;

import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * DataRepository acts as the Subject in the Observer design pattern.
 * It fetches player data from Supabase and notifies registered
 * DataObservers when the data has been loaded or refreshed.
 */
public class DataRepository implements DataSubject {

    private final List<DataObserver> observers = new ArrayList<>();
    private final Map<String, PlayerStats> playersByName;
    private final Map<String, List<PlayerStats>> playersByCountry;
    private final Map<String, List<PlayerStats>> playersByPosition;
    private final List<PlayerStats> allPlayers;

    // Default: men's data
    public DataRepository() {
        this(true);
    }

    // true = men, false = women
    public DataRepository(boolean men) {
        List<PlayerStats> players = fetchPlayers(men);
        this.allPlayers        = new ArrayList<>(players);
        this.playersByName     = new HashMap<>();
        this.playersByCountry  = new HashMap<>();
        this.playersByPosition = new HashMap<>();

        for (PlayerStats p : players) {
            playersByName.put(p.getName().toLowerCase(), p);
            if (p.getCountry() != null && !p.getCountry().isBlank()) {
                playersByCountry
                    .computeIfAbsent(p.getCountry().toLowerCase(), k -> new ArrayList<>())
                    .add(p);
            }
            if (p.getPosition() != null && !p.getPosition().isBlank()) {
                playersByPosition
                    .computeIfAbsent(p.getPosition().toLowerCase(), k -> new ArrayList<>())
                    .add(p);
            }
        }

        // Notify all observers that data has been loaded
        String source = men ? "Supabase (Men)" : "Supabase (Women)";
        notifyObservers(this.allPlayers.size(), source);
    }

    // ── Observer pattern methods ─────────────────────────────

    @Override
    public void addObserver(DataObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(DataObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(int playerCount, String source) {
        System.out.println("DataRepository: notifying " + observers.size() + " observer(s)");
        for (DataObserver observer : observers) {
            observer.onDataLoaded(playerCount, source);
        }
    }

    // ── Data fetching ────────────────────────────────────────

    private List<PlayerStats> fetchPlayers(boolean men) {
        try {
            String json = men
                ? SupabaseService.getAllPlayers()
                : SupabaseService.getWomenPlayers();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            Map<String, int[]> agg  = new LinkedHashMap<>();
            Map<String, String[]> meta = new LinkedHashMap<>();

            for (JsonNode node : root) {
                String rawName  = node.path("Name").asText("").trim();
                String team     = node.path("team").asText("").trim();
                String position = node.path("Position").asText("").trim();
                String cleaned  = cleanName(rawName);
                String key      = cleaned + "|" + team;

                int g   = parseIntSafe(node.path("G").asText("0"));
                int a   = parseIntSafe(node.path("A").asText("0"));
                int p   = parseIntSafe(node.path("P").asText("0"));
                int pim = parseIntSafe(node.path("PIM").asText("0"));

                agg.computeIfAbsent(key, k -> new int[5]);
                agg.get(key)[0] += g;
                agg.get(key)[1] += a;
                agg.get(key)[2] += p;
                agg.get(key)[3] += pim;
                agg.get(key)[4] += 1;

                meta.putIfAbsent(key, new String[]{ cleaned, team, position });
            }

            List<PlayerStats> players = new ArrayList<>();
            for (Map.Entry<String, int[]> entry : agg.entrySet()) {
                String[] m  = meta.get(entry.getKey());
                int[]    s  = entry.getValue();
                int goals   = s[0];
                int assists = s[1];
                int pts     = s[2] > 0 ? s[2] : goals + assists;
                int pim     = s[3];
                int gp      = s[4];
                players.add(new PlayerStats(m[0], "", m[1], m[2], gp, goals, assists, pts, pim, 0));
            }

            return players;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private String cleanName(String raw) {
        if (raw == null || raw.isBlank()) return raw;
        String[] parts = raw.trim().split("\\s+");
        String lastName = parts[0];
        List<String> firstName = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].equals(parts[i].toUpperCase())) {
                firstName.add(parts[i]);
            }
        }
        if (!firstName.isEmpty()) {
            return String.join(" ", firstName) + " " + capitalize(lastName);
        }
        return capitalize(lastName);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.charAt(0) + s.substring(1).toLowerCase();
    }

    private int parseIntSafe(String s) {
        try { return (int) Double.parseDouble(s.trim()); }
        catch (Exception e) { return 0; }
    }

    // ── Basic lookups ────────────────────────────────────────

    public List<PlayerStats> getAllPlayers() {
        return Collections.unmodifiableList(allPlayers);
    }

    public PlayerStats getPlayerByName(String name) {
        if (name == null) return null;
        return playersByName.get(name.toLowerCase());
    }

    public List<PlayerStats> getPlayersByCountry(String country) {
        if (country == null) return List.of();
        return playersByCountry.getOrDefault(country.toLowerCase(), List.of());
    }

    public List<PlayerStats> getPlayersByPosition(String position) {
        if (position == null) return List.of();
        return playersByPosition.getOrDefault(position.toLowerCase(), List.of());
    }

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

    public List<String> getAvailableCountries() {
        return playersByCountry.keySet().stream()
            .map(k -> playersByCountry.get(k).get(0).getCountry())
            .distinct().sorted()
            .collect(Collectors.toList());
    }

    public Map<String, Integer> getGoalsByCountry() {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (List<PlayerStats> players : playersByCountry.values()) {
            String country = players.get(0).getCountry();
            result.put(country, players.stream().mapToInt(PlayerStats::getGoals).sum());
        }
        return result;
    }

    public Map<String, Integer> getPointsByCountry() {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (List<PlayerStats> players : playersByCountry.values()) {
            String country = players.get(0).getCountry();
            result.put(country, players.stream().mapToInt(PlayerStats::getPts).sum());
        }
        return result;
    }

    public double getMetricForPlayer(String name, String metric) {
        PlayerStats p = getPlayerByName(name);
        if (p == null || metric == null) return 0.0;
        return extractMetric(p, metric);
    }

    public double getMetricForCountry(String country, String metric) {
        if (country == null || metric == null) return 0.0;
        return getPlayersByCountry(country).stream()
            .mapToDouble(p -> extractMetric(p, metric))
            .sum();
    }

    private double extractMetric(PlayerStats p, String metric) {
        return switch (metric) {
            case "G", "Goals"         -> p.getGoals();
            case "A", "Assists"       -> p.getAssists();
            case "P", "PTS", "Points" -> p.getPts();
            case "PIM"                -> p.getPim();
            case "PPG"                -> p.getPpg();
            case "GP"                 -> p.getGp();
            default                   -> 0.0;
        };
    }
}