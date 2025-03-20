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
                System.out.print(e.getMessage());
            }
        }
        System.out.println();
    }

//    public void notify(Notification notification) {
//        System.out.println(RED + notification.message());
//        printPrompt();
//    }

    private void printPrompt() {
        String state_string = "";
        if (client.getState().equals(State.SIGNEDOUT)) {
            state_string = "[SIGNED OUT]";
        } else if (client.getState().equals(State.SIGNEDIN)) {
            state_string = "[SIGNED IN]";
        }
        System.out.print("\n" + state_string + ">>> ");
//        System.out.print("\n"  + ">>> " );
    }



}