package client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import service.requestsresults.*;
import ui.EscapeSequences;

public class ChessClient {
    private String visitorName = null;
    private String visitorAuthToken = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
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
                case "quit" -> "quit";
                case "observe" -> observe(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            return result;
        }

        throw new ResponseException(400, "Invalid input");
    }

    public String create(String... params) throws ResponseException {
        if (params.length == 1) {
            assertSignedIn();
            CreateResult createResult = server.create(new CreateRequest(visitorAuthToken, params[0]));
            String result = "Created game: " + params[0] + " , with ID: " + createResult.gameID();
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

    public String observe(String... params) throws ResponseException {
        if (params.length == 1) {
            assertSignedIn();
            return drawBoard();
        }
        throw new ResponseException(400, "Invalid input");
    }

    public State getState() {
        return state;
    }

    public String list() throws client.ResponseException, URISyntaxException, IOException {
        assertSignedIn();
        var games = server.list(new ListRequest(visitorAuthToken));
        var result = new StringBuilder();
        for (var game : games.games()) {
            result.append("Game name: ");
            result.append(game.gameName());
            result.append(", White username: ");
            result.append(game.whiteUsername());
            result.append(", Black username: ");
            result.append(game.blackUsername());
            result.append('\n');
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
