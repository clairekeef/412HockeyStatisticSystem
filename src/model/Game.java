package src.model;

import java.util.Date;

public class Game {
    private String gameID;
    private Date date;
    private String location;
    private String teamCountry;
    private String opponentTeam;
    private String gameStatus;

    public Game(String gameID, String teamCountry, String opponentTeam, String location, Date date){
        this.gameID = gameID;
        this.date = date;
        this.location = location;
        this.teamCountry = teamCountry;
        this.opponentTeam = opponentTeam;
        this.gameStatus = "Scheduled";
    }

    public String getGameData(){
        System.out.println("Getting game data...");
        return "stats";
    }

    public void updateGameStatus(String gameStatus){
        this.gameStatus = gameStatus;
        System.out.println("Game status updated to: " + gameStatus);
    }

    public String getGameID(){
        return gameID;
    }

    public Date getDate(){
        return date;
    }
    public String getLocation() {
        return location;
    }

    public String getTeamCountry() {
        return teamCountry;
    }

    public String getOpponentTeam() {
        return opponentTeam;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    @Override
    public String toString(){
        return "*Game* \n" + "Game ID: " + gameID + "\nCountry: " + teamCountry 
        +  "\nOpponent: " + opponentTeam + "\nStatus: " + gameStatus;
    }

}
