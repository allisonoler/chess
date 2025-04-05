package client;

import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageHandler serverMessageHandler;


    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serverMessageHandler = serverMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    serverMessageHandler.notify(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

//    public void loadGame() throws ResponseException {
//        try {
//            var action = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }

    public void makeMove(String visitorName, String authtoken, String startString, String endString, Integer gameID) throws ResponseException {
        try {
            char letter1 = startString.charAt(0);
            int endpos1 = (int)(startString.charAt(1)-'0');
            int startpos1 = letter1 - 'a' + 1;
            char letter2 = endString.charAt(0);
            int endpos2 = (int)(endString.charAt(1)-'0');
            int startpos2 = letter2 - 'a' + 1;

            ChessMove chessMove = new ChessMove(new ChessPosition(endpos1, startpos1), new ChessPosition(endpos2, startpos2), null);
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authtoken, Integer.valueOf(gameID));
            userGameCommand.setMove(chessMove);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void connect(String visitorName, String authtoken, String gameID) throws ResponseException {
        try {
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authtoken, Integer.valueOf(gameID));
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

//    public void join(String visitorName, String authtoken, String gameID, String color) throws ResponseException {
//        try {
//            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.JOIN, visitorName, authtoken, Integer.valueOf(gameID), color);
//            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }

//    public void redraw(String visitorName, String authtoken, String gameID, String color) throws ResponseException {
//        try {
//            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.REDRAW, visitorName, authtoken, Integer.valueOf(gameID), color);
//            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }

    public void leave(String visitorName, String authtoken, String gameID, String color) throws ResponseException {
        try {
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authtoken, Integer.valueOf(gameID));
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resign(String visitorName, String authtoken, String gameID) throws ResponseException {
        try {
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authtoken, Integer.valueOf(gameID));
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
//
//    public void leavePetShop(String visitorName) throws ResponseException {
//        try {
//            var action = new Action(Action.Type.EXIT, visitorName);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//            this.session.close();
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }

}