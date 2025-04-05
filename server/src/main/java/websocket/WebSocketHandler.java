package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import model.UserData;
import server.Server;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
//        if (userGameCommand.getGameID() == null || userGameCommand.getAuthToken() == null) {
//            error(userGameCommand.getName());
//        }
        switch (userGameCommand.getCommandType()) {
//            case LOAD_GAME -> enter(action.visitorName(), session);
//            case ERROR -> exit(action.visitorName());
            case CONNECT -> connect(userGameCommand.getGameID(),session, userGameCommand.getAuthToken());
//            case JOIN -> join(userGameCommand.getName(), userGameCommand.getGameID(),  userGameCommand.getColor(),session, userGameCommand.getAuthToken());
            case MAKE_MOVE -> makeMove(userGameCommand.getMove(), userGameCommand.getGameID(), userGameCommand.getAuthToken());
//            case REDRAW -> redraw(userGameCommand.getGameID(), userGameCommand.getAuthToken(), userGameCommand.getName());
            case LEAVE -> leave(userGameCommand.getGameID(), userGameCommand.getAuthToken());
            case RESIGN -> resign(userGameCommand.getAuthToken(), userGameCommand.getGameID());
        }
    }

//    private void error(String name) throws IOException {
//        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
//        notification.setMessage("Error");
//        connections.sendOne(name, notification);
//    }

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
        Server.gameService.updateGame(authToken, gameData.gameID(), new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));

        notification.setMessage(name + " resigned.");
        connections.broadcast(null,gameID, notification);
    }

    private void leave(Integer gameID, String authToken) throws DataAccessException, IOException {
        String name = getVisitorName(authToken);
        GameData gameData = Server.gameService.getGame(authToken, String.valueOf(gameID));
        ChessGame game = gameData.game();
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        if (name.equals(gameData.whiteUsername())) {
            Server.gameService.updateGame(authToken, gameData.gameID(), new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), game));
        }
        else if (name.equals(gameData.blackUsername())) {
            Server.gameService.updateGame(authToken, gameData.gameID(), new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), game));
        }
        notification.setMessage(name + " left the game.");
        connections.broadcast(name,gameID, notification);
        connections.remove(name);
    }

    private String getVisitorName(String authToken) throws DataAccessException {
        return Server.userService.getUser(authToken);
    }


    private void makeMove(ChessMove chessMove, Integer gameID, String authToken) throws IOException, DataAccessException {
        String visitorName = getVisitorName(authToken);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        GameData gameData = Server.gameService.getGame(authToken, String.valueOf(gameID));
        ChessGame game = gameData.game();
        if (game.isResigned()) {
            notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            notification.setErrorMessage("Error: The game is already over.");
            connections.sendOne(visitorName, notification);
            return;
        }
        if (!((game.getTeamTurn().equals(ChessGame.TeamColor.BLACK) && gameData.blackUsername().equals(visitorName)) ||  (game.getTeamTurn().equals(ChessGame.TeamColor.WHITE) && gameData.whiteUsername().equals(visitorName)))) {
            if (!(gameData.blackUsername().equals(visitorName) || gameData.whiteUsername().equals(visitorName))) {
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
            Server.gameService.updateGame(authToken, gameData.gameID(), new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
            notification.setMessage(visitorName + "made the move: " + chessMove.toString());
            connections.broadcast(visitorName, gameID, notification);
            if ((game.isInCheck(ChessGame.TeamColor.WHITE) && color.equals("WHITE")) || (game.isInCheck(ChessGame.TeamColor.BLACK) && color.equals("BLACK"))) {
                notification= new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                notification.setMessage(color + " is in check!");
                connections.broadcast(null, gameID, notification);
            }
            if ((game.isInCheckmate(ChessGame.TeamColor.WHITE) && color.equals("WHITE")) || (game.isInCheckmate(ChessGame.TeamColor.BLACK) && color.equals("BLACK"))) {
                notification= new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                notification.setMessage(color + " is in checkmate!");
                connections.broadcast(null, gameID, notification);
            }
            if ((game.isInStalemate(ChessGame.TeamColor.WHITE) && color.equals("WHITE")) || (game.isInStalemate(ChessGame.TeamColor.BLACK) && color.equals("BLACK"))) {
                notification= new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                notification.setMessage(color + " is in stalemate!");
                connections.broadcast(null, gameID, notification);
            }
            var notification2 = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            notification2.setGame(Server.gameService.getGame(authToken, String.valueOf(gameID)));
//            notification2.setMessage(new Gson().toJson(Server.gameService.getGame(authToken, String.valueOf(gameID))));
            connections.broadcast(null, gameID, notification2);
        } catch (InvalidMoveException e) {
            notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            notification.setErrorMessage("Error: Invalid move, please try again.");
            connections.sendOne(visitorName, notification);
        }
    }

    private void connect( int gameID, Session session, String authToken) throws IOException, DataAccessException {
        String visitorName = getVisitorName(authToken);
        connections.add(visitorName, gameID, session);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(visitorName + " joined the game.");
//        session.getRemote().sendString("we got here!");
        connections.broadcast(visitorName, gameID, notification);
        GameData gameData = Server.gameService.getGame(authToken, String.valueOf(gameID));
        var notification2 = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
//        notification2.setMessage(new Gson().toJson(gameData));
        notification2.setGame(gameData);
        connections.sendOne(visitorName, notification2);
    }

//    private void join(String visitorName, int gameID, String color, Session session, String authToken) throws IOException, DataAccessException {
//        connections.add(visitorName, gameID, session);
//        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
//        notification.setMessage(visitorName + " joined the game as " + color + ".");
////        session.getRemote().sendString("we got here!");
//        connections.broadcast(visitorName, gameID, notification);
//        GameData gameData = Server.gameService.getGame(authToken, String.valueOf(gameID));
//        var notification2 = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
//        notification2.setMessage(new Gson().toJson(gameData));
//        connections.sendOne(visitorName, notification2);
//    }
//
//    private void exit(String visitorName) throws IOException {
//        connections.remove(visitorName);
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
}
