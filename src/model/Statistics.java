package src.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Statistics {
    private DataRepository dataRepository;
    private Map<String, MetricCalculator> metricCalculators;

 public Statistics() {
        System.out.println("Statistics initialized");

        this.dataRepository = new DataRepository();
        this.metricCalculators = new HashMap<>();

        metricCalculators.put("Goals", (id) -> 10.0);
        metricCalculators.put("Assists", (id) -> 5.0);
        metricCalculators.put("Wins", (id) -> 12.0);
    }

        public String comparePlayers(List<String> playerIds, List<String> tournamentIds, List<String> metrics) {
            System.out.println("Statistics.comparePlayers called");
            System.out.println("Players: " + playerIds);
            System.out.println("Tournaments: " + tournamentIds);
            System.out.println("Metrics: " + metrics);

            return "Stub Player Comparison Complete";
        }

        public String compareTeams(List<String> teamIds, List<String> tournamentIds, List<String> metrics) {
            System.out.println("Statistics.compareTeams called");
            System.out.println("Teams: " + teamIds);
            System.out.println("Tournaments: " + tournamentIds);
            System.out.println("Metrics: " + metrics);
            
            return "Stub Team Comparison Complete";
        }

        public List<Stats> normalizeData(List<Stats> data, List<String> tournaments) {
            System.out.println("Statistics.normalizeData called");
            System.out.println("Tournaments: " + tournaments);
            
            return data;
        }

        public List<String> getAvailableMetrics(String entityType) {
            System.out.println("Statistics.getAvailableMetrics called");
            System.out.println("Entity Type: " + entityType);

            return List.of("Goals", "Assists", "Shots", "Wins", "Losses");
        }

        public PlayerStats getPlayerStats(String playerId, String tournamentId) {
            System.out.println("Statistics.getPlayerStats called");
            System.out.println("Player ID: " + playerId);
            System.out.println("Tournament ID: " + tournamentId);

            return new PlayerStats(playerId, 15.0, 8.0);
        }

        public TeamStats getTeamStats(String teamId, String tournamentId) {
            System.out.println("Statistics.getTeamStats called");
            System.out.println("Team ID: " + teamId);
            System.out.println("Tournament ID: " + tournamentId);

            return new TeamStats(teamId, 20.0, 4.0);
        }

        public double calculateMetric(String entityId, String metric, String tournamentId) {
            System.out.println("Statistics.calculateMetric called");
            System.out.println("Entity ID: " + entityId);
            System.out.println("Metric: " + metric);
            System.out.println("Tournament ID: " + tournamentId);

            MetricCalculator calculator = metricCalculators.get(metric);

            if (calculator != null) {
                return calculator.calculate(entityId);
            }

            return 0.0; // default stub value
        }


    }


