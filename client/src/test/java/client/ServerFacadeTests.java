package client;

import org.junit.jupiter.api.*;
import server.Server;
import service.requestsresults.*;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        serverFacade = new ServerFacade("http://localhost:8080");
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws ResponseException {
        serverFacade.clear();
    }

    @Test
    public void registerPositive() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("username", "password", "email"));
        assertNotNull(registerResult.authToken());
    }

    @Test
    public void registerNegative() throws ResponseException {
        serverFacade.register(new RegisterRequest("username", "password", "email"));
        try {
            serverFacade.register(new RegisterRequest("username", "password", "email"));

        } catch (ResponseException e) {
            assertEquals(403, e.StatusCode());
        }
    }

    @Test
    public void loginPositive() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("username", "password", "email"));
        serverFacade.logout(new LogoutRequest(registerResult.authToken()));
        LoginResult loginResult = serverFacade.login(new LoginRequest("username", "password"));
        assertNotNull(loginResult.authToken());
    }

    @Test
    public void loginNegative() {
        try {
            LoginResult loginResult = serverFacade.login(new LoginRequest("username", "password"));
            assertNotNull(loginResult.authToken());
        } catch (ResponseException e) {
            assertEquals(401, e.StatusCode());
        }
    }

    @Test
    public void logoutPositive() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("username", "password", "email"));
        serverFacade.logout(new LogoutRequest(registerResult.authToken()));
        try {
            serverFacade.create(new CreateRequest("none", "hello"));
        } catch (ResponseException e) {
            assertEquals(401, e.StatusCode());
        }
    }

    @Test
    public void logoutNegative() {
        try {
            serverFacade.logout(new LogoutRequest("hi"));
        } catch (ResponseException e) {
            assertEquals(401, e.StatusCode());
        }
    }

    @Test
    public void createPositive() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("test", "test", "test"));
        CreateResult createResult = serverFacade.create(new CreateRequest(registerResult.authToken(), "game1"));
        assertNotNull(createResult.gameID());
    }

    @Test
    public void createNegative() {
        try {
            serverFacade.create(new CreateRequest("none", "game1"));
        } catch (ResponseException e) {
            assertEquals(401, e.StatusCode());
        }

    }

    @Test
    public void listPositive() throws ResponseException{
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("test", "test", "test"));
        serverFacade.create(new CreateRequest(registerResult.authToken(), "game1"));
        serverFacade.create(new CreateRequest(registerResult.authToken(), "game1"));
        ListResult listResult = serverFacade.list(new ListRequest(registerResult.authToken()));
        assertNotNull(listResult.games());
    }

    @Test
    public void listNegative() {
        try {
            serverFacade.list(new ListRequest("hi"));
        } catch (ResponseException e) {
            assertEquals(401, e.StatusCode());
        }
    }

    @Test
    public void joinPositive() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("test", "test", "test"));
        CreateResult createResult = serverFacade.create(new CreateRequest(registerResult.authToken(), "game1"));
        serverFacade.join(new JoinRequest(registerResult.authToken(), "WHITE",createResult.gameID()));
    }

    @Test
    public void joinNegative() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("test", "test", "test"));
        CreateResult createResult = serverFacade.create(new CreateRequest(registerResult.authToken(), "game1"));
        serverFacade.join(new JoinRequest(registerResult.authToken(), "WHITE",createResult.gameID()));
        try {
            serverFacade.join(new JoinRequest(registerResult.authToken(), "WHITE",createResult.gameID()));
        } catch (ResponseException e) {
            assertEquals(403, e.StatusCode());
        }
    }

}
