package service;

import dataaccess.*;
import org.junit.jupiter.api.Test;
import requestsresults.CreateRequest;
import requestsresults.CreateResult;
import requestsresults.RegisterRequest;
import requestsresults.RegisterResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CreateTest {
    @Test
    public void positiveTest() throws DataAccessException, UnauthorizedException, ForbiddenException, BadRequestException {
        UserDOA userDOA = new MemoryUserDAO();
        AuthDOA authDOA = new MemoryAuthDOA();
        GameDOA gameDOA = new MemoryGameDOA();
        GameService gameService = new GameService(gameDOA, authDOA);
        UserService userService = new UserService(userDOA, authDOA);
        RegisterResult registerResult = userService.register(new RegisterRequest("allison", "hi", "linoler@gmail.com"));
        CreateResult createResult = gameService.create(new CreateRequest(registerResult.authToken(), "game1"));
        assertNotNull(gameDOA.getGame(createResult.gameID()));
        assertNull(gameDOA.getGame("5"));
        assertNotNull(gameDOA.getGame("1"));
    }

    @Test
    public void negativeTest() throws DataAccessException {
        UserDOA userDOA = new MemoryUserDAO();
        AuthDOA authDOA = new MemoryAuthDOA();
        GameDOA gameDOA = new MemoryGameDOA();
        UserService userService = new UserService(userDOA, authDOA);
        GameService gameService = new GameService(gameDOA, authDOA);
        try {
            gameService.create(new CreateRequest("iamloggedin", "game1"));
        } catch (UnauthorizedException e) {
            assertNotNull(e);
        }
    }
}