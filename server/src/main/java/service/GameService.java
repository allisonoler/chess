package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import service.requestsresults.*;

import java.util.ArrayList;

public class GameService {
    GameDOA gameDOA;
    AuthDOA authDOA;
    private int curr_id;
    public GameService(GameDOA gameDOA, AuthDOA authDOA) {
        this.gameDOA = gameDOA;
        this.authDOA = authDOA;
        curr_id = 1;
    }
    public CreateResult create(CreateRequest createRequest) throws DataAccessException, UnauthorizedException {
        String gameID = Integer.toString(curr_id);
        curr_id+=1;
        AuthData auth = authDOA.getAuth(createRequest.authToken());
        if (auth != null) {
            gameDOA.insertGame(new GameData(gameID, null, null, createRequest.gameName(), new ChessGame()));
        } else {
            throw new UnauthorizedException("Not registered");
        }
        return new CreateResult(gameID);
    }
    public void join(JoinRequest joinRequest) throws UnauthorizedException, DataAccessException {
        AuthData auth = authDOA.getAuth(joinRequest.authToken());
        if (auth != null) {
            gameDOA.setGamePlayer(joinRequest.gameID(), auth.username(), joinRequest.playerColor());
        } else {
            throw new UnauthorizedException("Not registered");
        }
    }
    public ListResult list(ListRequest listRequest) throws DataAccessException, UnauthorizedException {
        AuthData auth = authDOA.getAuth(listRequest.authToken());
        ArrayList<GameData> games;
        if (auth != null) {
            games = gameDOA.listGame();
        } else {
            throw new UnauthorizedException("Not registered");
        }
        return new ListResult(games);
    }
}
