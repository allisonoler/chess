package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import client.ServerFacade;
import service.requestsresults.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

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

    @AfterEach
    public void clearDatabase2() throws ResponseException {
        serverFacade.clear();
    }



    @Test
    public void logoutPositive() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("username", "password", "email"));
        serverFacade.logout(new LogoutRequest(registerResult.authToken()));
//        assertTrue(facade.logout());
    }

    @Test
    public void logoutNegative() {
//        assertFalse(facade.logout());
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
    public void createNegative() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("test", "test", "test"));
        CreateResult createResult = serverFacade.create(new CreateRequest(registerResult.authToken(), "game1"));
        CreateResult createResult2 = serverFacade.create(new CreateRequest(registerResult.authToken(), "game1"));
        assertNotNull(createResult2.gameID());
    }

    @Test
    public void listPositive() throws ResponseException, URISyntaxException, IOException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("test", "test", "test"));
        CreateResult createResult = serverFacade.create(new CreateRequest(registerResult.authToken(), "game1"));
        CreateResult createResult2 = serverFacade.create(new CreateRequest(registerResult.authToken(), "game1"));
        ListResult listResult = serverFacade.list(new ListRequest(registerResult.authToken()));
        assertNotNull(listResult.games());
    }

    @Test
    public void joinPositive() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("test", "test", "test"));
        CreateResult createResult = serverFacade.create(new CreateRequest(registerResult.authToken(), "game1"));
        serverFacade.join(new JoinRequest(registerResult.authToken(), "WHITE",createResult.gameID()));
    }

}
