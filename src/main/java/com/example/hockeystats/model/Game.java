package com.example.hockeystats.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single Olympic Hockey game.
 * Field names mirror the Supabase `results` table columns.
 */
public class Game {

    private final int    gameId;
    private final String teamH;        // home team name  (teamH in Supabase)
    private final String teamA;        // away team name  (teamA in Supabase)
    private final String time;         // full timestamp  (time in Supabase)
    private final String location;     // venue           (location in Supabase)
    private final String eventStage;   // round/stage     (event_stage in Supabase)
    private       String eventStatus;  // game status     (event_status in Supabase: SCHEDULED, LIVE, FINAL)
    private       int    scoreTH;      // home score      (scoreTH in Supabase)
    private       int    scoreTA;      // away score      (scoreTA in Supabase)
    private final String teamHCode;    // home team code  (teamH_code in Supabase)
    private final String teamACode;    // away team code  (teamA_code in Supabase)
    private final String sex;          // gender          (sex in Supabase: M or W)

    public Game(int gameId, String teamH, String teamA,
                String time, String location, String eventStage,
                String eventStatus, int scoreTH, int scoreTA,
                String teamHCode, String teamACode, String sex) {
        this.gameId      = gameId;
        this.teamH       = teamH;
        this.teamA       = teamA;
        this.time        = time;
        this.location    = location;
        this.eventStage  = eventStage;
        this.eventStatus = eventStatus;
        this.scoreTH     = scoreTH;
        this.scoreTA     = scoreTA;
        this.teamHCode   = teamHCode;
        this.teamACode   = teamACode;
        this.sex         = sex;
    }

    // ── Getters ─────────────────────────────────────────────────────────────

    public int    getGameId()      { return gameId; }
    public String getTeamH()       { return teamH; }
    public String getTeamA()       { return teamA; }
    public String getTime()        { return time; }
    public String getLocation()    { return location; }
    public String getEventStage()  { return eventStage; }
    public String getEventStatus() { return eventStatus; }
    public int    getScoreTH()     { return scoreTH; }
    public int    getScoreTA()     { return scoreTA; }
    public String getTeamHCode()   { return teamHCode; }
    public String getTeamACode()   { return teamACode; }
    public String getSex()         { return sex; }

    // ── Setters (for live game updates) ─────────────────────────────────────

    public void setEventStatus(String eventStatus) { this.eventStatus = eventStatus; }
    public void setScoreTH(int scoreTH)            { this.scoreTH = scoreTH; }
    public void setScoreTA(int scoreTA)            { this.scoreTA = scoreTA; }

    // ── Derived helpers ──────────────────────────────────────────────────────

    public boolean isComplete() {
        return "Finished".equalsIgnoreCase(eventStatus);
    }

    /**
     * Returns the winning team's name, or "TIE" if scores are equal.
     * Returns null if the game is not yet complete.
     */
    public String getWinner() {
        if (!isComplete()) return null;
        if (scoreTH > scoreTA) return teamH;
        if (scoreTA > scoreTH) return teamA;
        return "TIE";
    }

    @Override
    public String toString() {
        return String.format("Game #%d | %s | %s vs %s | %d-%d | %s",
            gameId, eventStage, teamH, teamA, scoreTH, scoreTA, eventStatus);
    }

    // ── Sample hardcoded data ────────────────────────────────────────────────

    public static List<Game> getSampleGames() {
        List<Game> games = new ArrayList<>();

        games.add(new Game(1,  "Canada",  "USA",        "2026-02-10T10:00", "Ice Arena A", "Group A",    "Finished",     4, 2, "CAN", "USA", "M"));
        games.add(new Game(2,  "Sweden",  "Finland",    "2026-02-10T14:00", "Ice Arena A", "Group A",    "Finished",     3, 1, "SWE", "FIN", "M"));
        games.add(new Game(3,  "Russia",  "Czech Rep.", "2026-02-11T10:00", "Ice Arena B", "Group B",    "Finished",     2, 2, "RUS", "CZE", "M"));
        games.add(new Game(4,  "Germany", "Slovakia",   "2026-02-11T14:00", "Ice Arena B", "Group B",    "Finished",     1, 3, "GER", "SVK", "M"));
        games.add(new Game(5,  "Canada",  "Sweden",     "2026-02-13T10:00", "Ice Arena A", "Group A",    "Finished",     5, 2, "CAN", "SWE", "M"));
        games.add(new Game(6,  "USA",     "Finland",    "2026-02-13T14:00", "Ice Arena A", "Group A",    "Finished",     3, 3, "USA", "FIN", "M"));
        games.add(new Game(7,  "Canada",  "Germany",    "2026-02-17T16:00", "Main Arena",  "Quarterfinal","Finished",    4, 1, "CAN", "GER", "M"));
        games.add(new Game(8,  "Sweden",  "Russia",     "2026-02-17T20:00", "Main Arena",  "Quarterfinal","Finished",    2, 3, "SWE", "RUS", "M"));
        games.add(new Game(9,  "Canada",  "Russia",     "2026-02-20T18:00", "Main Arena",  "Semifinal",  "Live",      2, 1, "CAN", "RUS", "M"));
        games.add(new Game(10, "TBD",     "TBD",        "2026-02-22T20:00", "Main Arena",  "Final",      "Scheduled", 0, 0, "",    "",    "M"));

        return games;
    }

    public static Game getSampleGameById(int id) {
        for (Game g : getSampleGames()) {
            if (g.getGameId() == id) return g;
        }
        return null;
    }
}
