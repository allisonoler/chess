package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTest {

    AuthDOA authDao;
    @BeforeEach
    void create() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        authDao = new SqlAuthDOA();
        try (var conn = DatabaseManager.getConnection()) {
            var s2 = conn.prepareStatement("TRUNCATE auth");
            try (s2) {
                s2.executeUpdate();
            }
        }
    }

    @AfterEach
    void destroy() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            var s2 = conn.prepareStatement("TRUNCATE auth");
            try (s2) {
                s2.executeUpdate();
            }
        }
    }

    @Test
    public void positiveInsertTest() throws DataAccessException {
        authDao.insertAuth(new AuthData("allison", "chocolate"));
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, authToken FROM auth WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, "allison");
                try (var rs= ps.executeQuery()) {
                    if (rs.next()) {
                        assertEquals(rs.getString("authToken"),"chocolate");

                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Test
    public void negativeInsertTest() throws DataAccessException {
        authDao.insertAuth(new AuthData("allison", "chocolate"));
        try {
            authDao.insertAuth(new AuthData("allison", "vanilla"));
        } catch (DataAccessException e){
            assertNotNull(e);
            try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT username, authToken FROM auth WHERE username=?";
                try (var ps = conn.prepareStatement(statement)) {
                    ps.setString(1, "allison");
                    try (var rs= ps.executeQuery()) {
                        if (rs.next()) {
                            assertEquals(rs.getString("authToken"),"chocolate");

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
        authDao.insertAuth(new AuthData("steve", "chocolate"));
        assertEquals(authDao.getAuth("chocolate").username(),"steve");
        assertEquals(authDao.getAuth("chocolate").authToken(),"chocolate");
    }

    @Test
    public void negativeReadTest() throws DataAccessException {
        try {
            assertNull(authDao.getAuth("allison"));
        } catch (DataAccessException e){
            assertNotNull(e);
        }
    }

    @Test
    public void positiveDeleteTest() throws DataAccessException {
        authDao.insertAuth(new AuthData("charlotte", "chocolate"));
        assertNotNull(authDao.getAuth("chocolate"));
        authDao.deleteAuth("chocolate");
        assertNull(authDao.getAuth("chocolate"));
    }

    @Test
    public void negativeDeleteTest() throws DataAccessException {
        assertNull(authDao.getAuth("chocolate"));
        try {
            authDao.deleteAuth("chocolate");
        } catch (Exception e) {
            assertNotNull(e);
        }

    }

    @Test
    public void clearTest() throws DataAccessException {
        authDao.insertAuth(new AuthData("jon", "chocolate"));
        assertNotNull(authDao.getAuth("chocolate"));
        authDao.clear();
        assertNull(authDao.getAuth("chocolate"));
    }
}