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
        assertEquals(gameDOA.listGame(), exampleGames);
    }

//    @Test
//    public void negativeTest() throws DataAccessException {
//        UserDOA userDOA = new MemoryUserDAO();
//        AuthDOA authDOA = new MemoryAuthDOA();
//        UserService userService = new UserService(userDOA, authDOA);
//        userService.register(new RegisterRequest("allison", "chocolate", "linoler@gmail.com"));
//        userService.register(new RegisterRequest("steve", "chocolate", "linoler@gmail.com"));
//        LoginResult loginResult = userService.login(new LoginRequest("allison", "chocolate"));
//        LoginResult loginResult2 = userService.login(new LoginRequest("steve", "chocolate"));
//        userService.logout(new LogoutRequest(loginResult.authToken()));
//        assertNotNull(authDOA.getAuth(loginResult2.authToken()));
//        assertNull(authDOA.getAuth(loginResult.authToken()));
//    }
}