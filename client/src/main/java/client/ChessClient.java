package client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import requestsresults.*;
import ui.EscapeSequences;
import java.util.Collections;

public class ChessClient {
    private String visitorName = null;
    private String visitorAuthToken = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;

    private ArrayList<GameData> games = null;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
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
            String result = "Created game: " + params[0];
            return result;
        }

        throw new ResponseException(400, "Invalid input");
    }

    public String join(String... params) throws ResponseException {
        if (params.length == 2 && (params[1].equals("WHITE") || params[1].equals("BLACK"))) {
            assertSignedIn();
            int gameNum = 0;
            try {
                gameNum = Integer.parseInt(params[0]);
            } catch (Exception e) {
                throw new ResponseException(400, "Invalid input");
            }
            if (gameNum>games.size()) {
                throw new ResponseException(400, "Invalid game number");
            }
            server.join(new JoinRequest(visitorAuthToken, params[1], games.get(gameNum-1).gameID()));
            return drawBoard(params[1]);
        }
        throw new ResponseException(400, "Invalid input");
    }

    public String observe(String... params) throws ResponseException {
        if (params.length == 1) {
            assertSignedIn();
            return drawBoard("WHITE");
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
        int index = 1;
        this.games = new ArrayList<GameData>();
        for (var game : games.games()) {
            this.games.add(game);
            result.append("Game number: ");
            result.append(index);
            result.append(", Game name: ");
            result.append(game.gameName());
            result.append(", White username: ");
            if (game.whiteUsername() == null) {
                result.append("N/A");
            } else {
                result.append(game.whiteUsername());
            }
            result.append(", Black username: ");
            if (game.blackUsername() == null) {
                result.append("N/A");
            } else {
                result.append(game.blackUsername());
            }
            result.append('\n');
            index++;
        }
        return result.toString();
    }

    public String drawBoard(String playerColor) {
        StringBuilder return_result = new StringBuilder();
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        boolean color_switch = true;
        String column_label = "    a  b  c  d  e  f  g  h    ";
        ArrayList<Integer> rows = new ArrayList<Integer>();
        for (int i = 1; i <= 8; i++) {
            rows.add(i);
        }
        if (playerColor.equals("BLACK")) {
            column_label = new StringBuilder(column_label).reverse().toString();
            Collections.reverse(rows);
            color_switch = false;
        }
        return_result.append(column_label + "\n");
        for (int i: rows) {
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
        return_result.append(column_label + "\n");
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
