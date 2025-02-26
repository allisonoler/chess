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
//        AuthData auth = authDOA.getAuth(createRequest.authToken());
//        if (auth != null) {
        gameDOA.insertGame(new GameData(gameID, null, null, createRequest.gameName(), new ChessGame()));
//        } else {
//            throw new UnauthorizedException("Not registered");
//        }
        return new CreateResult(gameID);
    }
    public void join(JoinRequest joinRequest) throws UnauthorizedException, DataAccessException, BadRequestException, ForbiddenException {
        AuthData auth = authDOA.getAuth(joinRequest.authToken());
        if (joinRequest.playerColor() == null) {
            throw new BadRequestException("no color given");
        } else if (!(joinRequest.playerColor().equals("WHITE") || joinRequest.playerColor().equals("BLACK"))) {
            throw new BadRequestException("bad color");
        }
        if (gameDOA.getGame(joinRequest.gameID()) == null) {
            throw new BadRequestException("game not found");
        }
        GameData game = gameDOA.getGame(joinRequest.gameID());
        if (game.whiteUsername()!= null && joinRequest.playerColor().equals("WHITE") || game.blackUsername()!= null && joinRequest.playerColor().equals("BLACK")) {
            throw new ForbiddenException("can't steal team");
        }
        if (auth != null) {
            gameDOA.setGamePlayer(joinRequest.gameID(), auth.username(), joinRequest.playerColor());
        } else {
            throw new UnauthorizedException("Not registered");
        }
    }
    public ListResult list(ListRequest listRequest) throws DataAccessException, UnauthorizedException {
//        AuthData auth = authDOA.getAuth(listRequest.authToken());
        ArrayList<GameData> games;
//        if (auth != null) {
        games = gameDOA.listGame();
//        } else {
//            throw new UnauthorizedException("Not registered");
//        }
        return new ListResult(games);
    }
}
