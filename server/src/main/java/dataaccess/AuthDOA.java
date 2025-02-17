package dataaccess;

import model.AuthData;
import model.UserData;

public class AuthDOA {
    void insertAuth(AuthData u) throws DataAccessException {}

    void deleteAuth(String username) throws DataAccessException {}

    AuthData readAuth(String username) throws DataAccessException {return null;}

    void updateAuth(String username, AuthData u) throws DataAccessException {}
}
