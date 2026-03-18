package com.example.hockeystats.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * GameRepository loads and stores game data from a Game CSV file.
 *
 * CSV format:
 *   gameID,teamCountry,opponentTeam,location,date
 */
public class GameDataRepository {

    // Keyed by gameID
    private final Map<String, Game> gamesByID = new HashMap<>();

    // Keyed by teamCountry (case-insensitive)
    private final Map<String, List<Game>> gamesByTeam = new HashMap<>();

    // Master list preserving CSV order
    private final List<Game> allGames = new ArrayList<>();

    public GameDataRepository() {
        loadCSV("game.csv");
        
    }

    public GameDataRepository(String csvPath) {
        loadCSV(csvPath);
    
    }

    private void loadCSV(String path) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(path);

        try {
            BufferedReader reader;

            if (stream != null) {
                reader = new BufferedReader(new InputStreamReader(stream));
            } else {
                reader = new BufferedReader(new FileReader(path));
            }

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Skip header row
               if (firstLine) {
                firstLine = false;
                String clean = line.replace("\uFEFF", "").trim().toLowerCase();
                if (clean.startsWith("gameid")) continue;
            }


                parseAndStore(line);
                System.out.println("Parsing: " + line);

            }

            reader.close();

        } catch (IOException e) {
            System.err.println("GameRepository: could not load CSV from '" + path + "': " + e.getMessage());
        }
    }

    /**
     * Parses a single CSV row and registers the resulting Game object
     * in all internal indexes.
     *
     * Expected columns:
     *   0 gameID | 1 teamCountry | 2 opponentTeam | 3 location | 4 date
     */
    private void parseAndStore(String line) {
        String[] cols = line.split(",", -1);
        if (cols.length <5) {
            System.err.println("GameRepository: skipping malformed row: " + line);
            return;
        }

        try {
            String gameID = cols[0].trim();
            String teamCountry = cols[1].trim();
            String opponentTeam = cols[2].trim();
            String location = cols[3].trim();
            String dateString = cols[4].trim();

            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);

            Game game = new Game(gameID, teamCountry, opponentTeam, location, date);

            allGames.add(game);
            gamesByID.put(gameID, game);

            gamesByTeam
                .computeIfAbsent(teamCountry.toLowerCase(), k -> new ArrayList<>())
                .add(game);

        } catch (Exception e) {
            System.err.println("GameRepository: skipping row due to error: " + line);
        }
    }

    /** Returns all loaded games (unmodifiable). */
    public List<Game> getAllGames() {
        return Collections.unmodifiableList(allGames);
    }

    /** Looks up a game by ID. */
    public Game getGameByID(String id) {
        return gamesByID.get(id);
    }

    /** Returns all games played by a given team. */
    public List<Game> getGamesByTeam(String team) {
        if (team == null) return Collections.emptyList();
        List<Game> result = gamesByTeam.get(team.toLowerCase());
        return result != null ? Collections.unmodifiableList(result) : Collections.emptyList();
    }

    @Override
    public String toString() {
        return "GameRepository{games=" + allGames.size() + "}";
    }
}
 
