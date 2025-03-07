package service;

import dataaccess.AuthDOA;
import dataaccess.DataAccessException;
import dataaccess.UserDOA;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
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
    public RegisterResult register(RegisterRequest registerRequest) throws ForbiddenException, BadRequestException {
        String authToken = generateToken();
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new BadRequestException("bad request");
        }
        try {
            UserData user = userDOA.readUser(registerRequest.username());
            if (user == null) {
                userDOA.insertUser(new UserData(registerRequest.username(), BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt()), registerRequest.email()));

                authDOA.insertAuth(new AuthData(registerRequest.username(), authToken));

            } else {
                throw new ForbiddenException("Forbidden");
            }
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }
        return new RegisterResult(registerRequest.username(), authToken);
    }
    public LoginResult login(LoginRequest loginRequest) throws UnauthorizedException, BadRequestException {
        String authToken = generateToken();
        try {
            UserData user = userDOA.readUser(loginRequest.username());
            if (user!=null) {
                if (BCrypt.checkpw(loginRequest.password(), user.password())) {
                    authDOA.insertAuth(new AuthData(loginRequest.username(), authToken));
                } else {
                    throw new UnauthorizedException("Unauthorized");
                }
            }
            else {
                throw new UnauthorizedException("Unauthorized");
            }
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }
        return new LoginResult(loginRequest.username(), authToken);
    }
    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        try {
            authDOA.deleteAuth(logoutRequest.authToken());
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}