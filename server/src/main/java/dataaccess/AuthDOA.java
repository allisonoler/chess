package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDOA {
    void insertAuth(AuthData u) throws DataAccessException;

    void deleteAuth(String username) throws DataAccessException;

    AuthData getAuth(String authtoken) throws DataAccessException;

    void updateAuth(String username, AuthData u) throws DataAccessException;
}
