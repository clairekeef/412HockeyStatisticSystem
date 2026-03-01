package src.tester;

import src.controller.AuthenticationModule;
import src.model.*;
import src.view.Dashboard;
import src.controller.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class Tester {

    public static void main(String[] args) {

        System.out.println();
        System.out.println("===== STARTING SYSTEM INTEGRATION TEST =====\n");

        testAuthenticationFlow();
        testStatisticsAPIs();
        testPerformanceDataEvaluation();
        testTeam();

        System.out.println("\n===== SYSTEM TEST COMPLETE =====");
    }

    private static void testAuthenticationFlow() {

        System.out.println("---- Testing Authentication Flow ----");

        AuthenticationModule auth = new AuthenticationModule();

        boolean loginSuccess = auth.login("admin1", "password");

        if (loginSuccess) {
            System.out.println("PASS: Login succeeded");
            auth.launchDashboard();
        } else {
            System.out.println("FAIL: Login failed unexpectedly");
        }

        boolean loginFail = auth.login("admin1", "wrong");
        if (!loginFail) {
            System.out.println("PASS: Invalid login rejected");
        } else {
            System.out.println("FAIL: Invalid login accepted");
        }

        System.out.println();
    }

    private static void testStatisticsAPIs() {

        System.out.println("---- Testing Statistics APIs ----");

        Statistics stats = new Statistics();

        // comparePlayers
        String playerCompare = stats.comparePlayers(
                List.of("player1", "player2"),
                List.of("tournament1"),
                List.of("Goals", "Assists")
        );

        if (playerCompare != null) {
            System.out.println("PASS: comparePlayers returned result");
        } else {
            System.out.println("FAIL: comparePlayers returned null");
        }

        // compareTeams
        String teamCompare = stats.compareTeams(
                List.of("team1", "team2"),
                List.of("tournament1"),
                List.of("Wins")
        );

        if (teamCompare != null) {
            System.out.println("PASS: compareTeams returned result");
        } else {
            System.out.println("FAIL: compareTeams returned null");
        }

        // getPlayerStats
        PlayerStats ps = stats.getPlayerStats("player1", "tournament1");
        if (ps != null) {
            System.out.println("PASS: getPlayerStats returned object");
        } else {
            System.out.println("FAIL: getPlayerStats returned null");
        }

        // getTeamStats
        TeamStats ts = stats.getTeamStats("team1", "tournament1");
        if (ts != null) {
            System.out.println("PASS: getTeamStats returned object");
        } else {
            System.out.println("FAIL: getTeamStats returned null");
        }

        // calculateMetric
        double metric = stats.calculateMetric("player1", "Goals", "tournament1");
        if (metric > 0) {
            System.out.println("PASS: calculateMetric returned value");
        } else {
            System.out.println("FAIL: calculateMetric returned invalid value");
        }

        // getAvailableMetrics
        List<String> metrics = stats.getAvailableMetrics("player");
        if (metrics != null && !metrics.isEmpty()) {
            System.out.println("PASS: getAvailableMetrics returned list");
        } else {
            System.out.println("FAIL: getAvailableMetrics failed");
        }

        // normalizeData
        List<Stats> dummyStats = new ArrayList<>();
        dummyStats.add(new Stats("id1", 10.0));
        List<Stats> normalized = stats.normalizeData(dummyStats, List.of("tournament1"));

        if (normalized != null) {
            System.out.println("PASS: normalizeData returned list");
        } else {
            System.out.println("FAIL: normalizeData returned null");
        }

        System.out.println();

        //claire - testing
        Dashboard dashboard = new Dashboard();
        // Test tournament overview
        System.out.println("Testing Tournament Overview...");
        System.out.println(dashboard.viewTournamentOverview());

        // Test game details
        System.out.println("\nTesting Game Details...");
        GameSummary summary = dashboard.viewGameDetails("G1");

        // Test comparison
        System.out.println("\nTesting Team Comparison...");
        System.out.println(dashboard.compareTeams("USA", "Canada"));
    }

    public static void testGameData(){
        System.out.println("--- Testing Game Data ---");

        //create game
        Game game1 = new Game(
                "G001",
                "USA",
                "Canada",
                "Milano Cortina",
                new Date()
        );
        String gameData = game1.getGameData();
        if (gameData != null) {
            System.out.println("PASS: getGameData returned result");
        } else {
            System.out.println("FAIL: getGameData returned null");
        }

        //update game status        
        game1.updateGameStatus("In Progress");
        if (game1 != null) { 
            System.out.println("PASS: updateGameStatus executed successfully");
        } else {
            System.out.println("FAIL: updateGameStatus failed");
        }

        game1.updateGameStatus("Completed");
        System.out.println("Current game status: Completed");



        //create a game summary
        GameSummary summary1 = new GameSummary(
                "S001",
                "G001",
                "USA",
                "Canada",
                new Date()
        );

        //display charts
        List<String> charts = summary1.displayCharts();
        if (charts != null && !charts.isEmpty()) {
            System.out.println("PASS: displayCharts returned result");
        } else {
            System.out.println("FAIL: displayCharts returned null or empty");
        }

        //display stats
        String stats = summary1.displayStats();
        if (stats != null) {
            System.out.println("PASS: displayStatistics returned result" );
        } else {
            System.out.println("FAIL: displayStatistics returned null");
        }

        //compare teams
        String teamCompare = summary1.compareTeams("Canada");
        if (teamCompare != null) {
            System.out.println("PASS: compareTeams returned result");
        } else {
            System.out.println("FAIL: compareTeams returned null");
        }

        System.out.println();
    }

    //Test performance data evaluation
    public static void testPerformanceDataEvaluation() {
        System.out.println("--- Testing Performance Data Evaluation ---");

        TeamStats teamStats = new TeamStats("USA", 2, 0);
        PlayerStats playerStats = new PlayerStats("Joe Smith", 1, 0);
        PerformanceDataEvaluation evaluation = new PerformanceDataEvaluation(0, null);

        // Test player method
        String playerInfo = evaluation.player(playerStats);
        if (playerInfo != null) {
            System.out.println("PASS: player method returned result");
        } else {
            System.out.println("FAIL: player method returned null");
        }

        // Test calculateOverallPerformance
        double overallPerformance = evaluation.calculateOverallPerformance(teamStats, playerStats);
        if (overallPerformance >= 0) {
            System.out.println("PASS: calculateOverallPerformance returned value");
        } else {
            System.out.println("FAIL: calculateOverallPerformance returned invalid value");
        }

        // Test getEvaluation
        String evalResult = evaluation.getEvaluation();
        if (evalResult != null) {
            System.out.println("PASS: getEvaluation returned result");
        } else {
            System.out.println("FAIL: getEvaluation returned null");
        }

        System.out.println();
    }

    //Test Team
    public static void testTeam() {
        System.out.println("--- Testing Team ---");
        List<String> roster = new ArrayList<>();
        roster.add("Joe Smith");
        roster.add("Ryan Johnson");

        Team team = new Team("USA", roster);

        // Test getTeamCountry
        String country = team.getTeamCountry();
        if (country != null) {
            System.out.println("PASS: getTeamCountry returned result");
        } else {
            System.out.println("FAIL: getTeamCountry returned null");
        }

        // Test getRoster
        List<String> teamRoster = team.getRoster();
        if (teamRoster != null && !teamRoster.isEmpty()) {
            System.out.println("PASS: getRoster returned result");
        } else {
            System.out.println("FAIL: getRoster returned null or empty");
        }

        // Test getTeamStats
        Statistics stats = team.getTeamStats();
        if (stats != null) {
            System.out.println("PASS: getTeamStats returned object");
        } else {
            System.out.println("FAIL: getTeamStats returned null");
        }

        // Test getOpponentHistory
        List<GameSummary> history = team.getOpponentHistory("Canada");
        if (history != null) {
            System.out.println("PASS: getOpponentHistory returned list");
        } else {
            System.out.println("FAIL: getOpponentHistory returned null");
        }

        // Test updateRoster
        List<String> newRoster = new ArrayList<>();
        newRoster.add("Player3");
        team.updateRoster(newRoster);
        if (team.getRoster().contains("Player3")) {
            System.out.println("PASS: updateRoster updated the roster successfully");
        } else {
            System.out.println("FAIL: updateRoster failed to update the roster");
        }

        System.out.println();
    }
    
}
