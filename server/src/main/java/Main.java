import chess.*;
import dataaccess.*;
import server.Server;
import service.GameService;
import service.UserService;
import service.ClearService;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        try {

            Server server = new Server();
            server.run(8080);
        } catch (Throwable ex) {
        System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}