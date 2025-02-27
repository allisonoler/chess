package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.requestsresults.*;

import static org.junit.jupiter.api.Assertions.*;

public class JoinTest {
    UserDOA userDOA;
    AuthDOA authDOA;
    GameDOA gameDOA;

    GameService gameService;

    UserService userService;

    @BeforeEach
    public void setUp() {
        userDOA = new MemoryUserDAO();
        authDOA = new MemoryAuthDOA();
        gameDOA = new MemoryGameDOA();
        gameService = new GameService(gameDOA, authDOA);
        userService = new UserService(userDOA, authDOA);
    }

    @Test
    public void positiveTest() throws DataAccessException, UnauthorizedException, ForbiddenException, BadRequestException {
        RegisterResult registerResult = userService.register(new RegisterRequest("allison", "no", "linoler@gmail.com"));
        CreateResult createResult = gameService.create(new CreateRequest(registerResult.authToken(), "game1"));
        assertNotNull(gameDOA.getGame(createResult.gameID()));
        gameService.join(new JoinRequest(registerResult.authToken(), "WHITE", "1"));
        assertEquals(gameDOA.getGame("1").whiteUsername(), "allison");
    }

    @Test
    public void negativeTest() throws DataAccessException, ForbiddenException, BadRequestException, UnauthorizedException {
        RegisterResult registerResult = userService.register(new RegisterRequest("allison", "no", "linoler@gmail.com"));
        CreateResult createResult = gameService.create(new CreateRequest(registerResult.authToken(), "game1"));
        gameService.join(new JoinRequest(registerResult.authToken(), "WHITE", "1"));
        try {
            gameService.join(new JoinRequest(registerResult.authToken(), "WHITE", "1"));
        } catch (ForbiddenException e) {
            assertNotNull(e);
        }
    }
}