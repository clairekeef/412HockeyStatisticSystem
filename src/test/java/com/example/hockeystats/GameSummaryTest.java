package com.example.hockeystats;

import com.example.hockeystats.model.Game;
import com.example.hockeystats.model.GameSummary;
import com.example.hockeystats.model.GameSummary.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GameSummary and its inner classes TeamGameStats and GoalEvent.
 * Uses the built-in sample data from Game and GameSummary — no network required.
 */
public class GameSummaryTest {

    private GameSummary summary1; // Canada 4 - USA 2

    @BeforeEach
    void setUp() {
        summary1 = GameSummary.getSampleSummary(1);
    }

    // ── TeamGameStats ────────────────────────────────────────────────────────

    @Test
    void teamGameStats_getters_returnCorrectValues() {
        TeamGameStats stats = new TeamGameStats("Canada", 34, 28, 4, 2, 31, 58, 8, 14, 9);
        assertEquals("Canada", stats.getTeam());
        assertEquals(34, stats.getShotsOnGoal());
        assertEquals(28, stats.getHits());
        assertEquals(4,  stats.getPowerPlayOpportunities());
        assertEquals(2,  stats.getPowerPlayGoals());
        assertEquals(31, stats.getFaceoffWins());
        assertEquals(58, stats.getFaceoffTotal());
        assertEquals(8,  stats.getPenaltyMinutes());
        assertEquals(14, stats.getBlockedShots());
        assertEquals(9,  stats.getTurnovers());
    }

    @Test
    void teamGameStats_ppPct_calculatesCorrectly() {
        // 2/4 = 50%
        TeamGameStats stats = new TeamGameStats("Canada", 34, 28, 4, 2, 31, 58, 8, 14, 9);
        assertEquals(50.0, stats.getPowerPlayPct(), 0.01);
    }

    @Test
    void teamGameStats_ppPct_zeroWhenNoOpportunities() {
        TeamGameStats stats = new TeamGameStats("Canada", 34, 28, 0, 0, 31, 58, 8, 14, 9);
        assertEquals(0.0, stats.getPowerPlayPct());
    }

    @Test
    void teamGameStats_faceoffPct_calculatesCorrectly() {
        // 31/58 ≈ 53.4%
        TeamGameStats stats = new TeamGameStats("Canada", 34, 28, 4, 2, 31, 58, 8, 14, 9);
        assertEquals(53.4, stats.getFaceoffPct(), 0.5);
    }

    @Test
    void teamGameStats_faceoffPct_zeroWhenNoFaceoffs() {
        TeamGameStats stats = new TeamGameStats("Canada", 34, 28, 4, 2, 0, 0, 8, 14, 9);
        assertEquals(0.0, stats.getFaceoffPct());
    }

    // ── GoalEvent ────────────────────────────────────────────────────────────

    @Test
    void goalEvent_getters_returnCorrectValues() {
        GoalEvent g = new GoalEvent("Canada", "Crosby", "McDavid", 1, "07:14", false);
        assertEquals("Canada",  g.getTeam());
        assertEquals("Crosby",  g.getScorer());
        assertEquals("McDavid", g.getAssist());
        assertEquals(1,         g.getPeriod());
        assertEquals("07:14",   g.getTime());
        assertFalse(g.isPowerPlay());
    }

    @Test
    void goalEvent_isPowerPlay_trueWhenPP() {
        GoalEvent g = new GoalEvent("USA", "Kane", "Matthews", 2, "04:55", true);
        assertTrue(g.isPowerPlay());
    }

    @Test
    void goalEvent_toString_containsScorer() {
        GoalEvent g = new GoalEvent("Canada", "Crosby", "McDavid", 1, "07:14", false);
        assertTrue(g.toString().contains("Crosby"));
    }

    @Test
    void goalEvent_toString_containsPPTagWhenPP() {
        GoalEvent g = new GoalEvent("Canada", "MacKinnon", "Crosby", 1, "18:42", true);
        assertTrue(g.toString().contains("PP"));
    }

    @Test
    void goalEvent_toString_containsPeriodAndTime() {
        GoalEvent g = new GoalEvent("Canada", "Crosby", "McDavid", 2, "11:30", false);
        String s = g.toString();
        assertTrue(s.contains("11:30"));
    }

    // ── GameSummary — basic getters ──────────────────────────────────────────

    @Test
    void getSampleSummary_game1_isNotNull() {
        assertNotNull(summary1);
    }

    @Test
    void getSampleSummary_game1_hasCorrectTeams() {
        assertEquals("Canada", summary1.getGame().getTeamH());
        assertEquals("USA",    summary1.getGame().getTeamA());
    }

    @Test
    void getSampleSummary_game1_hasSixGoals() {
        assertEquals(6, summary1.getGoals().size());
    }

    @Test
    void getSampleSummary_invalidId_returnsNull() {
        assertNull(GameSummary.getSampleSummary(999));
    }

    // ── getShotSummary ───────────────────────────────────────────────────────

    @Test
    void getShotSummary_containsBothTeams() {
        String s = summary1.getShotSummary();
        assertTrue(s.contains("Canada"));
        assertTrue(s.contains("USA"));
    }

    @Test
    void getShotSummary_containsSOG() {
        assertTrue(summary1.getShotSummary().contains("SOG"));
    }

    // ── getPowerPlaySummary ──────────────────────────────────────────────────

    @Test
    void getPowerPlaySummary_containsBothTeams() {
        String s = summary1.getPowerPlaySummary();
        assertTrue(s.contains("Canada"));
        assertTrue(s.contains("USA"));
    }

    @Test
    void getPowerPlaySummary_containsPercentageSign() {
        assertTrue(summary1.getPowerPlaySummary().contains("%"));
    }

    // ── getGoalsByPeriod ─────────────────────────────────────────────────────

    @Test
    void getGoalsByPeriod_hasKeys1Through4() {
        Map<Integer, List<GoalEvent>> byPeriod = summary1.getGoalsByPeriod();
        assertTrue(byPeriod.containsKey(1));
        assertTrue(byPeriod.containsKey(2));
        assertTrue(byPeriod.containsKey(3));
        assertTrue(byPeriod.containsKey(4));
    }

    @Test
    void getGoalsByPeriod_totalGoalsMatchesGoalList() {
        Map<Integer, List<GoalEvent>> byPeriod = summary1.getGoalsByPeriod();
        int total = byPeriod.values().stream().mapToInt(List::size).sum();
        assertEquals(summary1.getGoals().size(), total);
    }

    @Test
    void getGoalsByPeriod_game1_twoGoalsInPeriod1() {
        assertEquals(2, summary1.getGoalsByPeriod().get(1).size());
    }

    // ── getGameMVP ───────────────────────────────────────────────────────────

    @Test
    void getGameMVP_returnsNonNullNonEmpty() {
        String mvp = summary1.getGameMVP();
        assertNotNull(mvp);
        assertFalse(mvp.isBlank());
    }

    @Test
    void getGameMVP_notNA_whenGoalsExist() {
        assertNotEquals("N/A", summary1.getGameMVP());
    }

    @Test
    void getGameMVP_isNA_whenNoGoals() {
        Game game = new Game.Builder(99, "A", "B").eventStatus("Finished").build();
        TeamGameStats h = new TeamGameStats("A", 20, 15, 2, 0, 30, 60, 4, 10, 5);
        TeamGameStats a = new TeamGameStats("B", 18, 12, 1, 0, 30, 60, 4, 8,  5);
        GameSummary empty = new GameSummary(game, h, a, new ArrayList<>());
        assertEquals("N/A", empty.getGameMVP());
    }

    // ── getPeriodScoreSummary ────────────────────────────────────────────────

    @Test
    void getPeriodScoreSummary_containsBothTeamNames() {
        String s = summary1.getPeriodScoreSummary();
        assertTrue(s.contains("Canada"));
        assertTrue(s.contains("USA"));
    }

    // ── getPerformanceInsights ───────────────────────────────────────────────

    @Test
    void getPerformanceInsights_notEmpty() {
        assertFalse(summary1.getPerformanceInsights().isEmpty());
    }

    @Test
    void getPerformanceInsights_containsShotInfo() {
        boolean hasShot = summary1.getPerformanceInsights().stream()
                .anyMatch(s -> s.toLowerCase().contains("shot"));
        assertTrue(hasShot);
    }

    // ── toMap ────────────────────────────────────────────────────────────────

    @Test
    void toMap_containsAllExpectedKeys() {
        Map<String, Object> m = summary1.toMap();
        assertTrue(m.containsKey("gameId"));
        assertTrue(m.containsKey("homeTeam"));
        assertTrue(m.containsKey("awayTeam"));
        assertTrue(m.containsKey("homeScore"));
        assertTrue(m.containsKey("awayScore"));
        assertTrue(m.containsKey("mvp"));
        assertTrue(m.containsKey("insights"));
        assertTrue(m.containsKey("shotSummary"));
        assertTrue(m.containsKey("ppSummary"));
        assertTrue(m.containsKey("periodScore"));
    }

    @Test
    void toMap_gameId_matchesGame() {
        assertEquals(1, summary1.toMap().get("gameId"));
    }

    // ── Other sample summaries ───────────────────────────────────────────────

    @Test
    void getSampleSummary_game2_isNotNull() {
        assertNotNull(GameSummary.getSampleSummary(2));
    }

    @Test
    void getSampleSummary_game7_isNotNull() {
        assertNotNull(GameSummary.getSampleSummary(7));
    }

    @Test
    void getSampleSummary_genericGame_isNotNull() {
        // Game 3 triggers buildGenericSummary
        assertNotNull(GameSummary.getSampleSummary(3));
    }
}
