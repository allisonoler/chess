package dataaccess;

import model.AuthData;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MemoryAuthDOA implements AuthDOA{
    private ArrayList<AuthData> auths;
    public MemoryAuthDOA() {
        auths = new ArrayList<AuthData>();
    }

    @Override
    public void insertAuth(AuthData u) throws DataAccessException {

    }

    @Override
    public void deleteAuth(String username) throws DataAccessException {

    }

    public AuthData getAuth(String authtoken) throws DataAccessException {
        return null;
    }

    @Override
    public void updateAuth(String username, AuthData u) throws DataAccessException {

    }
}
