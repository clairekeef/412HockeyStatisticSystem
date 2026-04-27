package com.example.hockeystats;

import com.example.hockeystats.model.GameCompare;
import com.example.hockeystats.model.GameCompare.GameStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GameCompare and its inner GameStats class.
 * Uses only local (non-network) sample data.
 */
public class GameCompareTest {

    private GameCompare compare;
    private GameStats   win;
    private GameStats   loss;

    @BeforeEach
    void setUp() {
        // Win: USA 3-1 vs Germany
        win = new GameStats("G1", "Germany",
                30, 18,   20, 14,
                1, 3,     0, 2,
                33, 60,   6, 10,
                12, 8,
                new int[]{1, 1, 1, 0},
                new int[]{1, 0, 0, 0});

        // Loss: USA 1-3 vs Canada
        loss = new GameStats("G2", "Canada",
                22, 35,   15, 28,
                0, 4,     2, 3,
                26, 60,   12, 6,
                10, 14,
                new int[]{0, 1, 0, 0},
                new int[]{1, 1, 1, 0});

        List<GameStats> games = new ArrayList<>();
        games.add(win);
        games.add(loss);
        compare = new GameCompare(games);
    }

    // ── GameStats — scores & outcome ─────────────────────────────────────────

    @Test
    void gameStats_usaScore_isGoalSum() {
        assertEquals(3, win.getUsaScore());
    }

    @Test
    void gameStats_oppScore_isGoalSum() {
        assertEquals(1, win.getOppScore());
    }

    @Test
    void gameStats_isWin_true_whenUsaLeads() {
        assertTrue(win.isWin());
    }

    @Test
    void gameStats_isWin_false_whenUsaTrails() {
        assertFalse(loss.isWin());
    }

    // ── GameStats — PP % ─────────────────────────────────────────────────────

    @Test
    void gameStats_ppPct_zeroWhenNoOpps() {
        GameStats g = new GameStats("G3", "Opp",
                20, 20, 10, 10,
                0, 0,   0, 0,
                30, 60, 4, 4, 8, 5,
                new int[]{1, 0, 0, 0},
                new int[]{1, 0, 0, 0});
        assertEquals(0.0, g.getUsaPpPct());
    }

    @Test
    void gameStats_ppPct_calculatesCorrectly() {
        // win: 1 goal on 3 opps → 33.3%
        assertEquals(33.3, win.getUsaPpPct(), 0.1);
    }

    // ── GameStats — Faceoff % ────────────────────────────────────────────────

    @Test
    void gameStats_faceoffPct_zeroWhenNoFaceoffs() {
        GameStats g = new GameStats("G3", "Opp",
                20, 20, 10, 10,
                0, 2, 0, 2,
                0, 0, 4, 4, 8, 5,
                new int[]{1, 0, 0, 0},
                new int[]{1, 0, 0, 0});
        assertEquals(0.0, g.getUsaFaceoffPct());
    }

    @Test
    void gameStats_faceoffPct_calculatesCorrectly() {
        // win: 33/60 = 55.0%
        assertEquals(55.0, win.getUsaFaceoffPct(), 0.1);
    }

    // ── GameStats — toMap ────────────────────────────────────────────────────

    @Test
    void gameStats_toMap_containsExpectedKeys() {
        Map<String, Object> m = win.toMap();
        assertTrue(m.containsKey("gameId"));
        assertTrue(m.containsKey("opponent"));
        assertTrue(m.containsKey("usaScore"));
        assertTrue(m.containsKey("oppScore"));
        assertTrue(m.containsKey("isWin"));
        assertTrue(m.containsKey("usaGoalsByPeriod"));
        assertTrue(m.containsKey("oppGoalsByPeriod"));
    }

    @Test
    void gameStats_toMap_isWinMatchesMethod() {
        Map<String, Object> m = win.toMap();
        assertEquals(win.isWin(), m.get("isWin"));
    }

    // ── GameCompare — trends ─────────────────────────────────────────────────

    @Test
    void getShotTrend_hasOneEntryPerGame() {
        assertEquals(2, compare.getShotTrend().size());
    }

    @Test
    void getShotTrend_firstEntryMatchesFirstGame() {
        assertEquals(win.getUsaShots(), compare.getShotTrend().get(0));
    }

    @Test
    void getPpTrend_sizeMatchesGameCount() {
        assertEquals(2, compare.getPpTrend().size());
    }

    @Test
    void getScoreTrend_valuesAreCorrect() {
        List<Integer> trend = compare.getScoreTrend();
        assertEquals(3, trend.get(0)); // win: 3
        assertEquals(1, trend.get(1)); // loss: 1
    }

    @Test
    void getPimTrend_firstEntryIsWinPim() {
        assertEquals(win.getUsaPenaltyMinutes(), compare.getPimTrend().get(0));
    }

    // ── GameCompare — aggregate stats ────────────────────────────────────────

    @Test
    void getAvgShots_averagesCorrectly() {
        // win=30, loss=22 → avg=26.0
        assertEquals(26.0, compare.getAvgShots(), 0.01);
    }

    @Test
    void getAvgPpPct_nonNegative() {
        assertTrue(compare.getAvgPpPct() >= 0.0);
    }

    @Test
    void getBestGame_returnsHighestScoringGame() {
        GameStats best = compare.getBestGame();
        assertNotNull(best);
        assertEquals("Germany", best.getOpponent()); // win had 3 goals
    }

    // ── GameCompare — period totals ──────────────────────────────────────────

    @Test
    void getTotalUsaGoalsByPeriod_summedAcrossGames() {
        int[] totals = compare.getTotalUsaGoalsByPeriod();
        // win P1=1, loss P1=0 → total P1=1
        assertEquals(1, totals[0]);
        // win P2=1, loss P2=1 → total P2=2
        assertEquals(2, totals[1]);
    }

    @Test
    void getTotalUsaGoalsByPeriod_lengthIsFour() {
        assertEquals(4, compare.getTotalUsaGoalsByPeriod().length);
    }

    @Test
    void getTotalOppGoalsByPeriod_lengthIsFour() {
        assertEquals(4, compare.getTotalOppGoalsByPeriod().length);
    }

    // ── GameCompare — toMap ──────────────────────────────────────────────────

    @Test
    void toMap_containsAllTopLevelKeys() {
        Map<String, Object> m = compare.toMap();
        assertTrue(m.containsKey("games"));
        assertTrue(m.containsKey("shotTrend"));
        assertTrue(m.containsKey("ppTrend"));
        assertTrue(m.containsKey("scoreTrend"));
        assertTrue(m.containsKey("insights"));
        assertTrue(m.containsKey("avgShots"));
        assertTrue(m.containsKey("totalUsaGoalsByPeriod"));
    }

    // ── GameCompare — insights ───────────────────────────────────────────────

    @Test
    void getInsights_notEmpty_whenGamesExist() {
        assertFalse(compare.getInsights().isEmpty());
    }

    @Test
    void getInsights_emptyList_returnsEmptyInsights() {
        GameCompare empty = new GameCompare(new ArrayList<>());
        assertTrue(empty.getInsights().isEmpty());
    }

    @Test
    void getInsights_containsPeriodAndPpInfo() {
        List<String> insights = compare.getInsights();
        boolean hasPeriod = insights.stream().anyMatch(s -> s.contains("period") || s.contains("Period"));
        assertTrue(hasPeriod);
    }

    // ── Sample data ──────────────────────────────────────────────────────────

    @Test
    void getSampleData_returnsNonEmpty() {
        GameCompare sample = GameCompare.getSampleData();
        assertFalse(sample.getGames().isEmpty());
    }

    @Test
    void getSampleData_hasFourGames() {
        assertEquals(4, GameCompare.getSampleData().getGames().size());
    }

    @Test
    void getSampleData_firstOpponentIsCanada() {
        assertEquals("Canada", GameCompare.getSampleData().getGames().get(0).getOpponent());
    }

    // ── Empty GameCompare ────────────────────────────────────────────────────

    @Test
    void emptyCompare_avgShotsIsZero() {
        assertEquals(0.0, new GameCompare(new ArrayList<>()).getAvgShots());
    }

    @Test
    void emptyCompare_bestGameIsNull() {
        assertNull(new GameCompare(new ArrayList<>()).getBestGame());
    }
}
