package server;

import dataaccess.*;
import requestsresults.*;
import service.*;
import spark.*;
import com.google.gson.Gson;
import websocket.WebSocketHandler;

import java.util.Map;

public class Server {
    GameDOA gameDOA;
    UserDOA userDOA;
    AuthDOA authDOA;
    public static UserService userService;
   ClearService clearService;

    public static GameService gameService;

    private final WebSocketHandler webSocketHandler;

    public Server() {
        webSocketHandler = new WebSocketHandler();
        try {
            this.gameDOA = new SqlGameDOA();
            this.userDOA = new SqlUserDOA();
            this.authDOA = new SqlAuthDOA();
            this.userService = new UserService(userDOA, authDOA);
            this.clearService = new ClearService(gameDOA, userDOA, authDOA);

            this.gameService = new GameService(gameDOA, authDOA);

        } catch (DataAccessException e) {
            System.out.printf("Unable to start server: %s%n", e.getMessage());
        }


    }

    public int run(int desiredPort) {

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", webSocketHandler);


        // Register your endpoints and handle exceptions here.
        Spark.post("/session", this::loginHandler);
        Spark.post("/user", this::registerHandler);
        Spark.delete("/db", this::clearHandler);
        Spark.delete("/session", this::logoutHandler);
        Spark.post("/game", this::createHandler);
        Spark.get("/game", this::listHandler);
        Spark.put("/game", this::joinHandler);

        Spark.exception(UnauthorizedException.class, this::unauthorizedExceptionHandler);
        Spark.exception(ForbiddenException.class, this::forbiddenExceptionHandler);
        Spark.exception(DataAccessException.class, this::dataAccessExceptionHandler);
        Spark.exception(BadRequestException.class, this::badRequestExceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object clearHandler(Request req, Response res) throws DataAccessException {
        clearService.clear();
        res.status(200);
        res.type("application/json");
        return "";
    }

    private Object unauthorizedExceptionHandler(UnauthorizedException e, Request req, Response res) {
        res.status(401);
        res.body(new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false)));
        return res.body();
    }

    private Object forbiddenExceptionHandler(ForbiddenException e, Request req, Response res) {
        res.status(403);
        res.body(new Gson().toJson(Map.of("message", String.format("Error: %s", "forbidden"), "success", false)));
        return res.body();
    }

    private Object badRequestExceptionHandler(BadRequestException e, Request req, Response res) {
        res.status(400);
        res.body(new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false)));
        return res.body();
    }

    private Object dataAccessExceptionHandler(DataAccessException e, Request req, Response res) {
        res.status(500);
        res.body(new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false)));
        return res.body();
    }

    private Object registerHandler(Request req, Response res) throws ForbiddenException, BadRequestException {
        var registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterResult registerResult = userService.register(registerRequest);
        res.status(200);
        res.body(new Gson().toJson(registerResult));
        res.type("application/json");
        return res.body();
    }

    private Object loginHandler(Request req, Response res) throws UnauthorizedException, BadRequestException {
        var loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResult loginResult = userService.login(loginRequest);
        res.status(200);
        res.body(new Gson().toJson(loginResult));
        res.type("application/json");
        return res.body();
    }

    private Object logoutHandler(Request req, Response res) throws DataAccessException, UnauthorizedException {
        LogoutRequest logoutRequest = new LogoutRequest(req.headers("authorization").toString());
        validateAuthToken(logoutRequest.authToken());
        res.status(200);
        userService.logout(logoutRequest);
        res.body("");
        res.type("application/json");
        return res.body();
    }

    private Object createHandler(Request req, Response res) throws UnauthorizedException, DataAccessException {
        var createRequest = new Gson().fromJson(req.body(), CreateRequest.class);
        String authToken = req.headers("authorization").toString();
        validateAuthToken(authToken);
        res.status(200);
        CreateResult createResult = gameService.create(createRequest);
        return new Gson().toJson(createResult);

    }

    private Object listHandler(Request req, Response res) throws DataAccessException, UnauthorizedException {
        var listRequest = new Gson().fromJson(req.body(), ListRequest.class);
        String authToken = req.headers("authorization").toString();
        validateAuthToken(authToken);
        res.status(200);
        ListResult listResult = gameService.list(listRequest);
        return new Gson().toJson(listResult);
    }

    private Object joinHandler(Request req, Response res) throws DataAccessException, ForbiddenException, UnauthorizedException, BadRequestException {
        var joinRequest = new Gson().fromJson(req.body(), JoinRequest.class);
        String authToken = req.headers("authorization").toString();
        var joinRequest2 = new JoinRequest(authToken, joinRequest.playerColor(), joinRequest.gameID());
        validateAuthToken(authToken);
        res.status(200);
        gameService.join(joinRequest2);
        return "";
    }

    private boolean validateAuthToken(String authToken) throws DataAccessException, UnauthorizedException {
        if (authDOA.getAuth(authToken)!=null) {
            return true;
        }
        else {
            throw new UnauthorizedException("Not authorized");
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
