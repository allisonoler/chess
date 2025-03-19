package client;

import org.junit.jupiter.api.*;
import server.Server;
import client.ServerFacade;
import service.requestsresults.LogoutRequest;
import service.requestsresults.RegisterRequest;
import service.requestsresults.RegisterResult;


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

    @Test
    public void logoutPositive() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("username", "password", "email"));
//        serverFacade.logout(new LogoutRequest(registerResult.authToken()));
//        assertTrue(facade.logout());
    }

    @Test
    public void logoutNegative() {
//        assertFalse(facade.logout());
    }

}
