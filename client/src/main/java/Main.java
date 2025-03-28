import chess.*;
import client.Repl;
import client.ResponseException;

public class Main {
    public static void main(String[] args) throws ResponseException {
        var serverUrl = "http://localhost:8080";
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        new Repl(serverUrl).run();
    }
}