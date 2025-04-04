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
        games.add(g);
    }

    @Override
    public void deleteGame(String id) throws DataAccessException {
        games.remove(getGame(id));
    }

    @Override
    public ArrayList<GameData> listGame() throws DataAccessException {
        return games;
    }

    @Override
    public void setGamePlayer(String id, String username, String playerColor) throws DataAccessException {
        GameData game = getGame(id);
        GameData newGame;
        if (playerColor == null) {
            throw new DataAccessException("bad request");
        }
        if (playerColor.equals("WHITE")) {
            newGame = new GameData(id, username, game.blackUsername(), game.gameName(), game.game());
        } else {
            newGame = new GameData(id, game.whiteUsername(), username, game.gameName(), game.game());
        }
        deleteGame(id);
        insertGame(newGame);
    }

    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public boolean empty() {
        return games.isEmpty();
    }

    @Override
    public GameData getGame(String gameID) {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).gameID().equals(gameID)) {
                return games.get(i);
            }
        }
        return null;
    }

    @Override
    public void updateGame(String id, GameData g) throws DataAccessException {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).gameID().equals(id)) {
                games.set(i, g);
            }
        }
    }
}
