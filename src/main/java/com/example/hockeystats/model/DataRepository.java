package src.main.java.com.example.hockeystats.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DataRepository loads and stores player data from a CSV file,
 * and provides query methods used by the rest of the system.
 *
 * CSV format:
 *   Name,Group,Country,Position,GP,G,A,PTS,PIM,PPG
 */
public class DataRepository {

    // Keyed by player name (case-insensitive lookup via toLowerCase)
    private final Map<String, PlayerStats> playersByName = new HashMap<>();

    // Keyed by country name (case-insensitive)
    private final Map<String, List<PlayerStats>> playersByCountry = new HashMap<>();

    // Keyed by group label (e.g. "A")
    private final Map<String, List<PlayerStats>> playersByGroup = new HashMap<>();

    // Master list preserving CSV order
    private final List<PlayerStats> allPlayers = new ArrayList<>();

    public DataRepository() {
        loadCSV("Players.csv");
        System.out.println("DataRepository initialized — " + allPlayers.size() + " players loaded.");
    }

    /**
     * Secondary constructor that accepts an explicit CSV path (useful for tests
     * that need to pass a different file or an absolute path).
     */
    public DataRepository(String csvPath) {
        loadCSV(csvPath);
        System.out.println("DataRepository initialized — " + allPlayers.size() + " players loaded.");
    }

    private void loadCSV(String path) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(path);

        try {
            BufferedReader reader;
            if (stream != null) {
                reader = new BufferedReader(new InputStreamReader(stream));
            } else {
                reader = new BufferedReader(new FileReader(path));
            }

            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Skip header row
                if (firstLine) {
                    firstLine = false;
                    if (line.toLowerCase().startsWith("name")) continue;
                }

                parseAndStore(line);
            }
            reader.close();

        } catch (IOException e) {
            System.err.println("DataRepository: could not load CSV from '" + path + "': " + e.getMessage());
        }
    }

    /**
     * Parses a single CSV row and registers the resulting PlayerStats object
     * in all internal indexes.
     *
     * Expected columns (0-based):
     *   0 Name | 1 Group | 2 Country | 3 Position |
     *   4 GP   | 5 G     | 6 A       | 7 PTS      | 8 PIM | 9 PPG
     */
    private void parseAndStore(String line) {
        String[] cols = line.split(",", -1);
        if (cols.length < 10) {
            System.err.println("DataRepository: skipping malformed row: " + line);
            return;
        }

        try {
            String name     = cols[0].trim();
            String group    = cols[1].trim();
            String country  = cols[2].trim();
            String position = cols[3].trim();
            int    gp       = Integer.parseInt(cols[4].trim());
            int    goals    = Integer.parseInt(cols[5].trim());
            int    assists  = Integer.parseInt(cols[6].trim());
            int    pts      = Integer.parseInt(cols[7].trim());
            int    pim      = Integer.parseInt(cols[8].trim());
            int    ppg      = Integer.parseInt(cols[9].trim());

            PlayerStats player = new PlayerStats(name, group, country, position,
                                                  gp, goals, assists, pts, pim, ppg);
            allPlayers.add(player);

            playersByName.put(name.toLowerCase(), player);

            playersByCountry
                .computeIfAbsent(country.toLowerCase(), k -> new ArrayList<>())
                .add(player);

            playersByGroup
                .computeIfAbsent(group.toLowerCase(), k -> new ArrayList<>())
                .add(player);

        } catch (NumberFormatException e) {
            System.err.println("DataRepository: skipping row with bad numeric field: " + line);
        }
    }

    /** Returns all loaded players (unmodifiable). */
    public List<PlayerStats> getAllPlayers() {
        return Collections.unmodifiableList(allPlayers);
    }

    /**
     * Looks up a player by exact name (case-insensitive).
     * Returns null when not found.
     */
    public PlayerStats getPlayerByName(String name) {
        if (name == null) return null;
        return playersByName.get(name.toLowerCase());
    }

    /**
     * Returns all players representing the given country (case-insensitive).
     */
    public List<PlayerStats> getPlayersByCountry(String country) {
        if (country == null) return Collections.emptyList();
        List<PlayerStats> result = playersByCountry.get(country.toLowerCase());
        return result != null ? Collections.unmodifiableList(result) : Collections.emptyList();
    }

    /**
     * Returns all players in the given tournament group (case-insensitive).
     */
    public List<PlayerStats> getPlayersByGroup(String group) {
        if (group == null) return Collections.emptyList();
        List<PlayerStats> result = playersByGroup.get(group.toLowerCase());
        return result != null ? Collections.unmodifiableList(result) : Collections.emptyList();
    }

    /**
     * Returns all players that play the given position (case-insensitive).
     * Common values: "Forward", "Defence".
     */
    public List<PlayerStats> getPlayersByPosition(String position) {
        if (position == null) return Collections.emptyList();
        return allPlayers.stream()
            .filter(p -> p.getPosition().equalsIgnoreCase(position))
            .collect(Collectors.toList());
    }

    /**
     * Returns the top {@code n} scorers across all loaded data,
     * ranked by total points (PTS), with goals as a tiebreaker.
     */
    public List<PlayerStats> getTopScorers(int n) {
        return allPlayers.stream()
            .sorted((a, b) -> {
                int cmp = Integer.compare(b.getPts(), a.getPts());
                return cmp != 0 ? cmp : Integer.compare(b.getGoals(), a.getGoals());
            })
            .limit(n)
            .collect(Collectors.toList());
    }

    /**
     * Returns the top {@code n} scorers for the specified country.
     */
    public List<PlayerStats> getTopScorersByCountry(String country, int n) {
        return getPlayersByCountry(country).stream()
            .sorted((a, b) -> {
                int cmp = Integer.compare(b.getPts(), a.getPts());
                return cmp != 0 ? cmp : Integer.compare(b.getGoals(), a.getGoals());
            })
            .limit(n)
            .collect(Collectors.toList());
    }

    /**
     * Returns a map of country → total goals, useful for team-level comparisons.
     */
    public Map<String, Integer> getGoalsByCountry() {
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, List<PlayerStats>> entry : playersByCountry.entrySet()) {
            int totalGoals = entry.getValue().stream()
                .mapToInt(PlayerStats::getGoals)
                .sum();
            String displayName = entry.getValue().get(0).getCountry();
            result.put(displayName, totalGoals);
        }
        return result;
    }

    /**
     * Returns a map of country → total points, useful for team-level comparisons.
     */
    public Map<String, Integer> getPointsByCountry() {
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, List<PlayerStats>> entry : playersByCountry.entrySet()) {
            int totalPts = entry.getValue().stream()
                .mapToInt(PlayerStats::getPts)
                .sum();
            String displayName = entry.getValue().get(0).getCountry();
            result.put(displayName, totalPts);
        }
        return result;
    }

    /**
     * Returns the list of distinct country names present in the loaded data.
     */
    public List<String> getAvailableCountries() {
        return playersByCountry.values().stream()
            .map(list -> list.get(0).getCountry())
            .sorted()
            .collect(Collectors.toList());
    }

    /**
     * Returns a numeric metric value for a named player.
     * Supported: "Goals"/"G", "Assists"/"A", "Points"/"PTS", "PIM", "PPG", "GP".
     * Returns 0.0 when the player or metric is not found.
     */
    public double getMetricForPlayer(String playerName, String metric) {
        PlayerStats p = getPlayerByName(playerName);
        if (p == null) return 0.0;
        return extractMetric(p, metric);
    }

    /**
     * Returns a numeric metric value summed across all players of a country.
     */
    public double getMetricForCountry(String country, String metric) {
        return getPlayersByCountry(country).stream()
            .mapToDouble(p -> extractMetric(p, metric))
            .sum();
    }

    private double extractMetric(PlayerStats p, String metric) {
        if (metric == null) return 0.0;
        switch (metric.toUpperCase()) {
            case "GOALS": case "G":    return p.getGoals();
            case "ASSISTS": case "A":  return p.getAssists();
            case "POINTS": case "PTS": return p.getPts();
            case "PIM":                return p.getPim();
            case "PPG":                return p.getPpg();
            case "GP":                 return p.getGp();
            default:
                System.err.println("DataRepository: unknown metric '" + metric + "'");
                return 0.0;
        }
    }

    @Override
    public String toString() {
        return "DataRepository{players=" + allPlayers.size()
            + ", countries=" + playersByCountry.size() + "}";
    }
}
