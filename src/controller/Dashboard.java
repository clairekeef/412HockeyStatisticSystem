package src.controller;

import java.util.HashMap;
import java.util.Map;

public class Dashboard {

    public Map<String,Object> viewTournamentOverview() {

        System.out.println("Dashboard: Loading tournament overview");

        Map<String, Object> overview = new HashMap<>();

        overview.put("TopTeam", "USA");
        overview.put("TotalGamesPlayed", 12);
        overview.put("CurrentLeader", "Canada");

        return overview;

    }

     /* public GameSummary viewGameDetails(String gameId) {

        System.out.println("Dashboard: Viewing details for game " + gameId);

        // Stubbed data
        return new GameSummary("USA vs Canada", "3-2", "Overtime thriller");
    }
    */

    public String viewPlayerStatistics(String playerId) {

        System.out.println("Dashboard: Viewing statistics for player " + playerId);

        // Stub return
        return "Goals: 4, Assists: 2, Points: 6";
    }

    public String compareTeams(String teamId1, String teamId2) {

        System.out.println("Dashboard: Comparing " + teamId1 + " vs " + teamId2);

        // Stub comparison
        return teamId1 + " has scored 10 goals. " +
               teamId2 + " has scored 8 goals.";
    }
}

    

