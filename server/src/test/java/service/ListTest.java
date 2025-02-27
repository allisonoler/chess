package service;

import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.Test;
import service.requestsresults.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ListTest {
    @Test
    public void positiveTest() throws DataAccessException, UnauthorizedException, ForbiddenException, BadRequestException {
        UserDOA userDOA = new MemoryUserDAO();
        AuthDOA authDOA = new MemoryAuthDOA();
        GameDOA gameDOA = new MemoryGameDOA();
        GameService gameService = new GameService(gameDOA, authDOA);
        UserService userService = new UserService(userDOA, authDOA);
        userService.register(new RegisterRequest("allison", "chocolate", "linoler@gmail.com"));
        LoginResult loginResult = userService.login(new LoginRequest("allison", "chocolate"));
        CreateResult createResult = gameService.create(new CreateRequest(loginResult.authToken(), "game1"));
        CreateResult createResul2 = gameService.create(new CreateRequest(loginResult.authToken(), "game2"));
        assertNotNull(gameDOA.getGame(createResult.gameID()));
        gameService.join(new JoinRequest(loginResult.authToken(), "WHITE", "1"));
        ArrayList<GameData> exampleGames = new ArrayList<GameData>();
        exampleGames.add(gameDOA.getGame(createResul2.gameID()));
        exampleGames.add(gameDOA.getGame(createResult.gameID()));
        assertEquals(gameService.list(new ListRequest(loginResult.authToken())).games(), exampleGames);
    }

    @Test
    public void negativeTest() throws DataAccessException, ForbiddenException, BadRequestException, UnauthorizedException {
        UserDOA userDOA = new MemoryUserDAO();
        AuthDOA authDOA = new MemoryAuthDOA();
        GameDOA gameDOA = new MemoryGameDOA();
        GameService gameService = new GameService(gameDOA, authDOA);
        UserService userService = new UserService(userDOA, authDOA);
        userService.register(new RegisterRequest("allison", "chocolate", "linoler@gmail.com"));
        LoginResult loginResult = userService.login(new LoginRequest("allison", "chocolate"));
        CreateResult createResult = gameService.create(new CreateRequest(loginResult.authToken(), "game1"));
        CreateResult createResul2 = gameService.create(new CreateRequest(loginResult.authToken(), "game2"));
        assertNotNull(gameDOA.getGame(createResult.gameID()));
        gameService.join(new JoinRequest(loginResult.authToken(), "WHITE", "1"));
        ArrayList<GameData> exampleGames = new ArrayList<GameData>();
        exampleGames.add(gameDOA.getGame(createResul2.gameID()));
        exampleGames.add(gameDOA.getGame(createResult.gameID()));
        userService.logout(new LogoutRequest(loginResult.authToken()));
        try {
            gameService.list(new ListRequest(loginResult.authToken()));
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }
    }
}