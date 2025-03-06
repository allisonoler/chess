package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDOA {
    void insertAuth(AuthData u) throws DataAccessException;

    void deleteAuth(String authtoken) throws DataAccessException;

    AuthData getAuth(String authtoken) throws DataAccessException;
    void clear() throws DataAccessException;

    boolean empty();
}
