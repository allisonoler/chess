package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import server.Server;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> connect(userGameCommand.getGameID(),session, userGameCommand.getAuthToken());
            case MAKE_MOVE -> makeMove(userGameCommand.getMove(), userGameCommand.getGameID(),
                    userGameCommand.getAuthToken(), session);
            case LEAVE -> leave(userGameCommand.getGameID(), userGameCommand.getAuthToken());
            case RESIGN -> resign(userGameCommand.getAuthToken(), userGameCommand.getGameID());
        }
    }

    private void resign(String authToken, Integer gameID) throws DataAccessException, IOException {
        String name = getVisitorName(authToken);
        GameData gameData = Server.gameService.getGame(authToken, String.valueOf(gameID));
        ChessGame game = gameData.game();
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        if (!(gameData.blackUsername().equals(name) || gameData.whiteUsername().equals(name)) || game.isResigned()) {
            notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            notification.setErrorMessage("Error: You are observing.");
            connections.sendOne(name, notification);
            return;
        }

        game.setResigned(true);
        Server.gameService.updateGame(authToken, gameData.gameID(), new GameData(gameData.gameID(),
                gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));

        notification.setMessage(name + " resigned.");
        connections.broadcast(null,gameID, notification);
    }

    private void leave(Integer gameID, String authToken) throws DataAccessException, IOException {
        String name = getVisitorName(authToken);
        GameData gameData = Server.gameService.getGame(authToken, String.valueOf(gameID));
        ChessGame game = gameData.game();
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        if (name.equals(gameData.whiteUsername())) {
            Server.gameService.updateGame(authToken, gameData.gameID(), new GameData(gameData.gameID(),
                    null, gameData.blackUsername(), gameData.gameName(), game));
        }
        else if (name.equals(gameData.blackUsername())) {
            Server.gameService.updateGame(authToken, gameData.gameID(), new GameData(gameData.gameID(),
                    gameData.whiteUsername(), null, gameData.gameName(), game));
        }
        notification.setMessage(name + " left the game.");
        connections.broadcast(name,gameID, notification);
        connections.remove(name);
    }

    private String getVisitorName(String authToken) throws DataAccessException {
        return Server.userService.getUser(authToken);
    }


    private void makeMove(ChessMove chessMove, Integer gameID, String authToken, Session session) throws
            IOException, DataAccessException {
        String visitorName = getVisitorName(authToken);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        if (visitorName == null) {
            handleBadAuth(session);
            return;
        }
        GameData gameData = Server.gameService.getGame(authToken, String.valueOf(gameID));
        ChessGame game = gameData.game();
        if (game.isResigned()) {
            notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            notification.setErrorMessage("Error: The game is already over.");
            connections.sendOne(visitorName, notification);
            return;
        }

//        if (gameData.blackUsername() != null && gameData.blackUsername().equals(visitorName) && game.getTeamTurn())

        if (!(gameData.blackUsername() != null && (game.getTeamTurn().equals(ChessGame.TeamColor.BLACK) && gameData.blackUsername().equals(visitorName)) ||
                (gameData.whiteUsername() != null && game.getTeamTurn().equals(ChessGame.TeamColor.WHITE) && gameData.whiteUsername().equals(visitorName)))) {
            if (!(gameData.blackUsername() != null && gameData.blackUsername().equals(visitorName) || gameData.whiteUsername() != null &&
                    gameData.whiteUsername().equals(visitorName))) {
                notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                notification.setErrorMessage("Error: You are observing.");
            }
            else {
                notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                notification.setErrorMessage("Error: It is not your turn.");
            }
            connections.sendOne(visitorName, notification);
            return;
        }
        try {
            String color = "WHITE";
            if (visitorName.equals(gameData.blackUsername())) {
                color = "BLACK";
            }
            game.makeMove(chessMove);
            Server.gameService.updateGame(authToken, gameData.gameID(), new GameData(gameData.gameID(),
                    gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
            notification.setMessage(visitorName + "made the move: " + chessMove.toString());
            connections.broadcast(visitorName, gameID, notification);
            if ((game.isInCheck(ChessGame.TeamColor.WHITE) && color.equals("WHITE")) ||
                    (game.isInCheck(ChessGame.TeamColor.BLACK) && color.equals("BLACK"))) {
                notification= new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                notification.setMessage(color + " is in check!");
                connections.broadcast(null, gameID, notification);
            }
            if ((game.isInCheckmate(ChessGame.TeamColor.WHITE) && color.equals("BLACK")) ||
                    (game.isInCheckmate(ChessGame.TeamColor.BLACK) && color.equals("WHITE"))) {
                notification= new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                notification.setMessage(color + " is in checkmate!");
                connections.broadcast(null, gameID, notification);
            }
            if ((game.isInStalemate(ChessGame.TeamColor.WHITE) && color.equals("BLACK")) ||
                    (game.isInStalemate(ChessGame.TeamColor.BLACK) && color.equals("WHITE"))) {
                notification= new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                notification.setMessage(color + " is in stalemate!");
                connections.broadcast(null, gameID, notification);
            }
            var notification2 = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            notification2.setGame(Server.gameService.getGame(authToken, String.valueOf(gameID)));
            connections.broadcast(null, gameID, notification2);
        } catch (InvalidMoveException e) {
            notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            notification.setErrorMessage("Error: Invalid move, please try again.");
            connections.sendOne(visitorName, notification);
        }
    }

    private void connect( int gameID, Session session, String authToken) throws IOException, DataAccessException {
        String visitorName = getVisitorName(authToken);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        if (visitorName == null) {
            handleBadAuth(session);
            return;
        }
        GameData gameData = Server.gameService.getGame(authToken, String.valueOf(gameID));
        if (gameData == null) {
            notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            notification.setErrorMessage("Error: bad gameID");
            Connection badConnection = new Connection("bad", session);
            badConnection.send(new Gson().toJson(notification));
            return;
        }
        connections.add(visitorName, gameID, session);
        notification.setMessage(visitorName + " joined the game.");
        connections.broadcast(visitorName, gameID, notification);

        var notification2 = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        notification2.setGame(gameData);
        connections.sendOne(visitorName, notification2);
    }

    private void handleBadAuth(Session session) throws IOException {
        var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        notification.setErrorMessage("Error: bad authToken");
        Connection badConnection = new Connection("bad", session);
        badConnection.send(new Gson().toJson(notification));
    }

}
