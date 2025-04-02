package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, Integer> gamesAndPlayers = new ConcurrentHashMap<>();

    public void add(String visitorName, int gameID, Session session) {
        var connection = new Connection(visitorName, session);
        connections.put(visitorName, connection);
        gamesAndPlayers.put(visitorName, gameID);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
        gamesAndPlayers.remove(visitorName);
    }

    public void sendOne(String visitorName, ServerMessage serverMessage) throws IOException {
        if(connections.get(visitorName).session.isOpen()) {
            connections.get(visitorName).send(new Gson().toJson(serverMessage));
        }
    }

    public void broadcast(String excludeVisitorName, Integer gameID, ServerMessage serverMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.visitorName.equals(excludeVisitorName) && gamesAndPlayers.get(c.visitorName).equals(gameID)) {
                    c.send(new Gson().toJson(serverMessage));
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.visitorName);
        }
    }
}
