package service;

import dataaccess.AuthDOA;
import dataaccess.DataAccessException;
import dataaccess.GameDOA;
import dataaccess.UserDOA;

public class ClearService {
    GameDOA gameDOA;
    UserDOA userDOA;
    AuthDOA authDOA;
    public ClearService(GameDOA gameDOA, UserDOA userDOA, AuthDOA authDOA) {
        this.gameDOA = gameDOA;
        this.userDOA = userDOA;
        this.authDOA = authDOA;
    }
    public void clear() throws DataAccessException {
        gameDOA.clear();
        userDOA.clear();
        authDOA.clear();
    }
}
