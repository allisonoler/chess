package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import javax.xml.crypto.Data;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class SqlGameDOA implements GameDOA {

    public SqlGameDOA() throws DataAccessException {
        DatabaseManager.configureDatabase(createStatements);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
              `gameID` varchar(256),
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256),
              `game` TEXT,
              PRIMARY KEY (`gameID`)
            );
            """

    };

    @Override
    public void insertGame(GameData g) throws DataAccessException {
        var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        DatabaseManager.executeUpdate(statement, g.gameID(), g.whiteUsername(), g.blackUsername(), g.gameName(), g.game());
    }

    @Override
    public void deleteGame(String id) throws DataAccessException {
        var statement = "DELETE FROM game WHERE gameID=?";
        DatabaseManager.executeUpdate(statement, id);
    }

    @Override
    public ArrayList<GameData> listGame() throws DataAccessException {
        ArrayList<GameData> result = new ArrayList<>();

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs= ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(new GameData(rs.getString("gameID"),
                                rs.getString("whiteUsername"), rs.getString("blackUsername"),
                                rs.getString("gameName"), new Gson().fromJson(rs.getString("game"),
                                ChessGame.class)));

                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public void setGamePlayer(String id, String username, String playerColor) throws DataAccessException {
        var statement = "";
        if (playerColor == null) {
            throw new DataAccessException("bad request");
        }
        if (playerColor.equals("WHITE")) {
            statement = "UPDATE game SET whiteUsername=? WHERE gameID=?";
        } else {
            statement = "UPDATE game SET blackUsername=? WHERE gameID=?";
        }
        DatabaseManager.executeUpdate(statement, username, id);



    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE game";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public boolean empty() {
        return false;
    }

    @Override
    public GameData getGame(String id) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, id);
                try (var rs= ps.executeQuery()) {
                    if (rs.next()) {
                        return new GameData(rs.getString("gameID"), rs.getString("whiteUsername"),
                                rs.getString("blackUsername"), rs.getString("gameName"),
                                new Gson().fromJson(rs.getString("game"), ChessGame.class));

                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }
}