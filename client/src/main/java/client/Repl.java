package client;

import java.util.Scanner;

import chess.ChessBoard;
import com.google.gson.Gson;
import model.GameData;
import ui.EscapeSequences;
import websocket.messages.ServerMessage;

public class Repl implements ServerMessageHandler{
    private final ChessClient client;
    private State state;
    public Repl(String serverUrl) throws ResponseException {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to chess! Sign in or register to start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }


    private void printPrompt() {
        String stateString = "";
        if (client.getState().equals(State.SIGNEDOUT)) {
            stateString = "[SIGNED OUT]";
        } else if (client.getState().equals(State.SIGNEDIN)) {
            stateString = "[SIGNED IN]";
        }
        System.out.print("\n" + stateString + ">>> ");
    }


    @Override
    public void notify(ServerMessage serverMessage) {
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> loadGame(serverMessage);
            case ERROR -> System.out.println(serverMessage.getErrorMessage());
            case NOTIFICATION -> System.out.println(serverMessage.getMessage());
        }
        printPrompt();
    }

    private void loadGame(ServerMessage serverMessage) {
//        GameData game = new Gson().fromJson(serverMessage.getMessage(), GameData.class);
        GameData game = serverMessage.getGame();
        System.out.println();
        String playerColor = "WHITE";
        if (game.blackUsername() != null && game.blackUsername().equals(client.getVisitorName())) {
            playerColor = "BLACK";
        }
        System.out.println(ChessClient.drawBoard(playerColor, game.game().getBoard()));
    }
}

