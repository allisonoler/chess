package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    UserDOA userDao;
    @BeforeEach
    void create() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        userDao = new SqlUserDOA();
        try (var conn = DatabaseManager.getConnection()) {
            var s = conn.prepareStatement("TRUNCATE user");
            try (s) {
                s.executeUpdate();
            }
        }
    }

    @AfterEach
    void destroy() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            var s = conn.prepareStatement("TRUNCATE user");
            try (s) {
                s.executeUpdate();
            }
        }
    }

    @Test
    public void positiveInsertTest() throws DataAccessException {
        userDao.insertUser(new UserData("allison", "chocolate", "linoler@gmail.com"));

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, "allison");
                try (var rs= ps.executeQuery()) {
                    if (rs.next()) {
                        assertEquals(rs.getString("password"),"chocolate");

                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Test
    public void negativeInsertTest() throws DataAccessException {
        userDao.insertUser(new UserData("allison", "chocolate", "linoler@gmail.com"));
        try {
            userDao.insertUser(new UserData("allison", "vanilla", "linoler@gmail.com"));
        } catch (DataAccessException e){
            assertNotNull(e);
            try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT username, password, email FROM user WHERE username=?";
                try (var ps = conn.prepareStatement(statement)) {
                    ps.setString(1, "allison");
                    try (var rs= ps.executeQuery()) {
                        if (rs.next()) {
                            assertEquals(rs.getString("password"),"chocolate");

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
        userDao.insertUser(new UserData("steve", "chocolate", "linoler@gmail.com"));
        assertEquals(userDao.readUser("steve").password(),"chocolate");
        assertEquals(userDao.readUser("steve").email(),"linoler@gmail.com");
        assertEquals(userDao.readUser("steve").username(),"steve");
    }

    @Test
    public void negativeReadTest() throws DataAccessException {
        try {
            assertNull(userDao.readUser("allison"));
        } catch (DataAccessException e){
            assertNotNull(e);
        }
    }

    @Test
    public void clearTest() throws DataAccessException {
        userDao.insertUser(new UserData("jon", "chocolate", "linoler@gmail.com"));
        assertNotNull(userDao.readUser("jon"));
        userDao.clear();
        assertNull(userDao.readUser("jon"));
    }
}