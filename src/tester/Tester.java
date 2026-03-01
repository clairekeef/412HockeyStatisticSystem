package src.tester;

import src.controller.AuthenticationModule;
import src.model.*;
import src.controller.*;

import java.util.List;
import java.util.ArrayList;

public class Tester {

    public static void main(String[] args) {

        System.out.println("===== STARTING SYSTEM INTEGRATION TEST =====\n");

        testAuthenticationFlow();
        testStatisticsAPIs();

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
        //GameSummary summary = dashboard.viewGameDetails("G1");
        //System.out.println(summary.getMatchup());
        //System.out.println(summary.getScore());
        //System.out.println(summary.getHighlight());

        // Test comparison
        System.out.println("\nTesting Team Comparison...");
        System.out.println(dashboard.compareTeams("USA", "Canada"));
    }
}
