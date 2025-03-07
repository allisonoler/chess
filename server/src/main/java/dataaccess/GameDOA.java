package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDOA {

    void insertGame(GameData g) throws DataAccessException;

    void deleteGame(String id) throws DataAccessException;

    ArrayList<GameData> listGame() throws DataAccessException;

    void setGamePlayer(String id, String username, String playerColor) throws DataAccessException;

    void clear() throws DataAccessException;

    boolean empty();

    GameData getGame(String id) throws DataAccessException;
}
