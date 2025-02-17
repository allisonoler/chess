package dataaccess;

import model.UserData;

public class UserDOA {

    void insertUser(UserData u) throws DataAccessException {}

    void deleteUser(String username) throws DataAccessException {}

    UserData readUser(String username) throws DataAccessException {return null;}

    void updateUser(String username, UserData u) throws DataAccessException {}

}
