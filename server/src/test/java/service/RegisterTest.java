package service;

import dataaccess.DataAccessException;
import dataaccess.*;
import dataaccess.UserDOA;
import org.junit.jupiter.api.Test;
import requestsresults.RegisterRequest;
import requestsresults.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterTest {
    @Test
    public void positiveTest() throws DataAccessException, ForbiddenException, BadRequestException {
        UserDOA userDOA = new MemoryUserDAO();
        AuthDOA authDOA = new MemoryAuthDOA();
        UserService userService = new UserService(userDOA, authDOA);
        RegisterResult registerResult = userService.register(new RegisterRequest("allison", "chocolate", "linoler@gmail.com"));
        assertNotNull(userDOA.readUser("allison"));
        assertNotNull(authDOA.getAuth(registerResult.authToken()));
    }

    @Test
    public void negativeTest() throws DataAccessException, ForbiddenException, BadRequestException {
        UserDOA userDOA = new MemoryUserDAO();
        AuthDOA authDOA = new MemoryAuthDOA();
        UserService userService = new UserService(userDOA, authDOA);
        userService.register(new RegisterRequest("allison", "chocolate", "linoler@gmail.com"));
        assertNull(userDOA.readUser("steve"));
    }
}