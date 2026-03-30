package com.example.hockeystats.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single Olympic Hockey game, including scheduling info,
 * team matchup, current status, and score.
 *
 * Sample hardcoded games are available via Game.getSampleGames().
 */
public class Game {

    public enum Status { SCHEDULED, IN_PROGRESS, FINAL }

    private final int    gameId;
    private final String homeTeam;
    private final String awayTeam;
    private final String date;        // e.g. "2026-02-10"
    private final String time;        // e.g. "14:00"
    private final String venue;
    private final String round;       // e.g. "Group A", "Quarterfinal", "Final"
    private       Status status;
    private       int    homeScore;
    private       int    awayScore;
    private       int    period;      // 1, 2, 3, or 0 if not started; 4 = OT
    private       String timeRemaining; // e.g. "12:34" or "FINAL"

    public Game(int gameId, String homeTeam, String awayTeam,
                String date, String time, String venue, String round,
                Status status, int homeScore, int awayScore,
                int period, String timeRemaining) {
        this.gameId        = gameId;
        this.homeTeam      = homeTeam;
        this.awayTeam      = awayTeam;
        this.date          = date;
        this.time          = time;
        this.venue         = venue;
        this.round         = round;
        this.status        = status;
        this.homeScore     = homeScore;
        this.awayScore     = awayScore;
        this.period        = period;
        this.timeRemaining = timeRemaining;
    }

    // ── Getters ─────────────────────────────────────────────────────────────

    public int    getGameId()        { return gameId; }
    public String getHomeTeam()      { return homeTeam; }
    public String getAwayTeam()      { return awayTeam; }
    public String getDate()          { return date; }
    public String getTime()          { return time; }
    public String getVenue()         { return venue; }
    public String getRound()         { return round; }
    public Status getStatus()        { return status; }
    public int    getHomeScore()     { return homeScore; }
    public int    getAwayScore()     { return awayScore; }
    public int    getPeriod()        { return period; }
    public String getTimeRemaining() { return timeRemaining; }

    // ── Setters (for live game updates) ─────────────────────────────────────

    public void setStatus(Status status)               { this.status = status; }
    public void setHomeScore(int homeScore)             { this.homeScore = homeScore; }
    public void setAwayScore(int awayScore)             { this.awayScore = awayScore; }
    public void setPeriod(int period)                   { this.period = period; }
    public void setTimeRemaining(String timeRemaining)  { this.timeRemaining = timeRemaining; }

    // ── Derived helpers ──────────────────────────────────────────────────────

    /** Returns true if the game has ended. */
    public boolean isComplete() {
        return status == Status.FINAL;
    }

    /**
     * Returns the winning team's name, or "TIE" if scores are equal.
     * Returns null if the game is not yet complete.
     */
    public String getWinner() {
        if (!isComplete()) return null;
        if (homeScore > awayScore) return homeTeam;
        if (awayScore > homeScore) return awayTeam;
        return "TIE";
    }

    /** Returns the period as a readable label: "1st", "2nd", "3rd", "OT", or "Not Started". */
    public String getPeriodLabel() {
        switch (period) {
            case 1:  return "1st Period";
            case 2:  return "2nd Period";
            case 3:  return "3rd Period";
            case 4:  return "Overtime";
            default: return "Not Started";
        }
    }

    @Override
    public String toString() {
        return String.format("Game #%d | %s | %s vs %s | %d-%d | %s | %s",
            gameId, round, homeTeam, awayTeam, homeScore, awayScore,
            status, isComplete() ? "FINAL" : getPeriodLabel() + " " + timeRemaining);
    }

    // ── Sample hardcoded data ────────────────────────────────────────────────

    /**
     * Returns a list of sample Olympic hockey games with hardcoded data.
     * Used for development and testing until the database is connected.
     */
    public static List<Game> getSampleGames() {
        List<Game> games = new ArrayList<>();

        // Group stage games (completed)
        games.add(new Game(1, "Canada",  "USA",        "2026-02-10", "10:00", "Ice Arena A", "Group A", Status.FINAL,       4, 2, 3, "FINAL"));
        games.add(new Game(2, "Sweden",  "Finland",    "2026-02-10", "14:00", "Ice Arena A", "Group A", Status.FINAL,       3, 1, 3, "FINAL"));
        games.add(new Game(3, "Russia",  "Czech Rep.", "2026-02-11", "10:00", "Ice Arena B", "Group B", Status.FINAL,       2, 2, 4, "FINAL"));
        games.add(new Game(4, "Germany", "Slovakia",   "2026-02-11", "14:00", "Ice Arena B", "Group B", Status.FINAL,       1, 3, 3, "FINAL"));
        games.add(new Game(5, "Canada",  "Sweden",     "2026-02-13", "10:00", "Ice Arena A", "Group A", Status.FINAL,       5, 2, 3, "FINAL"));
        games.add(new Game(6, "USA",     "Finland",    "2026-02-13", "14:00", "Ice Arena A", "Group A", Status.FINAL,       3, 3, 4, "FINAL"));

        // Playoff games (completed)
        games.add(new Game(7, "Canada",  "Germany",    "2026-02-17", "16:00", "Main Arena",  "Quarterfinal", Status.FINAL,  4, 1, 3, "FINAL"));
        games.add(new Game(8, "Sweden",  "Russia",     "2026-02-17", "20:00", "Main Arena",  "Quarterfinal", Status.FINAL,  2, 3, 4, "FINAL"));

        // Semifinal (in progress)
        games.add(new Game(9, "Canada",  "Russia",     "2026-02-20", "18:00", "Main Arena",  "Semifinal", Status.IN_PROGRESS, 2, 1, 2, "08:42"));

        // Final (scheduled)
        games.add(new Game(10, "TBD",   "TBD",        "2026-02-22", "20:00", "Main Arena",  "Final", Status.SCHEDULED,   0, 0, 0, "--:--"));

        return games;
    }

    /**
     * Returns a single sample game by its ID, or null if not found.
     */
    public static Game getSampleGameById(int id) {
        for (Game g : getSampleGames()) {
            if (g.getGameId() == id) return g;
        }
        return null;
    }
}
