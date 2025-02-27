package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public class QueenMovesCalculator implements PieceMovesCalculator{
    @Override
    public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        RookMovesCalculator rookMovesCalculator = new RookMovesCalculator();
        ArrayList<ChessMove> straightMoves = rookMovesCalculator.pieceMoves(board, myPosition);
        BishopMovesCalculator bishopMovesCalculator = new BishopMovesCalculator();
        ArrayList<ChessMove> diagonalMoves = bishopMovesCalculator.pieceMoves(board, myPosition);
        possibleMoves.addAll(straightMoves);
        possibleMoves.addAll(diagonalMoves);
        return possibleMoves;
    }
}
