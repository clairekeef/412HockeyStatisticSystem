package com.example.hockeystats;

import com.example.hockeystats.model.Team;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Team — covers constructor, copy constructor, copy(),
 * getters, and updateRoster().
 */
public class TeamTest {

    // ── Constructor ──────────────────────────────────────────────────────────

    @Test
    void constructor_setsCountryAndRoster() {
        List<String> roster = Arrays.asList("Player A", "Player B");
        Team team = new Team("Canada", roster);
        assertEquals("Canada", team.getTeamCountry());
        assertEquals(2,        team.getRoster().size());
    }

    @Test
    void constructor_emptyRoster_returnsEmptyList() {
        Team team = new Team("USA", new ArrayList<>());
        assertTrue(team.getRoster().isEmpty());
    }

    // ── Copy constructor ─────────────────────────────────────────────────────

    @Test
    void copyConstructor_producesIndependentRoster() {
        List<String> original = new ArrayList<>(Arrays.asList("P1", "P2"));
        Team team1 = new Team("Canada", original);
        Team team2 = new Team(team1);

        team2.updateRoster(new ArrayList<>(Arrays.asList("P1", "P2", "P3")));

        assertEquals(2, team1.getRoster().size()); // original unchanged
        assertEquals(3, team2.getRoster().size());
    }

    @Test
    void copyConstructor_preservesCountry() {
        Team original = new Team("Sweden", Arrays.asList("Karlsson"));
        Team copy = new Team(original);
        assertEquals("Sweden", copy.getTeamCountry());
    }

    // ── copy() ───────────────────────────────────────────────────────────────

    @Test
    void copy_returnsNewTeamObject() {
        Team team = new Team("Finland", Arrays.asList("Laine"));
        Team copy = team.copy();
        assertNotSame(team, copy);
    }

    @Test
    void copy_hasSameCountryAndRoster() {
        Team team = new Team("Finland", Arrays.asList("Laine", "Aho"));
        Team copy = team.copy();
        assertEquals(team.getTeamCountry(), copy.getTeamCountry());
        assertEquals(team.getRoster(),      copy.getRoster());
    }

    @Test
    void copy_rosterIsIndependent() {
        List<String> roster = new ArrayList<>(Arrays.asList("A", "B"));
        Team team = new Team("Russia", roster);
        Team copy = team.copy();

        copy.updateRoster(new ArrayList<>(Arrays.asList("X")));
        assertEquals(2, team.getRoster().size()); // original unchanged
    }

    // ── updateRoster ─────────────────────────────────────────────────────────

    @Test
    void updateRoster_replacesExistingRoster() {
        Team team = new Team("Canada", new ArrayList<>(Arrays.asList("P1")));
        team.updateRoster(Arrays.asList("P2", "P3", "P4"));
        assertEquals(3, team.getRoster().size());
        assertTrue(team.getRoster().contains("P3"));
    }

    @Test
    void updateRoster_toEmpty_clearsRoster() {
        Team team = new Team("Canada", new ArrayList<>(Arrays.asList("P1", "P2")));
        team.updateRoster(new ArrayList<>());
        assertTrue(team.getRoster().isEmpty());
    }

    // ── Roster contents ──────────────────────────────────────────────────────

    @Test
    void getRoster_containsExpectedPlayers() {
        List<String> players = Arrays.asList("McDavid", "Crosby", "MacKinnon");
        Team team = new Team("Canada", players);
        assertTrue(team.getRoster().contains("McDavid"));
        assertTrue(team.getRoster().contains("Crosby"));
    }
}
