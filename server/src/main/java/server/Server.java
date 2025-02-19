package server;

import dataaccess.*;
import service.ClearService;
import service.UserService;
import spark.*;
import com.google.gson.Gson;
//import excepti/*/on.ResponseException;

public class Server {
    GameDOA gameDOA = new MemoryGameDOA();
    UserDOA userDOA = new MemoryUserDAO();
    AuthDOA authDOA = new MemoryAuthDOA();
    UserService userService = new UserService(userDOA, authDOA);
    ClearService clearService = new ClearService(gameDOA, userDOA);

    public int run(int desiredPort) {

        Spark.port(desiredPort);


        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
//        Spark.get("/login", (request, response) ->"bet you wish you could login");
        Spark.post("/session", this::clearHandler);
        Spark.delete("/db", this::clearHandler);

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
        return new Gson().toJson("");
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
