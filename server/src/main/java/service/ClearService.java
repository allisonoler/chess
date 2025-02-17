package service;

import dataaccess.GameDOA;

public class ClearService {
    GameDOA gameDOA;
    public ClearService(GameDOA gameDOA) {
        this.gameDOA = gameDOA;
    }
    public void clear() {
        gameDOA.clear();
    }
}
