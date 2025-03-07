package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        userDao = new SqlUserDOA();
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
        assertEquals(userDao.readUser("allison").password(),"chocolate");
    }

    @Test
    public void negativeInsertTest() throws DataAccessException {
        userDao.insertUser(new UserData("allison", "chocolate", "linoler@gmail.com"));
        try {
            userDao.insertUser(new UserData("allison", "vanilla", "linoler@gmail.com"));
        } catch (DataAccessException e){
            assertNotNull(e);
            assertEquals(userDao.readUser("allison").password(),"chocolate");
        }
    }
}