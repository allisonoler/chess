package service;

import dataaccess.*;
import org.junit.jupiter.api.Test;
import service.requestsresults.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CreateTest {
    @Test
    public void positiveTest() throws DataAccessException, UnauthorizedException {
        UserDOA userDOA = new MemoryUserDAO();
        AuthDOA authDOA = new MemoryAuthDOA();
        GameDOA gameDOA = new MemoryGameDOA();
        GameService gameService = new GameService(gameDOA, authDOA);
        UserService userService = new UserService(userDOA, authDOA);
        userService.register(new RegisterRequest("allison", "chocolate", "linoler@gmail.com"));
        LoginResult loginResult = userService.login(new LoginRequest("allison", "chocolate"));
        CreateResult createResult = gameService.create(new CreateRequest(loginResult.authToken(), "game1"));
        assertNotNull(gameDOA.getGame(createResult.gameID()));
        assertNull(gameDOA.getGame("5"));
        assertNotNull(gameDOA.getGame("1"));
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