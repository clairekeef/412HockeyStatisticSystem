package com.example.hockeystats;

import com.example.hockeystats.model.PlayerStats;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PlayerStats — covers both constructors, all getters,
 * pts auto-calculation, and toString format.
 */
public class PlayerStatsTest {

    // ── Full constructor ─────────────────────────────────────────────────────

    @Test
    void fullConstructor_allFieldsSet() {
        PlayerStats p = new PlayerStats("Alex Ovechkin", "A", "Russia", "Forward",
                5, 10, 7, 17, 4, 3);
        assertEquals("Alex Ovechkin", p.getName());
        assertEquals("A",             p.getGroup());
        assertEquals("Russia",        p.getCountry());
        assertEquals("Forward",       p.getPosition());
        assertEquals(5,  p.getGp());
        assertEquals(10, p.getGoals());
        assertEquals(7,  p.getAssists());
        assertEquals(17, p.getPts());
        assertEquals(4,  p.getPim());
        assertEquals(3,  p.getPpg());
    }

    @Test
    void fullConstructor_getPlayerId_aliasesName() {
        PlayerStats p = new PlayerStats("Sidney Crosby", "A", "Canada", "Forward",
                3, 5, 8, 13, 2, 1);
        assertEquals(p.getName(), p.getPlayerId());
    }

    // ── Legacy constructor ───────────────────────────────────────────────────

    @Test
    void legacyConstructor_setsNameGoalsAssists() {
        PlayerStats p = new PlayerStats("P. Kane", 3.0, 5.0);
        assertEquals("P. Kane", p.getName());
        assertEquals(3,         p.getGoals());
        assertEquals(5,         p.getAssists());
    }

    @Test
    void legacyConstructor_ptsIsGoalsPlusAssists() {
        PlayerStats p = new PlayerStats("P. Kane", 3.0, 5.0);
        assertEquals(8, p.getPts());
    }

    @Test
    void legacyConstructor_defaultsGpPimPpgToZero() {
        PlayerStats p = new PlayerStats("P. Kane", 3.0, 5.0);
        assertEquals(0, p.getGp());
        assertEquals(0, p.getPim());
        assertEquals(0, p.getPpg());
    }

    @Test
    void legacyConstructor_doubleGoalsAreTruncated() {
        PlayerStats p = new PlayerStats("Test", 2.9, 1.1);
        assertEquals(2, p.getGoals());
        assertEquals(1, p.getAssists());
    }

    // ── Edge cases ───────────────────────────────────────────────────────────

    @Test
    void zeroStats_allGettersReturnZero() {
        PlayerStats p = new PlayerStats("Bench Warmer", "", "Canada", "D",
                0, 0, 0, 0, 0, 0);
        assertEquals(0, p.getGoals());
        assertEquals(0, p.getAssists());
        assertEquals(0, p.getPts());
        assertEquals(0, p.getGp());
        assertEquals(0, p.getPim());
        assertEquals(0, p.getPpg());
    }

    @Test
    void emptyCountryAndPosition_gettersReturnEmptyString() {
        PlayerStats p = new PlayerStats("Ghost", "", "", "",
                0, 0, 0, 0, 0, 0);
        assertEquals("", p.getCountry());
        assertEquals("", p.getPosition());
    }

    // ── toString ─────────────────────────────────────────────────────────────

    @Test
    void toString_containsNameAndStats() {
        PlayerStats p = new PlayerStats("Connor McDavid", "A", "Canada", "Forward",
                5, 7, 9, 16, 2, 2);
        String s = p.toString();
        assertTrue(s.contains("Connor McDavid"));
        assertTrue(s.contains("Canada"));
        assertTrue(s.contains("7"));
        assertTrue(s.contains("9"));
        assertTrue(s.contains("16"));
    }
}
