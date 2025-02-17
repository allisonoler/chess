package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class MemoryUserDAO implements UserDOA{
    private ArrayList<UserData> users;
    public MemoryUserDAO() {
        users = new ArrayList<>();
    }
    @Override
    public void insertUser(UserData u) throws DataAccessException {

    }

    @Override
    public void deleteUser(String username) throws DataAccessException {

    }

    @Override
    public UserData readUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void updateUser(String username, UserData u) throws DataAccessException {

    }
}
