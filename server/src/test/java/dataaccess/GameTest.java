package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    GameDOA gameDao;
    @BeforeEach
    void create() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        gameDao = new SqlGameDOA();
        try (var conn = DatabaseManager.getConnection()) {
            var s2 = conn.prepareStatement("TRUNCATE game");
            try (s2) {
                s2.executeUpdate();
            }
        }
    }

    @AfterEach
    void destroy() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        gameDao = new SqlGameDOA();
        try (var conn = DatabaseManager.getConnection()) {
            var s2 = conn.prepareStatement("TRUNCATE game");
            try (s2) {
                s2.executeUpdate();
            }
        }
    }

    @Test
    public void positiveInsertTest() throws DataAccessException {
        gameDao.insertGame(new GameData("1", null, null, "fungame", new ChessGame()));
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, "1");
                try (var rs= ps.executeQuery()) {
                    if (rs.next()) {
                        assertEquals(rs.getString("gameName"),"fungame");
                        assertNotEquals(rs.getString("whiteUsername"), "allison");
                        assertInstanceOf(String.class, rs.getObject("game"));

                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Test
    public void negativeInsertTest() throws DataAccessException {
        gameDao.insertGame(new GameData("1", null, null, "fungame", new ChessGame()));
        try {
            gameDao.insertGame(new GameData("1", null, null, "fungame", new ChessGame()));
        } catch (DataAccessException e){
            assertNotNull(e);
            try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID=?";
                try (var ps = conn.prepareStatement(statement)) {
                    ps.setString(1, "1");
                    try (var rs= ps.executeQuery()) {
                        if (rs.next()) {
                            assertEquals(rs.getString("gameID"),"1");

                        }
                    }
                }
            } catch (Exception ex){
                throw new DataAccessException(String.format("Unable to read data: %s", ex.getMessage()));
            }
        }
    }

    @Test
    public void positiveReadTest() throws DataAccessException {
        gameDao.insertGame(new GameData("2", null, null, "fungame", new ChessGame()));
        assertEquals(gameDao.getGame("2").gameName(),"fungame");
        assertInstanceOf(ChessGame.class,gameDao.getGame("2").game());
    }

    @Test
    public void negativeReadTest() throws DataAccessException {
        try {
            assertNull(gameDao.getGame("allison"));
        } catch (DataAccessException e){
            assertNotNull(e);
        }
    }

    @Test
    public void positiveDeleteTest() throws DataAccessException {
        gameDao.insertGame(new GameData("3", null, null, "fungame", new ChessGame()));
        assertNotNull(gameDao.getGame("3"));
        gameDao.deleteGame("3");
        assertNull(gameDao.getGame("3"));
    }
//
//    @Test
//    public void negativeDeleteTest() throws DataAccessException {
//        assertNull(authDao.getAuth("chocolate"));
//        try {
//            authDao.deleteAuth("chocolate");
//        } catch (Exception e) {
//            assertNotNull(e);
//        }
//
//    }
//
//    @Test
//    public void clearTest() throws DataAccessException {
//        authDao.insertAuth(new AuthData("jon", "chocolate"));
//        assertNotNull(authDao.getAuth("chocolate"));
//        authDao.clear();
//        assertNull(authDao.getAuth("chocolate"));
//    }
}