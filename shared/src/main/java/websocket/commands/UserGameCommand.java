package websocket.commands;

import chess.ChessMove;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 *
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    private final CommandType commandType;

    private final String authToken;

    private final Integer gameID;

    private final String color;

    private ChessMove move;

    private final String visitorName;

    public UserGameCommand(CommandType commandType, String visitorName, String authToken, Integer gameID, String color) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.color = color;
        this.move = null;
        this.visitorName = visitorName;
    }

    public enum CommandType {
        CONNECT,
        JOIN,
        MAKE_MOVE,
        LEAVE,
        RESIGN,

        REDRAW
    }

    public void setMove(ChessMove move) {
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }
    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getName() {
        return visitorName;
    }

    public Integer getGameID() {
        return gameID;
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand)) {
            return false;
        }
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID());
    }
}
