package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public class KingMovesCalculator implements PieceMovesCalculator {

    @Override
    public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1),possibleMoves);
        PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1),possibleMoves);
        PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()),possibleMoves);
        PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1),possibleMoves);
        PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1),possibleMoves);
        PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()),possibleMoves);
        PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn()+1),possibleMoves);
        PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn()-1),possibleMoves);
        PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn()),possibleMoves);
        return possibleMoves;
    }
}
