package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class SqlAuthDOA implements AuthDOA {

    public SqlAuthDOA() throws DataAccessException {
        configureDatabase();
    }
    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
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
            CREATE TABLE IF NOT EXISTS  auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
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
    public void insertAuth(AuthData u) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, u.authToken(), u.username());
    }

    @Override
    public void deleteAuth(String authtoken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        executeUpdate(statement, authtoken);
    }

    @Override
    public AuthData getAuth(String authtoken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authtoken);
                try (var rs= ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("username"), rs.getString("authToken"));

                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    @Override
    public boolean empty() {
        return false;
    }
}