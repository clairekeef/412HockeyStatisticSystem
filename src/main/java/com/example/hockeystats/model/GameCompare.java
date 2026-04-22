package com.example.hockeystats.model;

import java.util.*;

/**
 * Analyzes and compares USA hockey game performance across multiple games.
 * Coach-only feature — provides trend analysis, period patterns, and insights
 * to help coaches identify strengths and weaknesses across a tournament.
 *
 * Sample data is available via GameCompare.getSampleData().
 */
public class GameCompare {

    // ── Inner class: USA-normalized stats for one game ───────────────────────

    public static class GameStats {
        private final String gameId;
        private final String opponent;
        private final int usaShots;
        private final int oppShots;
        private final int usaHits;
        private final int oppHits;
        private final int usaPpGoals;
        private final int usaPpOpps;
        private final int oppPpGoals;
        private final int oppPpOpps;
        private final int usaFaceoffWins;
        private final int usaFaceoffTotal;
        private final int usaPenaltyMinutes;
        private final int oppPenaltyMinutes;
        private final int usaBlockedShots;
        private final int usaTurnovers;
        private final int[] usaGoalsByPeriod; // index: 0=P1, 1=P2, 2=P3, 3=OT
        private final int[] oppGoalsByPeriod;

        public GameStats(String gameId, String opponent,
                         int usaShots, int oppShots,
                         int usaHits, int oppHits,
                         int usaPpGoals, int usaPpOpps,
                         int oppPpGoals, int oppPpOpps,
                         int usaFaceoffWins, int usaFaceoffTotal,
                         int usaPenaltyMinutes, int oppPenaltyMinutes,
                         int usaBlockedShots, int usaTurnovers,
                         int[] usaGoalsByPeriod, int[] oppGoalsByPeriod) {
            this.gameId             = gameId;
            this.opponent           = opponent;
            this.usaShots           = usaShots;
            this.oppShots           = oppShots;
            this.usaHits            = usaHits;
            this.oppHits            = oppHits;
            this.usaPpGoals         = usaPpGoals;
            this.usaPpOpps          = usaPpOpps;
            this.oppPpGoals         = oppPpGoals;
            this.oppPpOpps          = oppPpOpps;
            this.usaFaceoffWins     = usaFaceoffWins;
            this.usaFaceoffTotal    = usaFaceoffTotal;
            this.usaPenaltyMinutes  = usaPenaltyMinutes;
            this.oppPenaltyMinutes  = oppPenaltyMinutes;
            this.usaBlockedShots    = usaBlockedShots;
            this.usaTurnovers       = usaTurnovers;
            this.usaGoalsByPeriod   = usaGoalsByPeriod.clone();
            this.oppGoalsByPeriod   = oppGoalsByPeriod.clone();
        }

        public String getGameId()            { return gameId; }
        public String getOpponent()          { return opponent; }
        public int getUsaShots()             { return usaShots; }
        public int getOppShots()             { return oppShots; }
        public int getUsaHits()              { return usaHits; }
        public int getOppHits()              { return oppHits; }
        public int getUsaPpGoals()           { return usaPpGoals; }
        public int getUsaPpOpps()            { return usaPpOpps; }
        public int getOppPpGoals()           { return oppPpGoals; }
        public int getOppPpOpps()            { return oppPpOpps; }
        public int getUsaFaceoffWins()       { return usaFaceoffWins; }
        public int getUsaFaceoffTotal()      { return usaFaceoffTotal; }
        public int getUsaPenaltyMinutes()    { return usaPenaltyMinutes; }
        public int getOppPenaltyMinutes()    { return oppPenaltyMinutes; }
        public int getUsaBlockedShots()      { return usaBlockedShots; }
        public int getUsaTurnovers()         { return usaTurnovers; }
        public int[] getUsaGoalsByPeriod()   { return usaGoalsByPeriod.clone(); }
        public int[] getOppGoalsByPeriod()   { return oppGoalsByPeriod.clone(); }

        public int getUsaScore() {
            int total = 0;
            for (int g : usaGoalsByPeriod) total += g;
            return total;
        }

        public int getOppScore() {
            int total = 0;
            for (int g : oppGoalsByPeriod) total += g;
            return total;
        }

        public boolean isWin() { return getUsaScore() > getOppScore(); }

        public double getUsaPpPct() {
            return usaPpOpps > 0 ? Math.round((usaPpGoals * 1000.0) / usaPpOpps) / 10.0 : 0.0;
        }

        public double getUsaFaceoffPct() {
            return usaFaceoffTotal > 0 ? Math.round((usaFaceoffWins * 1000.0) / usaFaceoffTotal) / 10.0 : 0.0;
        }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("gameId",              gameId);
            map.put("opponent",            opponent);
            map.put("usaScore",            getUsaScore());
            map.put("oppScore",            getOppScore());
            map.put("isWin",               isWin());
            map.put("usaShots",            usaShots);
            map.put("oppShots",            oppShots);
            map.put("usaHits",             usaHits);
            map.put("oppHits",             oppHits);
            map.put("usaPpGoals",          usaPpGoals);
            map.put("usaPpOpps",           usaPpOpps);
            map.put("usaPpPct",            getUsaPpPct());
            map.put("oppPpGoals",          oppPpGoals);
            map.put("oppPpOpps",           oppPpOpps);
            map.put("usaFaceoffPct",       getUsaFaceoffPct());
            map.put("usaPenaltyMinutes",   usaPenaltyMinutes);
            map.put("oppPenaltyMinutes",   oppPenaltyMinutes);
            map.put("usaBlockedShots",     usaBlockedShots);
            map.put("usaTurnovers",        usaTurnovers);
            List<Integer> up = new ArrayList<>(), op = new ArrayList<>();
            for (int g : usaGoalsByPeriod) up.add(g);
            for (int g : oppGoalsByPeriod) op.add(g);
            map.put("usaGoalsByPeriod",    up);
            map.put("oppGoalsByPeriod",    op);
            return map;
        }

        @Override
        public String toString() {
            return String.format("USA %d-%d %s | SOG: %d | PP: %d/%d",
                getUsaScore(), getOppScore(), opponent, usaShots, usaPpGoals, usaPpOpps);
        }
    }

    // ── GameCompare fields ───────────────────────────────────────────────────

    private final List<GameStats> games;

    public GameCompare(List<GameStats> games) {
        this.games = Collections.unmodifiableList(new ArrayList<>(games));
    }

    public List<GameStats> getGames() { return games; }

    // ── Trend methods ────────────────────────────────────────────────────────

    /** USA shots on goal per game, in order. */
    public List<Integer> getShotTrend() {
        List<Integer> trend = new ArrayList<>();
        for (GameStats g : games) trend.add(g.getUsaShots());
        return trend;
    }

    /** USA power play % per game, in order. */
    public List<Double> getPpTrend() {
        List<Double> trend = new ArrayList<>();
        for (GameStats g : games) trend.add(g.getUsaPpPct());
        return trend;
    }

    /** USA goals scored per game, in order. */
    public List<Integer> getScoreTrend() {
        List<Integer> trend = new ArrayList<>();
        for (GameStats g : games) trend.add(g.getUsaScore());
        return trend;
    }

    /** USA penalty minutes per game, in order. */
    public List<Integer> getPimTrend() {
        List<Integer> trend = new ArrayList<>();
        for (GameStats g : games) trend.add(g.getUsaPenaltyMinutes());
        return trend;
    }

    /** USA faceoff % per game, in order. */
    public List<Double> getFaceoffTrend() {
        List<Double> trend = new ArrayList<>();
        for (GameStats g : games) trend.add(g.getUsaFaceoffPct());
        return trend;
    }

    // ── Period analysis ──────────────────────────────────────────────────────

    /** Total USA goals per period across all games. Index: 0=P1, 1=P2, 2=P3, 3=OT. */
    public int[] getTotalUsaGoalsByPeriod() {
        int[] totals = new int[4];
        for (GameStats g : games) {
            int[] periods = g.getUsaGoalsByPeriod();
            for (int i = 0; i < 4; i++) totals[i] += periods[i];
        }
        return totals;
    }

    /** Total opponent goals per period across all games. */
    public int[] getTotalOppGoalsByPeriod() {
        int[] totals = new int[4];
        for (GameStats g : games) {
            int[] periods = g.getOppGoalsByPeriod();
            for (int i = 0; i < 4; i++) totals[i] += periods[i];
        }
        return totals;
    }

    // ── Aggregate stats ──────────────────────────────────────────────────────

    public double getAvgShots() {
        if (games.isEmpty()) return 0;
        int total = 0;
        for (GameStats g : games) total += g.getUsaShots();
        return Math.round((total * 10.0) / games.size()) / 10.0;
    }

    public double getAvgPpPct() {
        if (games.isEmpty()) return 0;
        double total = 0;
        for (GameStats g : games) total += g.getUsaPpPct();
        return Math.round((total * 10.0) / games.size()) / 10.0;
    }

    public GameStats getBestGame() {
        GameStats best = null;
        for (GameStats g : games) {
            if (best == null || g.getUsaScore() > best.getUsaScore()) best = g;
        }
        return best;
    }

    // ── Coach insights ───────────────────────────────────────────────────────

    /**
     * Returns bullet-point coaching insights derived from tournament trends.
     * Covers period patterns, power play, shot volume, and win/loss record.
     */
    public List<String> getInsights() {
        List<String> insights = new ArrayList<>();
        if (games.isEmpty()) return insights;

        int[] usaPeriods = getTotalUsaGoalsByPeriod();
        String[] periodNames = { "1st", "2nd", "3rd", "OT" };

        // Period pattern
        if (usaPeriods[0] < usaPeriods[2]) {
            insights.add("USA scores more in the 3rd period than the 1st — the team builds momentum as games progress.");
        } else if (usaPeriods[0] > usaPeriods[2]) {
            insights.add("USA starts strong but fades in the 3rd period — conditioning or game management may need attention.");
        } else {
            insights.add("USA's scoring is evenly distributed across periods — consistent offensive output throughout games.");
        }

        int bestPeriodIdx = 0;
        for (int i = 1; i < 3; i++) {
            if (usaPeriods[i] > usaPeriods[bestPeriodIdx]) bestPeriodIdx = i;
        }
        insights.add(String.format("Best scoring period across the tournament: %s period (%d total goals).",
            periodNames[bestPeriodIdx], usaPeriods[bestPeriodIdx]));

        // PP trend
        double firstPp = games.get(0).getUsaPpPct();
        double lastPp  = games.get(games.size() - 1).getUsaPpPct();
        if (lastPp < firstPp - 10) {
            insights.add(String.format(
                "Power play efficiency declined: %.1f%% (game 1) → %.1f%% (game %d). PP units may need adjustment.",
                firstPp, lastPp, games.size()));
        } else if (lastPp > firstPp + 10) {
            insights.add(String.format(
                "Power play improved across the tournament: %.1f%% → %.1f%%. Strong progression.", firstPp, lastPp));
        } else {
            insights.add(String.format(
                "Power play efficiency was consistent across games (avg %.1f%%).", getAvgPpPct()));
        }

        // Shot volume
        insights.add(String.format("USA averaged %.1f shots per game. %s",
            getAvgShots(),
            getAvgShots() >= 30 ? "Strong offensive pressure throughout the tournament."
                                : "Shot volume could be increased — look for more shooting opportunities."));

        // Best game
        GameStats best = getBestGame();
        if (best != null) {
            insights.add(String.format("Best offensive game: vs %s (%d–%d) with %d shots on goal.",
                best.getOpponent(), best.getUsaScore(), best.getOppScore(), best.getUsaShots()));
        }

        // Win/loss record
        int wins = 0;
        for (GameStats g : games) if (g.isWin()) wins++;
        insights.add(String.format("Tournament record: %d–%d. %s",
            wins, games.size() - wins,
            wins >= games.size() / 2 ? "Solid overall performance."
                                     : "More work needed in key areas to improve win rate."));

        return insights;
    }

    // ── toMap ────────────────────────────────────────────────────────────────

    /**
     * Returns a complete representation as a Map — used by REST controllers.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();

        List<Map<String, Object>> gameList = new ArrayList<>();
        for (GameStats g : games) gameList.add(g.toMap());
        map.put("games",        gameList);

        map.put("shotTrend",    getShotTrend());
        map.put("ppTrend",      getPpTrend());
        map.put("scoreTrend",   getScoreTrend());
        map.put("pimTrend",     getPimTrend());
        map.put("faceoffTrend", getFaceoffTrend());
        map.put("avgShots",     getAvgShots());
        map.put("avgPpPct",     getAvgPpPct());
        map.put("insights",     getInsights());

        List<Integer> up = new ArrayList<>(), op = new ArrayList<>();
        for (int g : getTotalUsaGoalsByPeriod()) up.add(g);
        for (int g : getTotalOppGoalsByPeriod()) op.add(g);
        map.put("totalUsaGoalsByPeriod", up);
        map.put("totalOppGoalsByPeriod", op);

        return map;
    }

    /**
     * Prints a formatted comparison report to standard output.
     */
    public void printReport() {
        System.out.println("=".repeat(60));
        System.out.println("USA TOURNAMENT GAME COMPARE REPORT");
        System.out.println("-".repeat(60));
        for (GameStats g : games) System.out.println("  " + g);
        System.out.println();
        System.out.println("TRENDS:");
        System.out.println("  Shots:    " + getShotTrend());
        System.out.println("  PP %:     " + getPpTrend());
        System.out.println("  Goals:    " + getScoreTrend());
        System.out.println("  PIM:      " + getPimTrend());
        System.out.println();
        System.out.println("PERIOD GOALS (USA): ");
        int[] p = getTotalUsaGoalsByPeriod();
        System.out.printf("  P1:%d  P2:%d  P3:%d  OT:%d%n", p[0], p[1], p[2], p[3]);
        System.out.println();
        System.out.println("INSIGHTS:");
        for (String s : getInsights()) System.out.println("  - " + s);
        System.out.println("=".repeat(60));
    }

    // ── Sample data ──────────────────────────────────────────────────────────

    /**
     * Returns sample USA men's tournament data for demonstration and testing.
     */
    public static GameCompare getSampleData() {
        List<GameStats> games = new ArrayList<>();

        // Game 1: USA vs Canada — loss 2-4
        games.add(new GameStats("G001", "Canada",
            27, 34,  22, 28,
            0, 3,    2, 4,
            27, 58,  10, 8,
            11, 12,
            new int[]{1, 1, 0, 0},
            new int[]{2, 1, 1, 0}));

        // Game 2: USA vs Germany — win 5-2
        games.add(new GameStats("G002", "Germany",
            35, 18,  30, 16,
            2, 4,    0, 2,
            33, 60,  6, 14,
            16, 7,
            new int[]{2, 2, 1, 0},
            new int[]{0, 1, 1, 0}));

        // Game 3: USA vs Czech Republic — win 3-1
        games.add(new GameStats("G003", "Czech Rep.",
            31, 22,  26, 19,
            1, 3,    0, 2,
            31, 60,  8, 10,
            14, 9,
            new int[]{0, 2, 1, 0},
            new int[]{1, 0, 0, 0}));

        // Game 4: USA vs Finland — loss 1-3
        games.add(new GameStats("G004", "Finland",
            24, 30,  20, 24,
            0, 4,    1, 3,
            28, 58,  12, 8,
            10, 11,
            new int[]{0, 1, 0, 0},
            new int[]{1, 1, 1, 0}));

        return new GameCompare(games);
    }
}
