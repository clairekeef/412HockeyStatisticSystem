package src.main.java.com.example.hockeystats.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Statistics provides high-level analysis operations backed by real player
 * data loaded through DataRepository.
 */
public class Statistics {

    private final DataRepository dataRepository;
    private final Map<String, MetricCalculator> metricCalculators;

    public Statistics() {
        this.dataRepository = new DataRepository();
        this.metricCalculators = new HashMap<>();

        // Wire metric calculators to live DataRepository data
        metricCalculators.put("Goals",   id -> dataRepository.getMetricForPlayer(id, "Goals"));
        metricCalculators.put("Assists", id -> dataRepository.getMetricForPlayer(id, "Assists"));
        metricCalculators.put("Points",  id -> dataRepository.getMetricForPlayer(id, "Points"));
        metricCalculators.put("PIM",     id -> dataRepository.getMetricForPlayer(id, "PIM"));
        metricCalculators.put("PPG",     id -> dataRepository.getMetricForPlayer(id, "PPG"));
        metricCalculators.put("GP",      id -> dataRepository.getMetricForPlayer(id, "GP"));

        // Team-level calculators (entityId = country name)
        metricCalculators.put("Wins",    id -> dataRepository.getMetricForCountry(id, "Goals"));

        System.out.println("Statistics initialized.");
    }

  
    /**
     * Compares multiple players across the given metrics and returns a
     * formatted summary string.
     *
     * @param playerIds     list of player names to compare
     * @param tournamentIds currently unused (reserved for future multi-tournament support)
     * @param metrics       metric names, e.g. ["Goals", "Assists", "Points"]
     * @return formatted comparison table as a String
     */
    public String comparePlayers(List<String> playerIds, List<String> tournamentIds,
                                 List<String> metrics) {
        System.out.println("Statistics.comparePlayers called");
        System.out.println("Players: " + playerIds);
        System.out.println("Tournaments: " + tournamentIds);
        System.out.println("Metrics: " + metrics);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-25s", "Player"));
        for (String m : metrics) sb.append(String.format(" | %-8s", m));
        sb.append("\n").append("-".repeat(25 + metrics.size() * 12)).append("\n");

        for (String playerId : playerIds) {
            PlayerStats p = dataRepository.getPlayerByName(playerId);
            if (p == null) {
                sb.append(String.format("%-25s | NOT FOUND%n", playerId));
                continue;
            }
            sb.append(String.format("%-25s", p.getName()));
            for (String metric : metrics) {
                double val = calculateMetric(playerId, metric, null);
                sb.append(String.format(" | %-8.0f", val));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Compares multiple teams (countries) across the given metrics.
     *
     * @param teamIds       list of country names to compare
     * @param tournamentIds currently unused (reserved for future use)
     * @param metrics       metric names
     * @return formatted comparison table as a String
     */
    public String compareTeams(List<String> teamIds, List<String> tournamentIds,
                                List<String> metrics) {
        System.out.println("Statistics.compareTeams called");
        System.out.println("Teams: " + teamIds);
        System.out.println("Tournaments: " + tournamentIds);
        System.out.println("Metrics: " + metrics);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s", "Country"));
        for (String m : metrics) sb.append(String.format(" | %-8s", m));
        sb.append("\n").append("-".repeat(10 + metrics.size() * 12)).append("\n");

        for (String teamId : teamIds) {
            sb.append(String.format("%-10s", teamId));
            for (String metric : metrics) {
                double val = dataRepository.getMetricForCountry(teamId, metric);
                sb.append(String.format(" | %-8.0f", val));
            }
            sb.append("\n");
        }

        return sb.toString();
    }


    /**
     * Returns the input list of Stats objects normalised to the [0, 1] range
     * based on the min/max values in that list.
     */
    public List<Stats> normalizeData(List<Stats> data, List<String> tournaments) {
        System.out.println("Statistics.normalizeData called");
        System.out.println("Tournaments: " + tournaments);

        if (data == null || data.isEmpty()) return data;

        double min = data.stream().mapToDouble(Stats::getValue).min().orElse(0);
        double max = data.stream().mapToDouble(Stats::getValue).max().orElse(1);
        double range = max - min;

        if (range == 0) return data; // all values are the same — nothing to normalise

        return data.stream()
            .map(s -> new Stats(s.getId(), (s.getValue() - min) / range))
            .collect(Collectors.toList());
    }

    /**
     * Returns the list of metric names supported by this system.
     *
     * @param entityType "player" or "team" (currently returns the same set)
     */
    public List<String> getAvailableMetrics(String entityType) {
        System.out.println("Statistics.getAvailableMetrics called");
        System.out.println("Entity Type: " + entityType);
        return List.of("Goals", "Assists", "Points", "GP", "PIM", "PPG");
    }

   

    /**
     * Returns a PlayerStats object for the given player name.
     * The tournamentId parameter is accepted for API compatibility but is not
     * used — all data currently comes from a single loaded dataset.
     */
    public PlayerStats getPlayerStats(String playerId, String tournamentId) {
        System.out.println("Statistics.getPlayerStats called");
        System.out.println("Player ID: " + playerId);
        System.out.println("Tournament ID: " + tournamentId);

        PlayerStats p = dataRepository.getPlayerByName(playerId);
        if (p != null) return p;

        // Fall back to a stub if the player is genuinely unknown
        System.out.println("Warning: player '" + playerId + "' not found — returning stub.");
        return new PlayerStats(playerId, 0.0, 0.0);
    }

    /**
     * Returns a TeamStats object built from aggregated country stats.
     * "wins" = total goals, "losses" = total PIM (placeholder for future
     * win/loss data when game results are available).
     */
    public TeamStats getTeamStats(String teamId, String tournamentId) {
        System.out.println("Statistics.getTeamStats called");
        System.out.println("Team ID: " + teamId);
        System.out.println("Tournament ID: " + tournamentId);

        double totalGoals = dataRepository.getMetricForCountry(teamId, "Goals");
        double totalPIM   = dataRepository.getMetricForCountry(teamId, "PIM");

        if (totalGoals == 0 && totalPIM == 0) {
            // Unknown team — return stub
            System.out.println("Warning: team '" + teamId + "' not found — returning stub.");
            return new TeamStats(teamId, 0, 0);
        }

        return new TeamStats(teamId, totalGoals, totalPIM);
    }

    /**
     * Calculates a named metric for a player or team entity.
     *
     * @param entityId    player name or country name
     * @param metric      metric name (see getAvailableMetrics)
     * @param tournamentId accepted for compatibility; not currently used
     * @return numeric metric value, or 0.0 if not found
     */
    public double calculateMetric(String entityId, String metric, String tournamentId) {
        System.out.println("Statistics.calculateMetric called");
        System.out.println("Entity ID: " + entityId);
        System.out.println("Metric: " + metric);
        System.out.println("Tournament ID: " + tournamentId);

        MetricCalculator calculator = metricCalculators.get(metric);
        if (calculator != null) {
            return calculator.calculate(entityId);
        }

        System.out.println("Warning: no calculator for metric '" + metric + "'");
        return 0.0;
    }


    public DataRepository getDataRepository() {
        return dataRepository;
    }
}
