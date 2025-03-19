package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import client.ServerFacade;
import service.requestsresults.*;

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
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

//    @Test
//    public void logoutPositive() throws ResponseException {
//        RegisterResult registerResult = serverFacade.register(new RegisterRequest("username", "password", "email"));
//        serverFacade.logout(new LogoutRequest(registerResult.authToken()));
////        assertTrue(facade.logout());
//    }
//
//    @Test
//    public void logoutNegative() {
////        assertFalse(facade.logout());
//    }
//
//    @Test
//    public void createPositive() throws ResponseException {
//        RegisterResult registerResult = serverFacade.register(new RegisterRequest("test", "test", "test"));
//        CreateResult createResult = serverFacade.create(new CreateRequest(registerResult.authToken(), "game1"));
//        assertNotNull(createResult.gameID());
//    }
//
//    @Test
//    public void createNegative() throws ResponseException {
//        RegisterResult registerResult = serverFacade.register(new RegisterRequest("test", "test", "test"));
//        CreateResult createResult = serverFacade.create(new CreateRequest(registerResult.authToken(), "game1"));
//        CreateResult createResult2 = serverFacade.create(new CreateRequest(registerResult.authToken(), "game1"));
//        assertNotNull(createResult2.gameID());
//    }

    @Test
    public void listPositive() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("test", "test", "test"));
        CreateResult createResult = serverFacade.create(new CreateRequest(registerResult.authToken(), "game1"));
        CreateResult createResult2 = serverFacade.create(new CreateRequest(registerResult.authToken(), "game1"));
//        ArrayList<GameData> games = new ArrayList<GameData>();
//        games.add(new GameData("hi", null, null, "hi", new ChessGame()));
//        games.add(new GameData("hi2", null, null, "hi", new ChessGame()));
//        ListResult goodListResult = new ListResult(games);
//        String jsonResult = new Gson().toJson(goodListResult);
//        ListResult changedListResult = new Gson().fromJson(jsonResult, ListResult.class);
//        System.out.println(changedListResult);
        ListResult listResult = serverFacade.list(new ListRequest(registerResult.authToken()));
        assertNotNull(listResult.games());
    }

}
