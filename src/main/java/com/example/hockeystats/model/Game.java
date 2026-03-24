package com.example.hockeystats.model;

import java.util.Date;

public class Game {
    private String gameID;
    private Date date;
    private String location;
    private String teamCountry;
    private String opponentTeam;
    private String status;

    public Game(String gameID, String teamCountry, String opponentTeam, String location, Date date){
        this.gameID = gameID;
        this.date = date;
        this.location = location;
        this.teamCountry = teamCountry;
        this.opponentTeam = opponentTeam;
    }

    public String getGameData(){
        System.out.println("Getting game data...");
        return "stats";
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


    @Override
    public String toString(){
        return "*Game* \n" + "Game ID: " + gameID + "\nCountry: " + teamCountry 
        +  "\nOpponent: " + opponentTeam + "\nLocation: " + location + "\nDate: " + date.toString();
    }
    
    public void updateGameStatus(String status) {
    this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
