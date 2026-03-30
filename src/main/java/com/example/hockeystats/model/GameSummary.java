package com.example.hockeystats.model;

import java.util.*;

/**
 * Generates a detailed summary of a hockey game, including shot statistics,
 * power play efficiency, period-by-period scoring, and performance insights.
 *
 * Primarily used by coaches (for strategy review) and analysts (for comparison).
 *
 * Sample summaries tied to Game.getSampleGames() are available via
 * GameSummary.getSampleSummary(gameId).
 */
public class GameSummary {

    // ── Inner class: per-team in-game stats ──────────────────────────────────

    public static class TeamGameStats {
        private final String team;
        private final int shotsOnGoal;
        private final int hits;
        private final int powerPlayOpportunities;
        private final int powerPlayGoals;
        private final int faceoffWins;
        private final int faceoffTotal;
        private final int penaltyMinutes;
        private final int blockedShots;
        private final int turnovers;

        public TeamGameStats(String team, int shotsOnGoal, int hits,
                             int powerPlayOpportunities, int powerPlayGoals,
                             int faceoffWins, int faceoffTotal,
                             int penaltyMinutes, int blockedShots, int turnovers) {
            this.team                   = team;
            this.shotsOnGoal            = shotsOnGoal;
            this.hits                   = hits;
            this.powerPlayOpportunities = powerPlayOpportunities;
            this.powerPlayGoals         = powerPlayGoals;
            this.faceoffWins            = faceoffWins;
            this.faceoffTotal           = faceoffTotal;
            this.penaltyMinutes         = penaltyMinutes;
            this.blockedShots           = blockedShots;
            this.turnovers              = turnovers;
        }

        public String getTeam()                   { return team; }
        public int getShotsOnGoal()               { return shotsOnGoal; }
        public int getHits()                      { return hits; }
        public int getPowerPlayOpportunities()    { return powerPlayOpportunities; }
        public int getPowerPlayGoals()            { return powerPlayGoals; }
        public int getFaceoffWins()               { return faceoffWins; }
        public int getFaceoffTotal()              { return faceoffTotal; }
        public int getPenaltyMinutes()            { return penaltyMinutes; }
        public int getBlockedShots()              { return blockedShots; }
        public int getTurnovers()                 { return turnovers; }

        /** Power play efficiency as a percentage, or 0 if no opportunities. */
        public double getPowerPlayPct() {
            if (powerPlayOpportunities == 0) return 0.0;
            return (powerPlayGoals * 100.0) / powerPlayOpportunities;
        }

        /** Faceoff win percentage, or 0 if no faceoffs recorded. */
        public double getFaceoffPct() {
            if (faceoffTotal == 0) return 0.0;
            return (faceoffWins * 100.0) / faceoffTotal;
        }
    }

    // ── Inner class: a single goal event ────────────────────────────────────

    public static class GoalEvent {
        private final String team;
        private final String scorer;
        private final String assist;    // "Unassisted" if none
        private final int    period;
        private final String time;      // e.g. "14:22"
        private final boolean isPowerPlay;

        public GoalEvent(String team, String scorer, String assist,
                         int period, String time, boolean isPowerPlay) {
            this.team        = team;
            this.scorer      = scorer;
            this.assist      = assist;
            this.period      = period;
            this.time        = time;
            this.isPowerPlay = isPowerPlay;
        }

        public String  getTeam()        { return team; }
        public String  getScorer()      { return scorer; }
        public String  getAssist()      { return assist; }
        public int     getPeriod()      { return period; }
        public String  getTime()        { return time; }
        public boolean isPowerPlay()    { return isPowerPlay; }

        @Override
        public String toString() {
            String ppTag = isPowerPlay ? " (PP)" : "";
            return String.format("P%d %s — %s (%s)%s", period, time, scorer, assist, ppTag);
        }
    }

    // ── GameSummary fields ───────────────────────────────────────────────────

    private final Game          game;
    private final TeamGameStats homeStats;
    private final TeamGameStats awayStats;
    private final List<GoalEvent> goals;

    public GameSummary(Game game, TeamGameStats homeStats,
                       TeamGameStats awayStats, List<GoalEvent> goals) {
        this.game      = game;
        this.homeStats = homeStats;
        this.awayStats = awayStats;
        this.goals     = Collections.unmodifiableList(new ArrayList<>(goals));
    }

    // ── Basic getters ────────────────────────────────────────────────────────

    public Game          getGame()      { return game; }
    public TeamGameStats getHomeStats() { return homeStats; }
    public TeamGameStats getAwayStats() { return awayStats; }
    public List<GoalEvent> getGoals()   { return goals; }

    // ── Analysis methods ─────────────────────────────────────────────────────

    /**
     * Returns a formatted shot comparison string.
     * e.g. "Canada: 34 SOG  |  USA: 28 SOG"
     */
    public String getShotSummary() {
        return String.format("%s: %d SOG  |  %s: %d SOG",
            homeStats.getTeam(), homeStats.getShotsOnGoal(),
            awayStats.getTeam(), awayStats.getShotsOnGoal());
    }

    /**
     * Returns a formatted power play summary.
     * e.g. "Canada PP: 2/4 (50.0%)  |  USA PP: 1/3 (33.3%)"
     */
    public String getPowerPlaySummary() {
        return String.format("%s PP: %d/%d (%.1f%%)  |  %s PP: %d/%d (%.1f%%)",
            homeStats.getTeam(), homeStats.getPowerPlayGoals(), homeStats.getPowerPlayOpportunities(), homeStats.getPowerPlayPct(),
            awayStats.getTeam(), awayStats.getPowerPlayGoals(), awayStats.getPowerPlayOpportunities(), awayStats.getPowerPlayPct());
    }

    /**
     * Returns goals grouped by period.
     * Keys are 1, 2, 3, 4 (OT). Values are lists of GoalEvent.
     */
    public Map<Integer, List<GoalEvent>> getGoalsByPeriod() {
        Map<Integer, List<GoalEvent>> byPeriod = new LinkedHashMap<>();
        for (int p = 1; p <= 4; p++) byPeriod.put(p, new ArrayList<>());
        for (GoalEvent g : goals) {
            byPeriod.get(g.getPeriod()).add(g);
        }
        return byPeriod;
    }

    /**
     * Returns a per-period score summary as a formatted string.
     * e.g. "Period 1: Canada 1 - USA 0  |  Period 2: Canada 2 - USA 1  ..."
     */
    public String getPeriodScoreSummary() {
        Map<Integer, List<GoalEvent>> byPeriod = getGoalsByPeriod();
        StringBuilder sb = new StringBuilder();
        int homeCumulative = 0, awayCumulative = 0;
        String[] labels = {"1st", "2nd", "3rd", "OT"};
        for (int p = 1; p <= 4; p++) {
            List<GoalEvent> periodGoals = byPeriod.get(p);
            if (periodGoals.isEmpty() && p == 4) continue; // skip OT if no OT goals
            for (GoalEvent g : periodGoals) {
                if (g.getTeam().equals(game.getHomeTeam())) homeCumulative++;
                else awayCumulative++;
            }
            if (sb.length() > 0) sb.append("  |  ");
            sb.append(String.format("%s: %s %d - %s %d",
                labels[p - 1], game.getHomeTeam(), homeCumulative,
                game.getAwayTeam(), awayCumulative));
        }
        return sb.toString();
    }

    /**
     * Returns the name of the player with the most goals in this game,
     * or "N/A" if no goals were scored.
     */
    public String getGameMVP() {
        Map<String, Integer> goalCount = new LinkedHashMap<>();
        for (GoalEvent g : goals) {
            goalCount.merge(g.getScorer(), 1, Integer::sum);
        }
        return goalCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
    }

    /**
     * Returns bullet-point performance insights for coaches and analysts.
     * Covers shot dominance, power play efficiency, faceoff control, and physicality.
     */
    public List<String> getPerformanceInsights() {
        List<String> insights = new ArrayList<>();

        // Shot dominance
        int shotDiff = homeStats.getShotsOnGoal() - awayStats.getShotsOnGoal();
        if (Math.abs(shotDiff) >= 5) {
            String dominant = shotDiff > 0 ? homeStats.getTeam() : awayStats.getTeam();
            insights.add(dominant + " dominated in shots on goal (" + getShotSummary() + ").");
        } else {
            insights.add("Shot totals were relatively even: " + getShotSummary() + ".");
        }

        // Power play
        if (homeStats.getPowerPlayOpportunities() > 0 || awayStats.getPowerPlayOpportunities() > 0) {
            String ppLeader = homeStats.getPowerPlayPct() >= awayStats.getPowerPlayPct()
                ? homeStats.getTeam() : awayStats.getTeam();
            insights.add(ppLeader + " had the better power play efficiency. " + getPowerPlaySummary() + ".");
        }

        // Faceoffs
        double homeFO = homeStats.getFaceoffPct();
        double awayFO = awayStats.getFaceoffPct();
        if (Math.abs(homeFO - awayFO) >= 10) {
            String foLeader = homeFO >= awayFO ? homeStats.getTeam() : awayStats.getTeam();
            insights.add(String.format("%s controlled faceoffs (%.1f%% vs %.1f%%).",
                foLeader, Math.max(homeFO, awayFO), Math.min(homeFO, awayFO)));
        }

        // Physical play
        int totalHits = homeStats.getHits() + awayStats.getHits();
        if (totalHits >= 60) {
            insights.add("This was a highly physical game with " + totalHits + " total hits.");
        }

        // Penalty discipline
        if (homeStats.getPenaltyMinutes() > 10 || awayStats.getPenaltyMinutes() > 10) {
            insights.add(String.format("Penalty discipline was an issue: %s %d PIM, %s %d PIM.",
                homeStats.getTeam(), homeStats.getPenaltyMinutes(),
                awayStats.getTeam(), awayStats.getPenaltyMinutes()));
        }

        // Goaltending (implied by shots vs score)
        int homeGoals = game.getHomeScore();
        int awayGoals = game.getAwayScore();
        if (homeStats.getShotsOnGoal() > 0 && awayGoals < (homeStats.getShotsOnGoal() * 0.10)) {
            insights.add(game.getAwayTeam() + "'s goaltender was exceptional, stopping most high-volume shots.");
        }
        if (awayStats.getShotsOnGoal() > 0 && homeGoals < (awayStats.getShotsOnGoal() * 0.10)) {
            insights.add(game.getHomeTeam() + "'s goaltender was exceptional, stopping most high-volume shots.");
        }

        return insights;
    }

    /**
     * Returns a complete summary as a Map — useful for REST controllers
     * or Dashboard integration.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("gameId",      game.getGameId());
        map.put("homeTeam",    game.getHomeTeam());
        map.put("awayTeam",    game.getAwayTeam());
        map.put("homeScore",   game.getHomeScore());
        map.put("awayScore",   game.getAwayScore());
        map.put("status",      game.getStatus().toString());
        map.put("round",       game.getRound());
        map.put("shotSummary", getShotSummary());
        map.put("ppSummary",   getPowerPlaySummary());
        map.put("periodScore", getPeriodScoreSummary());
        map.put("mvp",         getGameMVP());
        map.put("insights",    getPerformanceInsights());
        return map;
    }

    /**
     * Prints a full formatted game summary to standard output.
     * Useful for console testing and coach reports.
     */
    public void printSummary() {
        System.out.println("=".repeat(60));
        System.out.println("GAME SUMMARY: " + game.getHomeTeam() + " vs " + game.getAwayTeam());
        System.out.println("Round: " + game.getRound() + "  |  " + game.getDate() + "  |  " + game.getVenue());
        System.out.println("Final Score: " + game.getHomeTeam() + " " + game.getHomeScore()
            + " - " + game.getAwayScore() + " " + game.getAwayTeam());
        System.out.println("-".repeat(60));

        System.out.println("PERIOD SCORING:");
        System.out.println("  " + getPeriodScoreSummary());
        System.out.println();

        System.out.println("GOAL LOG:");
        for (GoalEvent g : goals) {
            System.out.println("  " + g);
        }
        System.out.println();

        System.out.println("TEAM STATS:");
        System.out.printf("  %-28s %-10s %-10s%n", "", homeStats.getTeam(), awayStats.getTeam());
        System.out.printf("  %-28s %-10d %-10d%n", "Shots on Goal",       homeStats.getShotsOnGoal(),         awayStats.getShotsOnGoal());
        System.out.printf("  %-28s %-10d %-10d%n", "Hits",                homeStats.getHits(),                awayStats.getHits());
        System.out.printf("  %-28s %-10s %-10s%n", "Power Play",
            homeStats.getPowerPlayGoals() + "/" + homeStats.getPowerPlayOpportunities(),
            awayStats.getPowerPlayGoals() + "/" + awayStats.getPowerPlayOpportunities());
        System.out.printf("  %-28s %-9.1f%% %-9.1f%%%n", "Faceoff %",       homeStats.getFaceoffPct(),          awayStats.getFaceoffPct());
        System.out.printf("  %-28s %-10d %-10d%n", "Penalty Minutes",     homeStats.getPenaltyMinutes(),       awayStats.getPenaltyMinutes());
        System.out.printf("  %-28s %-10d %-10d%n", "Blocked Shots",       homeStats.getBlockedShots(),         awayStats.getBlockedShots());
        System.out.println();

        System.out.println("GAME MVP: " + getGameMVP());
        System.out.println();

        System.out.println("PERFORMANCE INSIGHTS:");
        for (String insight : getPerformanceInsights()) {
            System.out.println("  - " + insight);
        }
        System.out.println("=".repeat(60));
    }

    // ── Sample hardcoded data ────────────────────────────────────────────────

    /**
     * Returns a sample GameSummary for Game #1 (Canada 4 - USA 2).
     */
    public static GameSummary getSampleSummary(int gameId) {
        Game game = Game.getSampleGameById(gameId);
        if (game == null) return null;

        switch (gameId) {
            case 1: return buildSummaryGame1(game);
            case 2: return buildSummaryGame2(game);
            case 7: return buildSummaryGame7(game);
            default: return buildGenericSummary(game);
        }
    }

    // Canada 4 - USA 2 (Group A)
    private static GameSummary buildSummaryGame1(Game game) {
        TeamGameStats home = new TeamGameStats("Canada", 34, 28, 4, 2, 31, 58, 8, 14, 9);
        TeamGameStats away = new TeamGameStats("USA",    27, 22, 3, 0, 27, 58, 10, 11, 12);

        List<GoalEvent> goals = new ArrayList<>();
        goals.add(new GoalEvent("Canada", "Sidney Crosby",  "Nathan MacKinnon", 1, "07:14", false));
        goals.add(new GoalEvent("Canada", "Nathan MacKinnon","Connor McDavid",  1, "18:42", true));
        goals.add(new GoalEvent("USA",    "Patrick Kane",   "Auston Matthews",  2, "04:55", false));
        goals.add(new GoalEvent("Canada", "Alex Pietrangelo","Sidney Crosby",   2, "11:30", true));
        goals.add(new GoalEvent("USA",    "Auston Matthews", "Patrick Kane",    3, "03:17", false));
        goals.add(new GoalEvent("Canada", "Connor McDavid", "Unassisted",       3, "17:58", false));

        return new GameSummary(game, home, away, goals);
    }

    // Sweden 3 - Finland 1 (Group A)
    private static GameSummary buildSummaryGame2(Game game) {
        TeamGameStats home = new TeamGameStats("Sweden",  30, 24, 3, 1, 33, 60, 6, 16, 7);
        TeamGameStats away = new TeamGameStats("Finland", 22, 19, 2, 0, 27, 60, 8, 13, 10);

        List<GoalEvent> goals = new ArrayList<>();
        goals.add(new GoalEvent("Sweden",  "Erik Karlsson",  "Victor Hedman",    1, "09:03", false));
        goals.add(new GoalEvent("Sweden",  "Victor Hedman",  "Erik Karlsson",    2, "14:21", true));
        goals.add(new GoalEvent("Finland", "Patrik Laine",   "Sebastian Aho",    3, "02:48", false));
        goals.add(new GoalEvent("Sweden",  "William Nylander","Unassisted",      3, "19:11", false));

        return new GameSummary(game, home, away, goals);
    }

    // Canada 4 - Germany 1 (Quarterfinal)
    private static GameSummary buildSummaryGame7(Game game) {
        TeamGameStats home = new TeamGameStats("Canada",  38, 31, 5, 2, 35, 62, 10, 18, 8);
        TeamGameStats away = new TeamGameStats("Germany", 18, 20, 3, 0, 27, 62, 14, 10, 15);

        List<GoalEvent> goals = new ArrayList<>();
        goals.add(new GoalEvent("Canada",  "Connor McDavid",  "Sidney Crosby",    1, "06:19", false));
        goals.add(new GoalEvent("Canada",  "Sidney Crosby",   "Nathan MacKinnon", 2, "03:44", true));
        goals.add(new GoalEvent("Germany", "Leon Draisaitl",  "Moritz Seider",    2, "12:55", false));
        goals.add(new GoalEvent("Canada",  "Nathan MacKinnon","Connor McDavid",   3, "05:02", true));
        goals.add(new GoalEvent("Canada",  "Alex Pietrangelo","Unassisted",       3, "16:33", false));

        return new GameSummary(game, home, away, goals);
    }

    // Generic summary for games without detailed hardcoded data
    private static GameSummary buildGenericSummary(Game game) {
        TeamGameStats home = new TeamGameStats(game.getHomeTeam(), 25, 20, 2, 1, 30, 60, 6, 12, 8);
        TeamGameStats away = new TeamGameStats(game.getAwayTeam(), 23, 18, 3, 1, 30, 60, 8, 10, 9);

        List<GoalEvent> goals = new ArrayList<>();
        // Add placeholder goals matching the final score
        for (int i = 0; i < game.getHomeScore(); i++) {
            goals.add(new GoalEvent(game.getHomeTeam(), "Player " + (i + 1), "Unassisted",
                (i % 3) + 1, "10:00", false));
        }
        for (int i = 0; i < game.getAwayScore(); i++) {
            goals.add(new GoalEvent(game.getAwayTeam(), "Player " + (i + 1), "Unassisted",
                (i % 3) + 1, "15:00", false));
        }
        goals.sort(Comparator.comparingInt(GoalEvent::getPeriod));

        return new GameSummary(game, home, away, goals);
    }
}
