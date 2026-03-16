package com.example.hockeystats;

import com.example.hockeystats.model.DataRepository;
import com.example.hockeystats.model.PlayerStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DataRepositoryTest {

    private DataRepository repo;

    @BeforeEach
    void setUp() {
        repo = new DataRepository();
    }

    @Test
    void getAllPlayers_returns86Players() {
        assertEquals(87, repo.getAllPlayers().size());
    }

    @Test
    void getPlayerByName_exactMatch() {
        PlayerStats p = repo.getPlayerByName("Adam Tambellini");
        assertNotNull(p);
        assertEquals("Adam Tambellini", p.getName());
    }

    @Test
    void getPlayerByName_caseInsensitive() {
        assertNotNull(repo.getPlayerByName("adam tambellini"));
    }

    @Test
    void getPlayerByName_unknownReturnsNull() {
        assertNull(repo.getPlayerByName("Fake Player"));
    }

    @Test
    void getPlayerByName_nullReturnsNull() {
        assertNull(repo.getPlayerByName(null));
    }

    @Test
    void getPlayersByCountry_canadaNotEmpty() {
        assertFalse(repo.getPlayersByCountry("Canada").isEmpty());
    }

    @Test
    void getPlayersByCountry_caseInsensitive() {
        assertEquals(
            repo.getPlayersByCountry("Canada").size(),
            repo.getPlayersByCountry("canada").size()
        );
    }

    @Test
    void getPlayersByCountry_unknownReturnsEmpty() {
        assertTrue(repo.getPlayersByCountry("France").isEmpty());
    }

    @Test
    void getPlayersByCountry_nullReturnsEmpty() {
        assertTrue(repo.getPlayersByCountry(null).isEmpty());
    }

    @Test
    void getPlayersByPosition_forwardNotEmpty() {
        assertFalse(repo.getPlayersByPosition("Forward").isEmpty());
    }

    @Test
    void getPlayersByPosition_defenceNotEmpty() {
        assertFalse(repo.getPlayersByPosition("Defence").isEmpty());
    }

    @Test
    void getPlayersByPosition_nullReturnsEmpty() {
        assertTrue(repo.getPlayersByPosition(null).isEmpty());
    }

    @Test
    void getTopScorers_returnsCorrectCount() {
        assertEquals(5, repo.getTopScorers(5).size());
    }

    @Test
    void getTopScorers_sortedByPoints() {
        List<PlayerStats> top = repo.getTopScorers(5);
        assertTrue(top.get(0).getPts() >= top.get(1).getPts());
    }

    @Test
    void getTopScorers_zeroReturnsEmpty() {
        assertTrue(repo.getTopScorers(0).isEmpty());
    }

    @Test
    void getTopScorersByCountry_correctCount() {
        assertEquals(3, repo.getTopScorersByCountry("Canada", 3).size());
    }

    @Test
    void getTopScorersByCountry_allFromCorrectCountry() {
        repo.getTopScorersByCountry("Canada", 5)
            .forEach(p -> assertEquals("Canada", p.getCountry()));
    }

    @Test
    void getGoalsByCountry_containsExpectedCountries() {
        Map<String, Integer> goals = repo.getGoalsByCountry();
        assertTrue(goals.containsKey("Canada"));
        assertTrue(goals.containsKey("USA"));
    }

    @Test
    void getGoalsByCountry_noNegativeValues() {
        repo.getGoalsByCountry().values().forEach(v -> assertTrue(v >= 0));
    }

    @Test
    void getPointsByCountry_canadaMoreThanChina() {
        Map<String, Integer> pts = repo.getPointsByCountry();
        assertTrue(pts.get("Canada") > pts.get("China"));
    }

    @Test
    void getAvailableCountries_returnsFour() {
        assertEquals(4, repo.getAvailableCountries().size());
    }

    @Test
    void getAvailableCountries_isSorted() {
        List<String> countries = repo.getAvailableCountries();
        for (int i = 0; i < countries.size() - 1; i++) {
            assertTrue(countries.get(i).compareTo(countries.get(i + 1)) <= 0);
        }
    }

    @Test
    void getMetricForPlayer_goals() {
        assertEquals(3.0, repo.getMetricForPlayer("Adam Tambellini", "G"));
    }

    @Test
    void getMetricForPlayer_points() {
        assertEquals(7.0, repo.getMetricForPlayer("Adam Tambellini", "PTS"));
    }

    @Test
    void getMetricForPlayer_unknownPlayerReturnsZero() {
        assertEquals(0.0, repo.getMetricForPlayer("Nobody", "G"));
    }

    @Test
    void getMetricForPlayer_unknownMetricReturnsZero() {
        assertEquals(0.0, repo.getMetricForPlayer("Adam Tambellini", "XYZ"));
    }

    @Test
    void getMetricForCountry_canadaGoalsPositive() {
        assertTrue(repo.getMetricForCountry("Canada", "G") > 0);
    }

    @Test
    void getMetricForCountry_unknownCountryReturnsZero() {
        assertEquals(0.0, repo.getMetricForCountry("France", "G"));
    }
}