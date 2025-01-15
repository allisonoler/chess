package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {
    @Override
    public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+2),possibleMoves);
        PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-2),possibleMoves);
        PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+2),possibleMoves);
        PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-2),possibleMoves);
        PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()-1),possibleMoves);
        PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()+1),possibleMoves);
        PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()+1),possibleMoves);
        PieceMovesCalculator.goodMove(board, myPosition, new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()-1),possibleMoves);
        return possibleMoves;
    }
}
