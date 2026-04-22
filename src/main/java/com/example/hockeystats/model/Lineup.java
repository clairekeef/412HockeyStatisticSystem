package com.example.hockeystats.model;

import java.util.*;

/**
 * Represents a coach-built lineup for a USA men's hockey game,
 * including 4 forward lines, 3 defense pairs, and 2 goalies.
 *
 * Coach-only feature — used to plan and save game-day rosters.
 * Sample lineups are available via Lineup.getSampleLineup().
 */
public class Lineup {

    // ── Inner class: a single player slot ───────────────────────────────────

    public static class LineupPlayer {
        private final String name;
        private final String position;
        private final int    goals;
        private final int    assists;
        private final int    points;

        public LineupPlayer(String name, String position, int goals, int assists, int points) {
            this.name     = name;
            this.position = position;
            this.goals    = goals;
            this.assists  = assists;
            this.points   = points;
        }

        public String getName()     { return name; }
        public String getPosition() { return position; }
        public int    getGoals()    { return goals; }
        public int    getAssists()  { return assists; }
        public int    getPoints()   { return points; }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name",     name);
            map.put("position", position);
            map.put("G",        goals);
            map.put("A",        assists);
            map.put("P",        points);
            return map;
        }

        @Override
        public String toString() {
            return String.format("%s (%s) — G:%d A:%d P:%d", name, position, goals, assists, points);
        }
    }

    // ── Inner class: a forward line (LW / C / RW) ───────────────────────────

    public static class ForwardLine {
        private final int          lineNumber;
        private final LineupPlayer leftWing;
        private final LineupPlayer center;
        private final LineupPlayer rightWing;

        public ForwardLine(int lineNumber, LineupPlayer leftWing,
                           LineupPlayer center, LineupPlayer rightWing) {
            this.lineNumber = lineNumber;
            this.leftWing   = leftWing;
            this.center     = center;
            this.rightWing  = rightWing;
        }

        public int          getLineNumber() { return lineNumber; }
        public LineupPlayer getLeftWing()   { return leftWing; }
        public LineupPlayer getCenter()     { return center; }
        public LineupPlayer getRightWing()  { return rightWing; }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("line",     lineNumber);
            map.put("leftWing", leftWing  != null ? leftWing.toMap()  : null);
            map.put("center",   center    != null ? center.toMap()    : null);
            map.put("rightWing",rightWing != null ? rightWing.toMap() : null);
            return map;
        }
    }

    // ── Inner class: a defense pair (LD / RD) ───────────────────────────────

    public static class DefensePair {
        private final int          pairNumber;
        private final LineupPlayer leftDefense;
        private final LineupPlayer rightDefense;

        public DefensePair(int pairNumber, LineupPlayer leftDefense, LineupPlayer rightDefense) {
            this.pairNumber   = pairNumber;
            this.leftDefense  = leftDefense;
            this.rightDefense = rightDefense;
        }

        public int          getPairNumber()   { return pairNumber; }
        public LineupPlayer getLeftDefense()  { return leftDefense; }
        public LineupPlayer getRightDefense() { return rightDefense; }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("pair",         pairNumber);
            map.put("leftDefense",  leftDefense  != null ? leftDefense.toMap()  : null);
            map.put("rightDefense", rightDefense != null ? rightDefense.toMap() : null);
            return map;
        }
    }

    // ── Lineup fields ────────────────────────────────────────────────────────

    private final String             gameLabel;
    private final List<ForwardLine>  forwardLines;
    private final List<DefensePair>  defensePairs;
    private final LineupPlayer       starterGoalie;
    private final LineupPlayer       backupGoalie;

    public Lineup(String gameLabel,
                  List<ForwardLine> forwardLines,
                  List<DefensePair> defensePairs,
                  LineupPlayer starterGoalie,
                  LineupPlayer backupGoalie) {
        this.gameLabel    = gameLabel;
        this.forwardLines = Collections.unmodifiableList(new ArrayList<>(forwardLines));
        this.defensePairs = Collections.unmodifiableList(new ArrayList<>(defensePairs));
        this.starterGoalie = starterGoalie;
        this.backupGoalie  = backupGoalie;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String            getGameLabel()    { return gameLabel; }
    public List<ForwardLine> getForwardLines() { return forwardLines; }
    public List<DefensePair> getDefensePairs() { return defensePairs; }
    public LineupPlayer      getStarterGoalie(){ return starterGoalie; }
    public LineupPlayer      getBackupGoalie() { return backupGoalie; }

    // ── Analysis methods ─────────────────────────────────────────────────────

    /**
     * Returns all players in the lineup as a flat list.
     */
    public List<LineupPlayer> getAllPlayers() {
        List<LineupPlayer> all = new ArrayList<>();
        for (ForwardLine line : forwardLines) {
            if (line.getLeftWing()  != null) all.add(line.getLeftWing());
            if (line.getCenter()    != null) all.add(line.getCenter());
            if (line.getRightWing() != null) all.add(line.getRightWing());
        }
        for (DefensePair pair : defensePairs) {
            if (pair.getLeftDefense()  != null) all.add(pair.getLeftDefense());
            if (pair.getRightDefense() != null) all.add(pair.getRightDefense());
        }
        if (starterGoalie != null) all.add(starterGoalie);
        if (backupGoalie  != null) all.add(backupGoalie);
        return all;
    }

    /**
     * Returns the top scorer (by points) across all forward lines.
     */
    public LineupPlayer getTopScorer() {
        return forwardLines.stream()
            .flatMap(line -> Arrays.stream(new LineupPlayer[]{
                line.getLeftWing(), line.getCenter(), line.getRightWing()
            }))
            .filter(Objects::nonNull)
            .max(Comparator.comparingInt(LineupPlayer::getPoints))
            .orElse(null);
    }

    /**
     * Returns the total points across all skaters in this lineup.
     */
    public int getTotalPoints() {
        return getAllPlayers().stream()
            .filter(p -> !p.getPosition().equals("GK"))
            .mapToInt(LineupPlayer::getPoints)
            .sum();
    }

    /**
     * Returns a formatted summary string of the lineup totals.
     * e.g. "USA Lineup — 20 players, 142 total points"
     */
    public String getLineupSummary() {
        return String.format("USA Lineup (%s) — %d players, %d total points",
            gameLabel != null ? gameLabel : "No game set",
            getAllPlayers().size(),
            getTotalPoints());
    }

    /**
     * Returns a complete representation as a Map — used by REST controllers.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("gameLabel", gameLabel);

        List<Map<String, Object>> lines = new ArrayList<>();
        for (ForwardLine l : forwardLines) lines.add(l.toMap());
        map.put("forwardLines", lines);

        List<Map<String, Object>> pairs = new ArrayList<>();
        for (DefensePair p : defensePairs) pairs.add(p.toMap());
        map.put("defensePairs", pairs);

        map.put("starterGoalie", starterGoalie != null ? starterGoalie.toMap() : null);
        map.put("backupGoalie",  backupGoalie  != null ? backupGoalie.toMap()  : null);
        map.put("totalPlayers",  getAllPlayers().size());
        map.put("totalPoints",   getTotalPoints());

        LineupPlayer top = getTopScorer();
        map.put("topScorer", top != null ? top.toMap() : null);

        return map;
    }

    /**
     * Prints a formatted lineup to standard output.
     * Useful for console testing and coach reports.
     */
    public void printLineup() {
        System.out.println("=".repeat(60));
        System.out.println("USA LINEUP — " + (gameLabel != null ? gameLabel : "No game set"));
        System.out.println(getLineupSummary());
        System.out.println("-".repeat(60));

        System.out.println("FORWARD LINES:");
        for (ForwardLine line : forwardLines) {
            System.out.printf("  Line %d:  LW: %-22s C: %-22s RW: %s%n",
                line.getLineNumber(),
                line.getLeftWing()  != null ? line.getLeftWing().getName()  : "—",
                line.getCenter()    != null ? line.getCenter().getName()    : "—",
                line.getRightWing() != null ? line.getRightWing().getName() : "—");
        }
        System.out.println();

        System.out.println("DEFENSE PAIRS:");
        for (DefensePair pair : defensePairs) {
            System.out.printf("  Pair %d:  LD: %-22s RD: %s%n",
                pair.getPairNumber(),
                pair.getLeftDefense()  != null ? pair.getLeftDefense().getName()  : "—",
                pair.getRightDefense() != null ? pair.getRightDefense().getName() : "—");
        }
        System.out.println();

        System.out.println("GOALIES:");
        System.out.printf("  Starter: %s%n", starterGoalie != null ? starterGoalie.getName() : "—");
        System.out.printf("  Backup:  %s%n", backupGoalie  != null ? backupGoalie.getName()  : "—");
        System.out.println();

        LineupPlayer top = getTopScorer();
        System.out.println("TOP SCORER: " + (top != null ? top.toString() : "—"));
        System.out.println("=".repeat(60));
    }

    // ── Sample data ──────────────────────────────────────────────────────────

    /**
     * Returns a sample USA men's lineup for demonstration and testing.
     */
    public static Lineup getSampleLineup() {
        List<ForwardLine> lines = new ArrayList<>();
        lines.add(new ForwardLine(1,
            new LineupPlayer("A. Oshie",       "F", 3, 5, 8),
            new LineupPlayer("A. Galchenyuk",  "F", 2, 4, 6),
            new LineupPlayer("J. Voracek",     "F", 1, 6, 7)));
        lines.add(new ForwardLine(2,
            new LineupPlayer("K. Hayes",       "F", 2, 3, 5),
            new LineupPlayer("D. Backes",      "F", 1, 2, 3),
            new LineupPlayer("T. J. Oshie",    "F", 4, 2, 6)));
        lines.add(new ForwardLine(3,
            new LineupPlayer("B. Kessel",      "F", 1, 1, 2),
            new LineupPlayer("R. Kesler",      "F", 0, 2, 2),
            new LineupPlayer("M. Camalleri",   "F", 1, 0, 1)));
        lines.add(new ForwardLine(4,
            new LineupPlayer("P. Stastny",     "F", 0, 1, 1),
            new LineupPlayer("D. Bylsma",      "F", 0, 0, 0),
            new LineupPlayer("B. Morrow",      "F", 0, 1, 1)));

        List<DefensePair> pairs = new ArrayList<>();
        pairs.add(new DefensePair(1,
            new LineupPlayer("R. Suter",       "D", 1, 4, 5),
            new LineupPlayer("Z. Chara",       "D", 0, 3, 3)));
        pairs.add(new DefensePair(2,
            new LineupPlayer("J. Carlson",     "D", 0, 2, 2),
            new LineupPlayer("A. Ekblad",      "D", 1, 1, 2)));
        pairs.add(new DefensePair(3,
            new LineupPlayer("J. Faulk",       "D", 0, 1, 1),
            new LineupPlayer("M. Streit",      "D", 0, 0, 0)));

        LineupPlayer starter = new LineupPlayer("J. Quick",   "GK", 0, 0, 0);
        LineupPlayer backup  = new LineupPlayer("R. Miller",  "GK", 0, 0, 0);

        return new Lineup("USA vs Canada — Feb 10", lines, pairs, starter, backup);
    }
}
