package com.example.hockeystats.model;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private String teamCountry;
    private List<String> roster;

    public Team(String teamCountry, List<String> roster) {
        this.teamCountry = teamCountry;
        this.roster = roster;
    }

    public Team(Team other) {
        this.teamCountry = other.teamCountry;
        this.roster = new ArrayList<>(other.roster);
    }

    public Team copy() {
        return new Team(this);
    }

    public String getTeamCountry() {
        return teamCountry;
    }

    public List<String> getRoster() {
        return roster;
    }

    public void updateRoster(List<String> newRoster) {
        this.roster = newRoster;
    }
}