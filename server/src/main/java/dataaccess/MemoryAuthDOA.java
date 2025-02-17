package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public class MemoryAuthDOA implements AuthDOA{
    private ArrayList<AuthData> auths;
    public MemoryAuthDOA() {
        auths = new ArrayList<AuthData>();
    }

    @Override
    public void insertAuth(AuthData u) throws DataAccessException {
        auths.add(u);

    }

    @Override
    public void deleteAuth(String authtoken) throws DataAccessException {
        auths.remove(getAuth(authtoken));

    }

    public AuthData getAuth(String authtoken) throws DataAccessException {
        for (int i = 0; i < auths.size(); i++) {
            if (auths.get(i).authToken() == authtoken) {
                return auths.get(i);
            }
        }
        return null;
    }

    @Override
    public void updateAuth(String username, AuthData u) throws DataAccessException {

    }

    @Override
    public void clear() {
        auths.clear();
    }
}
