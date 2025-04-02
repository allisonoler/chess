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
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
//            case LOAD_GAME -> enter(action.visitorName(), session);
//            case ERROR -> exit(action.visitorName());
            case CONNECT -> connect(userGameCommand.getName(), userGameCommand.getGameID(),session);
            case JOIN -> join(userGameCommand.getName(), userGameCommand.getGameID(),  userGameCommand.getColor(),session);
            case MAKE_MOVE -> makeMove(userGameCommand.getMove(), userGameCommand.getName(), userGameCommand.getColor(), userGameCommand.getGameID(), userGameCommand.getAuthToken());
        }
    }

    private void makeMove(ChessMove chessMove,String visitorName, String color, Integer gameID, String authToken) throws IOException, DataAccessException {
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        GameData gameData = Server.gameService.getGame(authToken, String.valueOf(gameID));
        ChessGame game = gameData.game();
        try {
            game.makeMove(chessMove);
            notification.setMessage(visitorName + "made the move: " + chessMove.toString());
            connections.broadcast(null, gameID, notification);
            var notification2 = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            notification2.setMessage(new Gson().toJson(game.getBoard()));
            connections.broadcast(null, gameID, notification2);
        } catch (InvalidMoveException e) {
            notification.setMessage("Invalid move, please try again.");
            connections.sendOne(visitorName, notification);
        }
    }

    private void connect(String visitorName, int gameID, Session session) throws IOException {
        connections.add(visitorName, gameID, session);
        var message = String.format("%s is in the shop", visitorName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(visitorName + " joined the game.");
//        session.getRemote().sendString("we got here!");
        connections.broadcast(visitorName, gameID, notification);
    }

    private void join(String visitorName, int gameID, String color, Session session) throws IOException {
        connections.add(visitorName, gameID, session);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(visitorName + " joined the game as " + color + ".");
//        session.getRemote().sendString("we got here!");
        connections.broadcast(visitorName, gameID, notification);
    }
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
