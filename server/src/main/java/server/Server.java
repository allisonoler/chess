package server;

import dataaccess.*;
import org.eclipse.jetty.util.log.Log;
import service.*;
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
    ClearService clearService = new ClearService(gameDOA, userDOA, authDOA);

    GameService gameService = new GameService(gameDOA, authDOA);

    public int run(int desiredPort) {

        Spark.port(desiredPort);


        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/session", this::loginHandler);
        Spark.post("/user", this::registerHandler);
        Spark.delete("/db", this::clearHandler);
        Spark.delete("/session", this::logoutHandler);
        Spark.post("/game", this::createHandler);
        Spark.get("/game", this::listHandler);
        Spark.put("/game", this::joinHandler);


        //This line initializes the server and can be removed once you have a functioning endpoint 
//        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object clearHandler(Request req, Response res) {
        clearService.clear();
        res.status(200);
        res.type("application/json");
        return "";
    }

    private Object registerHandler(Request req, Response res) {
        var registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        try {
            RegisterResult registerResult = userService.register(registerRequest);
            res.status(200);
            res.body(new Gson().toJson(registerResult));
        } catch (ForbiddenException e) {
            res.status(403);
            res.body(new Gson().toJson(Map.of("message", String.format("Error: %s", "forbidden"), "success", false)));
        } catch (BadRequestException e) {
            res.status(400);
            res.body(new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false)));
        }
        res.type("application/json");
        return res.body();
    }

    private Object loginHandler(Request req, Response res) {
        var loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        try {
            LoginResult loginResult = userService.login(loginRequest);
            res.status(200);
            res.body(new Gson().toJson(loginResult));
        } catch (UnauthorizedException e) {
            res.status(401);
            res.body(new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false)));
            return res.body();
        } catch (BadRequestException e) {
            res.status(400);
            res.body(new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false)));
        }
        res.type("application/json");
        return res.body();
    }

    private Object logoutHandler(Request req, Response res) {
        LogoutRequest logoutRequest = new LogoutRequest(req.headers("authorization").toString());
        try {
            if (validateAuthToken(logoutRequest.authToken())) {
                res.status(200);
                userService.logout(logoutRequest);
                res.body("");
            } else {
                res.status(401);
                res.body(new Gson().toJson(Map.of("message", String.format("Error: %s", "unauthorized"), "success", false)));
            }
        } catch (DataAccessException e) {
            res.status(500);
            res.body(new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false)));
        }

        res.type("application/json");
        return res.body();
    }

    private Object createHandler(Request req, Response res) {
        var createRequest = new Gson().fromJson(req.body(), CreateRequest.class);
        String authToken = req.headers("authorization").toString();
        try {
            if (validateAuthToken(authToken)){
                res.status(200);
                CreateResult createResult = gameService.create(createRequest);
                return new Gson().toJson(createResult);
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

    private Object listHandler(Request req, Response res) {
        var listRequest = new Gson().fromJson(req.body(), ListRequest.class);
        String authToken = req.headers("authorization").toString();
        try {
            if (validateAuthToken(authToken)){
                res.status(200);
                ListResult listResult = gameService.list(listRequest);
                return new Gson().toJson(listResult);
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

    private Object joinHandler(Request req, Response res) {
        var joinRequest = new Gson().fromJson(req.body(), JoinRequest.class);
        String authToken = req.headers("authorization").toString();
        var joinRequest2 = new JoinRequest(authToken, joinRequest.playerColor(), joinRequest.gameID());
        try {
            if (validateAuthToken(authToken)){
                res.status(200);
                gameService.join(joinRequest2);
                return "";
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
        } catch (BadRequestException e) {
            res.status(400);
            res.body(new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false)));
            return res.body();
        } catch (ForbiddenException e) {
            res.status(403);
            res.body(new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false)));
            return res.body();
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
