package src.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import src.model.Game;
import src.model.GameSummary;

public class GameController {
    private List<Game> games;
    private List<GameSummary> summaries;

    public GameController(){
        this.games = new ArrayList<>();
        this.summaries = new ArrayList<>();
    }

    public void gameDetails(String gameID, String teamCountry, String opponentTeam,
        String location, Date date){
            Game game = new Game(gameID, teamCountry,opponentTeam,location, date);
            game.updateGameStatus("Scheduled");
            games.add(game);
            System.out.println("Recent game: " + gameID  +"\n" + teamCountry + " vs " + opponentTeam);
            game.getGameData();
        }


    public void setGameStatus(String gameID, String status){
        for(Game game : games){
            if(game.getGameID().equals(gameID)){
                game.updateGameStatus(status);
                return;
            }
        }
        System.out.println("Game not found: " + gameID);
    }

    public void createGameSummary(String summaryID, String gameID, String teamCountry,
        String opponentTeam, Date date){
            GameSummary summary = new GameSummary(summaryID, gameID, teamCountry, opponentTeam, date);
            summaries.add(summary);
            System.out.println("Game summary created for: " + gameID + "\n" + teamCountry + " vs" + opponentTeam);
        }

    public void displayCharts(String summaryID){
        for (GameSummary summary : summaries) {
            if(summary.getSummaryID().equals(summaryID)){
                System.out.println("Charts: " + summary.displayCharts());
                return;
            }
        }
         System.out.println("Summary not found: " + summaryID);
    }

    public void displayPerformancePatterns(String summaryID){
        for (GameSummary summary : summaries) {
            if(summary.getSummaryID().equals(summaryID)){
                System.out.println("Patterns: " + summary.displayCharts());
                return;
            }
        }
         System.out.println("Summary not found: " + summaryID);
    }

     public void displayStats(String summaryID){
        for (GameSummary summary : summaries) {
            if(summary.getSummaryID().equals(summaryID)){
                System.out.println("Stats: " + summary.displayStats());
                return;
            }
        }
         System.out.println("Summary not found: " + summaryID);
    }

    public void compareTeams(String summaryID, String opponentTeam){
        for (GameSummary summary : summaries) {
            if(summary.getSummaryID().equals(summaryID)){
                System.out.println(summary.compareTeams(opponentTeam));
                return;
            }
        }
         System.out.println("Summary not found: " + summaryID);
    }





}
