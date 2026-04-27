package com.example.hockeystats;

import com.example.hockeystats.model.Lineup;
import com.example.hockeystats.model.Lineup.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Lineup and its inner classes LineupPlayer, ForwardLine, DefensePair.
 * Uses only the public API and the built-in sample data — no network required.
 */
public class LineupTest {

    private Lineup lineup;

    @BeforeEach
    void setUp() {
        lineup = Lineup.getSampleLineup();
    }

    // ── LineupPlayer ─────────────────────────────────────────────────────────

    @Test
    void lineupPlayer_getters_returnCorrectValues() {
        LineupPlayer p = new LineupPlayer("J. Quick", "GK", 0, 0, 0);
        assertEquals("J. Quick", p.getName());
        assertEquals("GK",       p.getPosition());
        assertEquals(0,          p.getGoals());
        assertEquals(0,          p.getAssists());
        assertEquals(0,          p.getPoints());
    }

    @Test
    void lineupPlayer_toMap_containsExpectedKeys() {
        LineupPlayer p = new LineupPlayer("Scorer", "F", 5, 3, 8);
        Map<String, Object> m = p.toMap();
        assertTrue(m.containsKey("name"));
        assertTrue(m.containsKey("position"));
        assertTrue(m.containsKey("G"));
        assertTrue(m.containsKey("A"));
        assertTrue(m.containsKey("P"));
    }

    @Test
    void lineupPlayer_toMap_valuesMatchGetters() {
        LineupPlayer p = new LineupPlayer("A. Oshie", "F", 3, 5, 8);
        Map<String, Object> m = p.toMap();
        assertEquals("A. Oshie", m.get("name"));
        assertEquals(3, m.get("G"));
        assertEquals(5, m.get("A"));
        assertEquals(8, m.get("P"));
    }

    @Test
    void lineupPlayer_toString_containsNameAndStats() {
        LineupPlayer p = new LineupPlayer("A. Oshie", "F", 3, 5, 8);
        String s = p.toString();
        assertTrue(s.contains("A. Oshie"));
        assertTrue(s.contains("3"));
        assertTrue(s.contains("5"));
        assertTrue(s.contains("8"));
    }

    // ── ForwardLine ──────────────────────────────────────────────────────────

    @Test
    void forwardLine_getLineNumber_correct() {
        LineupPlayer lw = new LineupPlayer("LW", "F", 0, 0, 0);
        LineupPlayer c  = new LineupPlayer("C",  "F", 0, 0, 0);
        LineupPlayer rw = new LineupPlayer("RW", "F", 0, 0, 0);
        ForwardLine line = new ForwardLine(2, lw, c, rw);
        assertEquals(2, line.getLineNumber());
    }

    @Test
    void forwardLine_toMap_containsLineKey() {
        ForwardLine line = new ForwardLine(1,
                new LineupPlayer("LW", "F", 0, 0, 0),
                new LineupPlayer("C",  "F", 0, 0, 0),
                new LineupPlayer("RW", "F", 0, 0, 0));
        assertTrue(line.toMap().containsKey("line"));
        assertTrue(line.toMap().containsKey("leftWing"));
        assertTrue(line.toMap().containsKey("center"));
        assertTrue(line.toMap().containsKey("rightWing"));
    }

    @Test
    void forwardLine_toMap_nullPlayer_storesNull() {
        ForwardLine line = new ForwardLine(1, null, null, null);
        assertNull(line.toMap().get("leftWing"));
    }

    // ── DefensePair ──────────────────────────────────────────────────────────

    @Test
    void defensePair_getters_returnCorrectValues() {
        LineupPlayer ld = new LineupPlayer("LD", "D", 0, 2, 2);
        LineupPlayer rd = new LineupPlayer("RD", "D", 0, 1, 1);
        DefensePair pair = new DefensePair(1, ld, rd);
        assertEquals(1,    pair.getPairNumber());
        assertEquals("LD", pair.getLeftDefense().getName());
        assertEquals("RD", pair.getRightDefense().getName());
    }

    @Test
    void defensePair_toMap_containsExpectedKeys() {
        DefensePair pair = new DefensePair(1,
                new LineupPlayer("LD", "D", 0, 0, 0),
                new LineupPlayer("RD", "D", 0, 0, 0));
        Map<String, Object> m = pair.toMap();
        assertTrue(m.containsKey("pair"));
        assertTrue(m.containsKey("leftDefense"));
        assertTrue(m.containsKey("rightDefense"));
    }

    // ── Lineup — sample data ─────────────────────────────────────────────────

    @Test
    void sampleLineup_hasGameLabel() {
        assertNotNull(lineup.getGameLabel());
        assertFalse(lineup.getGameLabel().isBlank());
    }

    @Test
    void sampleLineup_hasFourForwardLines() {
        assertEquals(4, lineup.getForwardLines().size());
    }

    @Test
    void sampleLineup_hasThreeDefensePairs() {
        assertEquals(3, lineup.getDefensePairs().size());
    }

    @Test
    void sampleLineup_hasStarterAndBackupGoalie() {
        assertNotNull(lineup.getStarterGoalie());
        assertNotNull(lineup.getBackupGoalie());
    }

    @Test
    void sampleLineup_goaliesAreGK() {
        assertEquals("GK", lineup.getStarterGoalie().getPosition());
        assertEquals("GK", lineup.getBackupGoalie().getPosition());
    }

    // ── getAllPlayers ─────────────────────────────────────────────────────────

    @Test
    void getAllPlayers_returnsNonEmptyList() {
        assertFalse(lineup.getAllPlayers().isEmpty());
    }

    @Test
    void getAllPlayers_includesGoalies() {
        List<LineupPlayer> all = lineup.getAllPlayers();
        long goalies = all.stream().filter(p -> p.getPosition().equals("GK")).count();
        assertEquals(2, goalies);
    }

    @Test
    void getAllPlayers_includes12Forwards() {
        // 4 lines × 3 players = 12
        List<LineupPlayer> all = lineup.getAllPlayers();
        long forwards = all.stream().filter(p -> p.getPosition().equals("F")).count();
        assertEquals(12, forwards);
    }

    @Test
    void getAllPlayers_includes6Defensemen() {
        // 3 pairs × 2 players = 6
        List<LineupPlayer> all = lineup.getAllPlayers();
        long dmen = all.stream().filter(p -> p.getPosition().equals("D")).count();
        assertEquals(6, dmen);
    }

    // ── getTopScorer ─────────────────────────────────────────────────────────

    @Test
    void getTopScorer_isNotNull() {
        assertNotNull(lineup.getTopScorer());
    }

    @Test
    void getTopScorer_isForward() {
        assertEquals("F", lineup.getTopScorer().getPosition());
    }

    @Test
    void getTopScorer_hasHighestPointsAmongForwards() {
        LineupPlayer top = lineup.getTopScorer();
        int maxPts = lineup.getForwardLines().stream()
                .flatMap(l -> Arrays.stream(new LineupPlayer[]{
                        l.getLeftWing(), l.getCenter(), l.getRightWing()
                }))
                .filter(Objects::nonNull)
                .mapToInt(LineupPlayer::getPoints)
                .max().orElse(0);
        assertEquals(maxPts, top.getPoints());
    }

    // ── getTotalPoints ───────────────────────────────────────────────────────

    @Test
    void getTotalPoints_isPositive() {
        assertTrue(lineup.getTotalPoints() > 0);
    }

    @Test
    void getTotalPoints_excludesGoalies() {
        int withGoalies  = lineup.getAllPlayers().stream().mapToInt(LineupPlayer::getPoints).sum();
        int goaliePoints = (lineup.getStarterGoalie() != null ? lineup.getStarterGoalie().getPoints() : 0)
                         + (lineup.getBackupGoalie()  != null ? lineup.getBackupGoalie().getPoints()  : 0);
        assertEquals(withGoalies - goaliePoints, lineup.getTotalPoints());
    }

    // ── getLineupSummary ─────────────────────────────────────────────────────

    @Test
    void getLineupSummary_containsGameLabel() {
        assertTrue(lineup.getLineupSummary().contains(lineup.getGameLabel()));
    }

    @Test
    void getLineupSummary_containsPlayerCount() {
        int count = lineup.getAllPlayers().size();
        assertTrue(lineup.getLineupSummary().contains(String.valueOf(count)));
    }

    // ── toMap ────────────────────────────────────────────────────────────────

    @Test
    void toMap_containsAllExpectedKeys() {
        Map<String, Object> m = lineup.toMap();
        assertTrue(m.containsKey("gameLabel"));
        assertTrue(m.containsKey("forwardLines"));
        assertTrue(m.containsKey("defensePairs"));
        assertTrue(m.containsKey("starterGoalie"));
        assertTrue(m.containsKey("backupGoalie"));
        assertTrue(m.containsKey("totalPlayers"));
        assertTrue(m.containsKey("totalPoints"));
        assertTrue(m.containsKey("topScorer"));
    }

    @Test
    void toMap_totalPlayersMatchesGetAllPlayersSize() {
        assertEquals(lineup.getAllPlayers().size(), lineup.toMap().get("totalPlayers"));
    }
}
