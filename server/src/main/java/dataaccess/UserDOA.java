package dataaccess;

import model.UserData;

public interface UserDOA {

    void insertUser(UserData u) throws DataAccessException;
    UserData readUser(String username) throws DataAccessException;
    void clear() throws DataAccessException;
    boolean empty() throws DataAccessException;

}
