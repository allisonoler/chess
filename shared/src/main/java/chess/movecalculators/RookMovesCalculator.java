package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public class RookMovesCalculator implements PieceMovesCalculator {

    @Override
    public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        for (int j = myPosition.getColumn()-1; j>0;j--) {
            if (!PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(myPosition.getRow(), j), possibleMoves)) {
                break;
            }
        }
        for (int j = myPosition.getColumn()+1; j<9;j++) {
            if (!PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(myPosition.getRow(), j), possibleMoves)) {
                break;
            }
        }
        for (int i = myPosition.getRow()-1; i>0;i--) {
            if (!PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(i, myPosition.getColumn()), possibleMoves)) {
                break;
            }
        }
        for (int i = myPosition.getRow()+1; i<9;i++) {
            if (!PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(i, myPosition.getColumn()), possibleMoves)) {
                break;
            }
        }
        return possibleMoves;
    }
}
