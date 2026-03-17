package com.example.hockeystats;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.hockeystats.model.Game;
import com.example.hockeystats.model.GameDataRepository;

public class GameRepoTest {
    
    @Test
    void testLoadAmt(){
       GameDataRepository repo = new GameDataRepository("games.csv") ;
       assertEquals(20, repo.getAllGames()
    );
    }

    @Test
    void testGetGameByID(){
        GameDataRepository repo = new GameDataRepository("games.csv") ;
        Game game = repo.getGameByID("W-CAN-USA-2022");

        assertNotNull(game);
        assertEquals("Canada", game.getTeamCountry());
        assertEquals("USA", game.getOpponentTeam());
    }

    @Test
    void testGetGamesByTeam(){
        GameDataRepository repo = new GameDataRepository("games.csv") ;
        List<Game> canadaGames = repo.getGamesByTeam("canada");
        assertEquals(4, canadaGames.size());
    }

}
