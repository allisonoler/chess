package service;

import dataaccess.GameDOA;
import dataaccess.UserDOA;

public class ClearService {
    GameDOA gameDOA;
    UserDOA userDOA;
    public ClearService(GameDOA gameDOA, UserDOA userDOA) {
        this.gameDOA = gameDOA;
        this.userDOA = userDOA;
    }
    public void clear() {
        gameDOA.clear();
        userDOA.clear();
    }
}
