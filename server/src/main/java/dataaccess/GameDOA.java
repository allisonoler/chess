package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDOA {

    void insertGame(GameData g) throws DataAccessException;

    void deleteGame(String id) throws DataAccessException;

    ArrayList<GameData> listGame(String id) throws DataAccessException;

    void updateGame(String id, GameData g) throws DataAccessException;
}
