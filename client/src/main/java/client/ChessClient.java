package client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import chess.*;
import model.GameData;
import requestsresults.*;
import ui.EscapeSequences;

import java.util.Collection;
import java.util.Collections;

public class ChessClient {
    private String visitorName = null;
    private String visitorAuthToken = null;
    private final ServerFacade server;

    private WebSocketFacade ws;

    private final ServerMessageHandler serverMessageHandler;
    private State state = State.SIGNEDOUT;

    private ArrayList<GameData> games = null;

    private int gameID = 0;

    private GameData currGame = null;

    public ChessClient(String serverUrl, ServerMessageHandler serverMessageHandler) throws ResponseException {
        server = new ServerFacade(serverUrl);
        this.serverMessageHandler = serverMessageHandler;
        ws = new WebSocketFacade(serverUrl, serverMessageHandler);
    }

    public void setCurrGame(GameData game) {
        this.currGame = game;
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
                case "leave" -> leave();
                case "redraw" -> redraw();
                case "move" -> makeMove(params);
                case "resign" -> resign();
                case "highlight" -> highlight(params);
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
    public String highlight(String... params) throws ResponseException {
        if (params.length == 1 && validateMoveInput(params[0])) {
            assertGameplay();
            char letter1 = params[0].charAt(0);
            int endpos1 = (int)(params[0].charAt(1)-'0');
            int startpos1 = letter1 - 'a' + 1;
            var moves = currGame.game().validMoves(new ChessPosition(endpos1, startpos1));
            String playerColor = "WHITE";
            if (currGame.blackUsername() != null && currGame.blackUsername().equals(visitorName)) {
                playerColor = "BLACK";
            }
            return drawBoard(playerColor, currGame.game().getBoard(), moves);
        }
        throw new ResponseException(400, "Invalid input");
    }

    public String resign() throws ResponseException {
        assertGameplay();
        ws.resign(this.visitorName, this.visitorAuthToken, String.valueOf(gameID));
        return "";
    }
    public String redraw() throws ResponseException {
        assertGameplay();
        return redrawBoard();
    }
    public String leave() throws ResponseException {
        assertGameplay();
        state = State.SIGNEDIN;
        ws.leave(visitorName, visitorAuthToken, String.valueOf(gameID), null);
        currGame = null;
        gameID=0;
        return "You left the game.";
    }
    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            LoginResult loginResult = server.login(new LoginRequest(params[0], params[1]));
            state = State.SIGNEDIN;
            visitorName = params[0];
            visitorAuthToken = loginResult.authToken();
            return String.format("You signed in as %s.", params[0]);
        }
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

    private boolean validateMoveInput(String input) throws ResponseException {
        try {
            String letter = input.substring(0, 1);
            int digit = Integer.parseInt(input.substring(1));
            if ((letter.equals("a") || letter.equals("b") || letter.equals("c") || letter.equals("d") || letter.equals("e")
                    || letter.equals("f") || letter.equals("g") || letter.equals("h")) && digit > 0 && digit < 9) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new ResponseException(400, "Invalid input");
        }
    }

    public String makeMove(String... params) throws ResponseException {
        if ((params.length == 2 || params.length == 3) && validateMoveInput(params[0]) && validateMoveInput(params[1])) {
            assertGameplay();
            String pieceType = null;
            if (3 == params.length) {
                if (params[2].equals("QUEEN") || params[2].equals("BISHOP") || params[2].equals("KNIGHT") || params[2].equals("ROOK")) {
                    pieceType = params[2];
                }
            }
            ws.makeMove(this.visitorName, this.visitorAuthToken, params[0], params[1], pieceType,gameID);
            return "";
        }
        throw new ResponseException(400, "Invalid input");
    }

    public String join(String... params) throws ResponseException {
        if (params.length == 2 && (params[1].equals("WHITE") || params[1].equals("BLACK"))) {
            assertSignedIn();
            state = State.GAMEPLAY;
            currGame = games.get(getGameNum(params[0])-1);
            gameID = Integer.valueOf(games.get(getGameNum(params[0])-1).gameID());
            int gameNum = getGameNum(params[0]);
            server.join(new JoinRequest(visitorAuthToken, params[1], String.valueOf(gameID)));
            ws.connect(this.visitorName, this.visitorAuthToken, String.valueOf(gameID));
            return "";
        }
        throw new ResponseException(400, "Invalid input");
    }

    public String observe(String... params) throws ResponseException {
        if (params.length == 1) {
            assertSignedIn();
            currGame = games.get(getGameNum(params[0])-1);
            gameID = Integer.valueOf(games.get(getGameNum(params[0])-1).gameID());
            state = State.GAMEPLAY;
            ws.connect(this.visitorName, this.visitorAuthToken, String.valueOf(gameID));
            return "";
        }
        throw new ResponseException(400, "Invalid input");
    }

    private int getGameNum(String param) throws ResponseException {
        int gameNum = 0;
        try {
            gameNum = Integer.parseInt(param);
        } catch (Exception e) {
            throw new ResponseException(400, "Invalid input");
        }
        if (gameNum>games.size()) {
            throw new ResponseException(400, "Invalid game number");
        }
        return gameNum;
    }

    public State getState() {
        return state;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public String list() throws client.ResponseException, URISyntaxException, IOException {
        assertSignedIn();
        var games = server.list(new ListRequest(visitorAuthToken));
        var result = new StringBuilder();
        int index = 1;
        this.games = new ArrayList<GameData>();
        for (GameData game : games.games()) {
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

    private String redrawBoard() {
        String playerColor = "WHITE";
        if (currGame.blackUsername() != null && currGame.blackUsername().equals(visitorName)) {
            playerColor = "BLACK";
        }
        return drawBoard(playerColor, currGame.game().getBoard(), null);
    }

    public static String drawBoard(String playerColor, ChessBoard board, Collection<ChessMove> possibleMoves) {
        StringBuilder returnResult = new StringBuilder();
        returnResult.append(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
        boolean colorSwitch = true;
        String columnLabel = "    a  b  c  d  e  f  g  h    ";
        ArrayList<Integer> rows = new ArrayList<Integer>();
        ArrayList<Integer> cols = new ArrayList<Integer>();
        int[][] possibleSquares = new int[9][9];
        if (possibleMoves!= null) {
            for (ChessMove move : possibleMoves) {
                ChessPosition endpos = move.getEndPosition();
                ChessPosition startpos = move.getStartPosition();
                possibleSquares[endpos.getRow()][endpos.getColumn()] = 1;
                possibleSquares[startpos.getRow()][startpos.getColumn()] = 2;
            }
        }
        for (int i = 1; i <= 8; i++) {
            rows.add(i);
            cols.add(i);
        }
        if (playerColor.equals("BLACK")) {
            columnLabel = new StringBuilder(columnLabel).reverse().toString();
//            colorSwitch = false;
            Collections.reverse(cols);
        } else {
            Collections.reverse(rows);
        }
        returnResult.append(columnLabel + "\n");
        for (int i: rows) {
            returnResult.append(" " + i + " ");
            for (int j: cols) {
                if (colorSwitch) {
                    if (possibleSquares[i][j] == 1) {
                        returnResult.append(EscapeSequences.SET_BG_COLOR_GREEN);
                    } else if (possibleSquares[i][j] == 2) {
                        returnResult.append(EscapeSequences.SET_BG_COLOR_YELLOW);
                    }
                    else {
                        returnResult.append(EscapeSequences.SET_BG_COLOR_WHITE);
                    }
                    colorSwitch = false;
                } else {
                    if (possibleSquares[i][j] == 1) {
                        returnResult.append(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                    } else if (possibleSquares[i][j] == 2) {
                        returnResult.append(EscapeSequences.SET_BG_COLOR_YELLOW);
                    }
                    else {
                        returnResult.append(EscapeSequences.SET_BG_COLOR_MAGENTA);
                    }
                    colorSwitch = true;
                }
                if (board.getPiece(new ChessPosition(i,j)) == null) {
                    returnResult.append("   ");
                } else {
                    if (board.getPiece(new ChessPosition(i, j)).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                        returnResult.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
                    } else {
                        returnResult.append(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                    }
                    if (board.getPiece(new ChessPosition(i, j)).getPieceType().equals(ChessPiece.PieceType.ROOK)) {
                        returnResult.append(" R ");
                    } else if (board.getPiece(new ChessPosition(i, j)).getPieceType().equals(ChessPiece.PieceType.KNIGHT)) {
                        returnResult.append(" N ");
                    } else if (board.getPiece(new ChessPosition(i, j)).getPieceType().equals(ChessPiece.PieceType.BISHOP)) {
                        returnResult.append(" B ");
                    } else if (board.getPiece(new ChessPosition(i, j)).getPieceType().equals(ChessPiece.PieceType.QUEEN)) {
                        returnResult.append(" Q ");
                    } else if (board.getPiece(new ChessPosition(i, j)).getPieceType().equals(ChessPiece.PieceType.KING)) {
                        returnResult.append(" K ");
                    } else if (board.getPiece(new ChessPosition(i, j)).getPieceType().equals(ChessPiece.PieceType.PAWN)) {
                        returnResult.append(" P ");
                    }
                    returnResult.append(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                }
            }
            colorSwitch = !colorSwitch;
            returnResult.append(EscapeSequences.RESET_BG_COLOR);
            returnResult.append(" " + i + " ");
            returnResult.append("\n");
        }
        returnResult.append(columnLabel + "\n");
        return returnResult.toString();
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    register <username> <password> <email> - to create an account
                    login <username> <password> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
        } else if (state == State.SIGNEDIN) {
            return """
                    create <NAME> - a game
                    list - games
                    join <ID> WHITE|BLACK - a game
                    observe <ID> - a game
                    logout - when you are done
                    quit - playing chess
                    help - with possible commands
                    """;
        } else {
            return """
                    redraw - the chessboard
                    leave - the game
                    move <POSITION> <POSITION> <PROMOTION NAME> (ex: a4 f8, only put promotion if applicable!) - make move
                    resign - give up in game
                    leave - leave game
                    highlight <POSITION> (ex: a4) - highlights all valid squares for selected piece
                    help - with possible commands
                    """;
        }
    }

    private void assertSignedIn() throws ResponseException {
        if (state != State.SIGNEDIN) {
            throw new ResponseException(400, "You must sign in");
        }
    }

    private void assertGameplay() throws ResponseException {
        if (state != State.GAMEPLAY) {
            throw new ResponseException(400, "You are not currently playing a game");
        }
    }
}
