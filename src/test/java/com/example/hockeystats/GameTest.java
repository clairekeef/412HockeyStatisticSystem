package com.example.hockeystats;

import com.example.hockeystats.model.Game;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Game — covers constructor, Builder, getters, setters,
 * derived helpers (isComplete, getWinner), and sample data.
 */
public class GameTest {

    // ── Builder ──────────────────────────────────────────────────────────────

    @Test
    void builder_createsGameWithRequiredFields() {
        Game g = new Game.Builder(1, "Canada", "USA").build();
        assertEquals(1,        g.getGameId());
        assertEquals("Canada", g.getTeamH());
        assertEquals("USA",    g.getTeamA());
    }

    @Test
    void builder_defaultEventStatus_isScheduled() {
        Game g = new Game.Builder(1, "Canada", "USA").build();
        assertEquals("SCHEDULED", g.getEventStatus());
    }

    @Test
    void builder_defaultScores_areZero() {
        Game g = new Game.Builder(1, "Canada", "USA").build();
        assertEquals(0, g.getScoreTH());
        assertEquals(0, g.getScoreTA());
    }

    @Test
    void builder_fluentSetters_applyCorrectly() {
        Game g = new Game.Builder(5, "Sweden", "Finland")
                .time("2026-02-10T14:00")
                .location("Ice Arena A")
                .eventStage("Group A")
                .eventStatus("Finished")
                .scoreTH(3)
                .scoreTA(1)
                .teamHCode("SWE")
                .teamACode("FIN")
                .sex("M")
                .build();

        assertEquals("2026-02-10T14:00", g.getTime());
        assertEquals("Ice Arena A",      g.getLocation());
        assertEquals("Group A",          g.getEventStage());
        assertEquals("Finished",         g.getEventStatus());
        assertEquals(3,                  g.getScoreTH());
        assertEquals(1,                  g.getScoreTA());
        assertEquals("SWE",              g.getTeamHCode());
        assertEquals("FIN",              g.getTeamACode());
        assertEquals("M",                g.getSex());
    }

    // ── Setters (mutable fields) ─────────────────────────────────────────────

    @Test
    void setEventStatus_updatesStatus() {
        Game g = new Game.Builder(1, "A", "B").build();
        g.setEventStatus("Live");
        assertEquals("Live", g.getEventStatus());
    }

    @Test
    void setScores_updatesScores() {
        Game g = new Game.Builder(1, "A", "B").build();
        g.setScoreTH(2);
        g.setScoreTA(3);
        assertEquals(2, g.getScoreTH());
        assertEquals(3, g.getScoreTA());
    }

    // ── isComplete ───────────────────────────────────────────────────────────

    @Test
    void isComplete_finishedGame_returnsTrue() {
        Game g = new Game.Builder(1, "A", "B").eventStatus("Finished").build();
        assertTrue(g.isComplete());
    }

    @Test
    void isComplete_finishedCaseInsensitive_returnsTrue() {
        Game g = new Game.Builder(1, "A", "B").eventStatus("FINISHED").build();
        assertTrue(g.isComplete());
    }

    @Test
    void isComplete_scheduledGame_returnsFalse() {
        Game g = new Game.Builder(1, "A", "B").eventStatus("Scheduled").build();
        assertFalse(g.isComplete());
    }

    @Test
    void isComplete_liveGame_returnsFalse() {
        Game g = new Game.Builder(1, "A", "B").eventStatus("Live").build();
        assertFalse(g.isComplete());
    }

    // ── getWinner ────────────────────────────────────────────────────────────

    @Test
    void getWinner_homeWins_returnsHomeTeam() {
        Game g = new Game.Builder(1, "Canada", "USA")
                .eventStatus("Finished").scoreTH(4).scoreTA(2).build();
        assertEquals("Canada", g.getWinner());
    }

    @Test
    void getWinner_awayWins_returnsAwayTeam() {
        Game g = new Game.Builder(1, "Canada", "USA")
                .eventStatus("Finished").scoreTH(1).scoreTA(3).build();
        assertEquals("USA", g.getWinner());
    }

    @Test
    void getWinner_tie_returnsTie() {
        Game g = new Game.Builder(1, "Canada", "USA")
                .eventStatus("Finished").scoreTH(2).scoreTA(2).build();
        assertEquals("TIE", g.getWinner());
    }

    @Test
    void getWinner_gameNotComplete_returnsNull() {
        Game g = new Game.Builder(1, "Canada", "USA")
                .eventStatus("Scheduled").scoreTH(0).scoreTA(0).build();
        assertNull(g.getWinner());
    }

    // ── toString ─────────────────────────────────────────────────────────────

    @Test
    void toString_containsKeyInfo() {
        Game g = new Game.Builder(7, "Canada", "Germany")
                .eventStage("Quarterfinal").eventStatus("Finished")
                .scoreTH(4).scoreTA(1).build();
        String s = g.toString();
        assertTrue(s.contains("7"));
        assertTrue(s.contains("Canada"));
        assertTrue(s.contains("Germany"));
        assertTrue(s.contains("4"));
        assertTrue(s.contains("1"));
    }

    // ── Sample data ──────────────────────────────────────────────────────────

    @Test
    void getSampleGames_returnsTenGames() {
        assertEquals(10, Game.getSampleGames().size());
    }

    @Test
    void getSampleGames_allHaveUniqueIds() {
        List<Game> games = Game.getSampleGames();
        long distinct = games.stream().mapToInt(Game::getGameId).distinct().count();
        assertEquals(games.size(), distinct);
    }

    @Test
    void getSampleGameById_validId_returnsCorrectGame() {
        Game g = Game.getSampleGameById(1);
        assertNotNull(g);
        assertEquals(1, g.getGameId());
        assertEquals("Canada", g.getTeamH());
        assertEquals("USA",    g.getTeamA());
    }

    @Test
    void getSampleGameById_invalidId_returnsNull() {
        assertNull(Game.getSampleGameById(999));
    }

    @Test
    void getSampleGames_game1_hasFinalStatus() {
        Game g = Game.getSampleGameById(1);
        assertNotNull(g);
        assertTrue(g.isComplete());
    }

    @Test
    void getSampleGames_game10_hasScheduledStatus() {
        Game g = Game.getSampleGameById(10);
        assertNotNull(g);
        assertFalse(g.isComplete());
    }
}
