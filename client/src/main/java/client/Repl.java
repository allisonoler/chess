package client;

import java.util.Scanner;
import ui.EscapeSequences;

public class Repl {
    private final ChessClient client;
    private State state;
    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
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



}