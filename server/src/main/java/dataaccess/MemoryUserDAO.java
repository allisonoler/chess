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
        users.add(u);
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {

    }

    @Override
    public UserData readUser(String username) throws DataAccessException {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).username().equals(username)) {
                return users.get(i);
            }
        }
        return null;
    }

    @Override
    public void updateUser(String username, UserData u) throws DataAccessException {

    }

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public boolean empty() {
        return users.isEmpty();
    }
}
