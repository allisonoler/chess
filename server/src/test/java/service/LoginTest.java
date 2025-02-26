package service;

import dataaccess.*;
import org.junit.jupiter.api.Test;
import service.requestsresults.LoginRequest;
import service.requestsresults.RegisterRequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LoginTest {
    @Test
    public void positiveTest() throws DataAccessException, UnauthorizedException, ForbiddenException, BadRequestException {
        UserDOA userDOA = new MemoryUserDAO();
        AuthDOA authDOA = new MemoryAuthDOA();
        UserService userService = new UserService(userDOA, authDOA);
        userService.register(new RegisterRequest("allison", "chocolate", "linoler@gmail.com"));
        userService.login(new LoginRequest("allison", "chocolate"));
        assertNotNull(userDOA.readUser("allison"));
    }

    @Test
    public void negativeTest() throws DataAccessException, ForbiddenException, BadRequestException {
        UserDOA userDOA = new MemoryUserDAO();
        AuthDOA authDOA = new MemoryAuthDOA();
        UserService userService = new UserService(userDOA, authDOA);
        try {
            userService.login(new LoginRequest("allison", "chocolate"));
        } catch (UnauthorizedException e) {
            assertNotNull(e);
        }
    }
}