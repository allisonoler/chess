package client;

import java.util.Arrays;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import service.requestsresults.*;
import ui.EscapeSequences;

public class ChessClient {
    private String visitorName = null;
    private String visitorAuthToken = null;
    private final ServerFacade server;
    private final String serverUrl;
//    private final NotificationHandler notificationHandler;
//    private WebSocketFacade ws;
    private State state = State.SIGNEDOUT;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
//        this.notificationHandler = notificationHandler;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "create" -> create(params);
                case "list" -> list();
                case "logout" -> logout();
                case "join" -> join(params);
//                case "adoptall" -> adoptAllPets();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }
//
    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            LoginResult loginResult = server.login(new LoginRequest(params[0], params[1]));
            state = State.SIGNEDIN;
            visitorName = params[0];
//            ws = new WebSocketFacade(serverUrl, notificationHandler);
//            ws.enterPetShop(visitorName);
            visitorAuthToken = loginResult.authToken();
            return String.format("You signed in as %s.", params[0]);
        }
        System.out.println("Hello??");
        throw new ResponseException(400, "Invalid credentials");
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            RegisterResult registerResult = server.register(new RegisterRequest(params[0], params[1], params[2]));
            state = State.SIGNEDIN;
            visitorAuthToken = registerResult.authToken();
            visitorName = params[0];
//            ws = new WebSocketFacade(serverUrl, notificationHandler);
//            ws.enterPetShop(visitorName);
            return String.format("You signed in as %s.", params[0]);
        }

        throw new ResponseException(400, "Invalid input");
    }

    public String logout(String... params) throws ResponseException {
        if (params.length == 0) {
            assertSignedIn();
            server.logout(new LogoutRequest(visitorAuthToken));
            visitorAuthToken = null;
            String result = visitorName + " logged out.";
            state = State.SIGNEDOUT;
            visitorName = null;
//            ws = new WebSocketFacade(serverUrl, notificationHandler);
//            ws.enterPetShop(visitorName);
            return result;
        }

        throw new ResponseException(400, "Invalid input");
    }

    public String create(String... params) throws ResponseException {
        if (params.length == 1) {
            assertSignedIn();
            CreateResult createResult = server.create(new CreateRequest(visitorAuthToken, params[0]));
            String result = "Created game: " + params[0];
//            ws = new WebSocketFacade(serverUrl, notificationHandler);
//            ws.enterPetShop(visitorName);
            return result;
        }

        throw new ResponseException(400, "Invalid input");
    }

    public String join(String... params) throws ResponseException {
        if (params.length == 2) {
            assertSignedIn();
//            server.join(new JoinRequest(visitorAuthToken, params[0], params[1]));
            return drawBoard();
        }
        throw new ResponseException(400, "Invalid input");
    }

//    public String rescuePet(String... params) throws client.ResponseException {
//        assertSignedIn();
//        if (params.length >= 2) {
//            var name = params[0];
//            var type = PetType.valueOf(params[1].toUpperCase());
//            var pet = new Pet(0, name, type);
//            pet = server.addPet(pet);
//            return String.format("You rescued %s. Assigned ID: %d", pet.name(), pet.id());
//        }
//        throw new client.ResponseException(400, "Expected: <name> <CAT|DOG|FROG>");
//    }
//
    public String list() throws client.ResponseException {
        assertSignedIn();
        var games = server.list(new ListRequest(visitorAuthToken));
        var result = new StringBuilder();
        var gson = new Gson();
        for (var game : games.games()) {
            result.append(gson.toJson(game)).append('\n');
        }
        return result.toString();
    }

    public String drawBoard() {
        StringBuilder return_result = new StringBuilder();
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        boolean color_switch = true;
        return_result.append("    h  g  f  e  d  c  b  a  \n");
        for (int i = 1; i<=8;i++) {
            return_result.append(" " + i + " ");
            for (int j = 1; j<=8; j++) {
                if (color_switch) {
                    return_result.append(EscapeSequences.SET_BG_COLOR_WHITE);
                    color_switch = false;
                } else {
                    return_result.append(EscapeSequences.SET_BG_COLOR_MAGENTA);
                    color_switch = true;
                }
                if (board.getPiece(new ChessPosition(i,j)) == null) {
                    return_result.append("   ");
                } else if (board.getPiece(new ChessPosition(i, j)).getPieceType().equals(ChessPiece.PieceType.ROOK)) {
                    return_result.append(" R ");
                } else if (board.getPiece(new ChessPosition(i, j)).getPieceType().equals(ChessPiece.PieceType.KNIGHT)) {
                    return_result.append(" N ");
                } else if (board.getPiece(new ChessPosition(i, j)).getPieceType().equals(ChessPiece.PieceType.BISHOP)) {
                    return_result.append(" B ");
                } else if (board.getPiece(new ChessPosition(i, j)).getPieceType().equals(ChessPiece.PieceType.QUEEN)) {
                    return_result.append(" Q ");
                } else if (board.getPiece(new ChessPosition(i, j)).getPieceType().equals(ChessPiece.PieceType.KING)) {
                    return_result.append(" K ");
                } else if (board.getPiece(new ChessPosition(i, j)).getPieceType().equals(ChessPiece.PieceType.PAWN)) {
                    return_result.append(" P ");
                }
            }
            color_switch = !color_switch;
            return_result.append(EscapeSequences.RESET_BG_COLOR);
            return_result.append(" " + i + " ");
            return_result.append("\n");
        }
        return_result.append("    h  g  f  e  d  c  b  a  ");

        return return_result.toString();
    }
//
//    public String adoptPet(String... params) throws client.ResponseException {
//        assertSignedIn();
//        if (params.length == 1) {
//            try {
//                var id = Integer.parseInt(params[0]);
//                var pet = getPet(id);
//                if (pet != null) {
//                    server.deletePet(id);
//                    return String.format("%s says %s", pet.name(), pet.sound());
//                }
//            } catch (NumberFormatException ignored) {
//            }
//        }
//        throw new client.ResponseException(400, "Expected: <pet id>");
//    }
//
//    public String adoptAllPets() throws client.ResponseException {
//        assertSignedIn();
//        var buffer = new StringBuilder();
//        for (var pet : server.listPets()) {
//            buffer.append(String.format("%s says %s%n", pet.name(), pet.sound()));
//        }
//
//        server.deleteAllPets();
//        return buffer.toString();
//    }
//
//    public String signOut() throws client.ResponseException {
//        assertSignedIn();
//        ws.leavePetShop(visitorName);
//        ws = null;
//        state = client.State.SIGNEDOUT;
//        return String.format("%s left the shop", visitorName);
//    }
//
//    private Pet getPet(int id) throws client.ResponseException {
//        for (var pet : server.listPets()) {
//            if (pet.id() == id) {
//                return pet;
//            }
//        }
//        return null;
//    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    register <username> <password> <email> - to create an account
                    login <username> <password> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
        }
        return """
                create <NAME> - a game
                list - games
                join <ID> WHITE|BLACK - a game
                observe <ID> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}
