package com.example.hockeystats.model;

import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataRepository {

    private final Map<String, PlayerStats> playersByName;
    private final Map<String, List<PlayerStats>> playersByCountry;
    private final List<PlayerStats> allPlayers;

    public DataRepository() {
        List<PlayerStats> players = fetchPlayersFromSupabase();

        this.allPlayers = new ArrayList<>(players);
        this.playersByName = new HashMap<>();
        this.playersByCountry = new HashMap<>();

        for (PlayerStats p : players) {
            playersByName.put(p.getName(), p);

            playersByCountry
                .computeIfAbsent(p.getCountry(), k -> new ArrayList<>())
                .add(p);
        }
    }

    /**
     * Fetches player data from Supabase and converts JSON → PlayerStats objects
     */
    private List<PlayerStats> fetchPlayersFromSupabase() {
        try {
            String json = SupabaseService.getAllPlayers();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            List<PlayerStats> players = new ArrayList<>();

            for (JsonNode node : root) {

                String name = node.path("Name").asText();
                String country = node.path("team").asText();
                String position = node.path("Position").asText();

                int goals = (int) node.path("G").asDouble(0);
                int assists = (int) node.path("A").asDouble(0);
                int pts = (int) node.path("P").asDouble(0);
                int pim = (int) node.path("PIM").asDouble(0);

                PlayerStats player = new PlayerStats(
                        name,
                        "",          // group (not in your Supabase data)
                        country,
                        position,
                        0,           // gp (not available)
                        goals,
                        assists,
                        pts,
                        pim,
                        0            // ppg (not available)
                );

                players.add(player);
            }

            return players;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Returns all players
     */
    public List<PlayerStats> getAllPlayers() {
        return Collections.unmodifiableList(allPlayers);
    }

    /**
     * Get player by name
     */
    public PlayerStats getPlayerByName(String name) {
        return playersByName.get(name);
    }

    /**
     * Get players by country
     */
    public List<PlayerStats> getPlayersByCountry(String country) {
        return playersByCountry.getOrDefault(country, List.of());
    }

    /**
     * Get top scorers sorted by points
     */
    public List<PlayerStats> getTopScorers(int limit) {
        return allPlayers.stream()
                .sorted((a, b) -> Integer.compare(b.getPts(), a.getPts()))
                .limit(limit)
                .toList();
    }
}