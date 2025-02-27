package service;

import dataaccess.*;
import org.eclipse.jetty.util.log.Log;
import org.junit.jupiter.api.Test;
import service.requestsresults.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LogoutTest {
    @Test
    public void positiveTest() throws DataAccessException, UnauthorizedException, ForbiddenException, BadRequestException {
        UserDOA userDOA = new MemoryUserDAO();
        AuthDOA authDOA = new MemoryAuthDOA();
        UserService userService = new UserService(userDOA, authDOA);
        userService.register(new RegisterRequest("allison", "chocolate", "linoler@gmail.com"));
        LoginResult loginResult = userService.login(new LoginRequest("allison", "chocolate"));
        userService.logout(new LogoutRequest(loginResult.authToken()));
        assertNull(authDOA.getAuth(loginResult.authToken()));
    }

    @Test
    public void negativeTest() throws DataAccessException, UnauthorizedException, ForbiddenException, BadRequestException {
        UserDOA userDOA = new MemoryUserDAO();
        AuthDOA authDOA = new MemoryAuthDOA();
        UserService userService = new UserService(userDOA, authDOA);
        RegisterResult registerResult = userService.register(new RegisterRequest("allison", "chocolate", "linoler@gmail.com"));
        userService.logout(new LogoutRequest("hi"));
        assertNotNull(authDOA.getAuth(registerResult.authToken()));
    }
}