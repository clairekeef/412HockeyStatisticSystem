package com.example.hockeystats.view;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.hockeystats.model.*;

public class Dashboard {

    public Map<String,Object> viewTournamentOverview() {

        System.out.println("Dashboard: Loading tournament overview");

        Map<String, Object> overview = new HashMap<>();

        overview.put("TopTeam", "USA");
        overview.put("TotalGamesPlayed", 12);
        overview.put("CurrentLeader", "Canada");

        return overview;

    }

     public GameSummary viewGameDetails(String gameId) {

        System.out.println("Dashboard: Viewing details for game " + gameId);

        // Stubbed data
        return new GameSummary("sum1", "game1", "USA","Canada",new Date());

    }
    

    public String viewPlayerStatistics(String playerName, List<PlayerStats> players) {
    return players.stream()
        .filter(p -> p.getName().equalsIgnoreCase(playerName))
        .map(PlayerStats::toString)
        .findFirst()
        .orElse("Player not found");
}

public String compareTeams(String team1, String team2, List<PlayerStats> players) {
    int goalsTeam1 = players.stream()
        .filter(p -> p.getCountry().equalsIgnoreCase(team1))
        .mapToInt(PlayerStats::getGoals).sum();
    int goalsTeam2 = players.stream()
        .filter(p -> p.getCountry().equalsIgnoreCase(team2))
        .mapToInt(PlayerStats::getGoals).sum();

    return String.format("%s: %d goals | %s: %d goals", team1, goalsTeam1, team2, goalsTeam2);
}
}

    

