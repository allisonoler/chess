package server;

import dataaccess.*;
import org.eclipse.jetty.util.log.Log;
import service.ClearService;
import service.GameService;
import service.UnauthorizedException;
import service.UserService;
import service.requestsresults.*;
import spark.*;
import com.google.gson.Gson;

import java.util.Map;
//import excepti/*/on.ResponseException;

public class Server {
    GameDOA gameDOA = new MemoryGameDOA();
    UserDOA userDOA = new MemoryUserDAO();
    AuthDOA authDOA = new MemoryAuthDOA();
    UserService userService = new UserService(userDOA, authDOA);
    ClearService clearService = new ClearService(gameDOA, userDOA);

    GameService gameService = new GameService(gameDOA, authDOA);

    public int run(int desiredPort) {

        Spark.port(desiredPort);


        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
//        Spark.get("/login", (request, response) ->"bet you wish you could login");
        Spark.post("/session", this::loginHandler);
        Spark.post("/user", this::registerHandler);
        Spark.delete("/db", this::clearHandler);
        Spark.delete("/session", this::logoutHandler);
        Spark.post("/game", this::createHandler);

//        Spark.get("/pet", this::listPets);
//        Spark.post("/session", (request, response) -> "weiiiiii");

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object clearHandler(Request req, Response res) {
//        var clearRequest = new Gson().fromJson(req.body(), Clear.class);
//        pet = service.addPet(pet);
//        webSocketHandler.makeNoise(pet.name(), pet.sound());
//        return new Gson().toJson(pet);
        clearService.clear();
        res.status(200);
        res.type("application/json");
//        return new Gson().toJson("hi");
        return "";
    }

    private Object registerHandler(Request req, Response res) {
        var registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
//        pet = service.addPet(pet);
//        webSocketHandler.makeNoise(pet.name(), pet.sound());
//        return new Gson().toJson(pet);
        RegisterResult registerResult = userService.register(registerRequest);
        res.status(200);
        res.type("application/json");
//        return new Gson().toJson("hi");
        return new Gson().toJson(registerResult);
    }

    private Object loginHandler(Request req, Response res) {
        var loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
//        pet = service.addPet(pet);
//        webSocketHandler.makeNoise(pet.name(), pet.sound());
//        return new Gson().toJson(pet);
        try {
            LoginResult loginResult = userService.login(loginRequest);
            res.status(200);
            res.type("application/json");
//        return new Gson().toJson("hi");
            return new Gson().toJson(loginResult);
        } catch (UnauthorizedException e) {
            res.status(401);
            res.body(new Gson().toJson(Map.of("message", String.format("Error: %s", "unauthorized"), "success", false)));
            res.type("application/json");
            return res.body();

        }

    }

    private Object logoutHandler(Request req, Response res) {
        LogoutRequest logoutRequest = new LogoutRequest(req.headers("authorization").toString());
        try {
            if (validateAuthToken(logoutRequest.authToken())) {
                res.status(200);
                userService.logout(logoutRequest);
            } else {
                res.status(401);
                res.body(new Gson().toJson(Map.of("message", String.format("Error: %s", "unauthorized"), "success", false)));
                res.type("application/json");
                return res.body();
            }
        } catch (DataAccessException e) {
            res.status(500);
        }
//        pet = service.addPet(pet);
//        webSocketHandler.makeNoise(pet.name(), pet.sound());
//        return new Gson().toJson(pet);
        res.type("application/json");
//        return new Gson().toJson("hi");
        return "";
    }

    private Object createHandler(Request req, Response res) {
        var createRequest = new Gson().fromJson(req.body(), CreateRequest.class);
        try {
            if (validateAuthToken(createRequest.authToken())) {
                res.status(200);
                gameService.create(createRequest);
            } else {
                res.status(401);
                res.body(new Gson().toJson(Map.of("message", String.format("Error: %s", "unauthorized"), "success", false)));
                res.type("application/json");
                return res.body();
            }
        } catch (DataAccessException e) {
            res.status(500);
        } catch (UnauthorizedException e) {
            throw new RuntimeException(e);
        }
//        pet = service.addPet(pet);
//        webSocketHandler.makeNoise(pet.name(), pet.sound());
//        return new Gson().toJson(pet);
        res.type("application/json");
//        return new Gson().toJson("hi");
        return "";
    }

    private boolean validateAuthToken(String authToken) throws DataAccessException {
        if (authDOA.getAuth(authToken)!=null) {
            return true;
        }
        else {
            return false;
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
