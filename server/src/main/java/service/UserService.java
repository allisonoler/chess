package service;

import dataaccess.AuthDOA;
import dataaccess.DataAccessException;
import dataaccess.UserDOA;
import model.AuthData;
import model.UserData;
import service.requestsresults.*;

import java.util.UUID;

public class UserService {
    private UserDOA userDOA;
    private AuthDOA authDOA;
    public UserService(UserDOA userDOA, AuthDOA authDOA) {
        this.userDOA = userDOA;
        this.authDOA = authDOA;
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
    public RegisterResult register(RegisterRequest registerRequest) {
        String authToken = generateToken();
        try {
            UserData user = userDOA.readUser(registerRequest.username());
            if (user == null) {
                userDOA.insertUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));

                authDOA.insertAuth(new AuthData(registerRequest.username(), authToken));

            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return new RegisterResult(registerRequest.username(), authToken);
    }
    public LoginResult login(LoginRequest loginRequest) {
        String authToken = generateToken();
        try {
            UserData user = userDOA.readUser(loginRequest.username());
            if (user!=null) {
                authDOA.insertAuth(new AuthData(loginRequest.username(), authToken));
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return new LoginResult(loginRequest.username(), authToken);
    }
    public void logout(LogoutRequest logoutRequest) {
        try {
            AuthData authData = authDOA.getAuth(logoutRequest.authToken());
            if (authData != null) {
                authDOA.deleteAuth(logoutRequest.authToken());
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}