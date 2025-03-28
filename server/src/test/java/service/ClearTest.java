package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClearTest {
    @Test
    public void positiveTest() throws DataAccessException {
        GameDOA gameDOA = new MemoryGameDOA();
        UserDOA userDOA = new MemoryUserDAO();
        AuthDOA authDOA = new MemoryAuthDOA();
        gameDOA.insertGame(new GameData("55", "allison", "steve", "lol", new ChessGame()));
        ClearService clearService = new ClearService(gameDOA, userDOA, authDOA);
        clearService.clear();
        assertTrue(gameDOA.empty());
        assertTrue(userDOA.empty());
        assertTrue(authDOA.empty());
    }
}