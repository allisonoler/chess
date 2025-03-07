package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class SqlGameDOA implements GameDOA {

    public SqlGameDOA() throws DataAccessException {
        configureDatabase();
    }
    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof ChessGame p) ps.setString(i + 1, new Gson().toJson(p));
                    else if (param == null) ps.setString(i + 1, null);
                }
                ps.executeUpdate();
                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
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


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }

        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    @Override
    public void insertGame(GameData g) throws DataAccessException {
        var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        executeUpdate(statement, g.gameID(), g.whiteUsername(), g.blackUsername(), g.gameName(), g.game());
    }

    @Override
    public void deleteGame(String id) throws DataAccessException {
        var statement = "DELETE FROM game WHERE gameID=?";
        executeUpdate(statement, id);
    }

    @Override
    public ArrayList<GameData> listGame() throws DataAccessException {
        ArrayList<GameData> result = new ArrayList<>();

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs= ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(new GameData(rs.getString("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"), new Gson().fromJson(rs.getString("game"), ChessGame.class)));

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
        executeUpdate(statement, username, id);



    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
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
                        return new GameData(rs.getString("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"), new Gson().fromJson(rs.getString("game"), ChessGame.class));

                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }
}