package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDOA;
import dataaccess.MemoryGameDOA;
import model.GameData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClearTest {
    @Test
    public void positiveTest() throws DataAccessException {
        GameDOA gameDOA = new MemoryGameDOA();
        gameDOA.insertGame(new GameData(55, "allison", "steve", "lol", new ChessGame()));
        ClearService clearService = new ClearService(gameDOA);
        clearService.clear();
        assertTrue(gameDOA.empty());
    }
}