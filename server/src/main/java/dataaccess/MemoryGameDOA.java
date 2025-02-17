package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class MemoryGameDOA implements GameDOA {

    private ArrayList<GameData> games;
    public MemoryGameDOA() {
        games = new ArrayList<GameData>();
    }
    @Override
    public void insertGame(GameData g) throws DataAccessException {

    }

    @Override
    public void deleteGame(String id) throws DataAccessException {

    }

    @Override
    public ArrayList<GameData> listGame(String id) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(String id, GameData g) throws DataAccessException {

    }
}
