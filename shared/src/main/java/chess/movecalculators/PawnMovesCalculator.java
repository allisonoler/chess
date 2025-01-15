package chess.movecalculators;

import chess.*;

import java.util.ArrayList;

public class PawnMovesCalculator implements PieceMovesCalculator{
    @Override
    public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        int direction = 1;
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            direction = -1;
        }
        boolean firstMove = false;
        if (board.getPiece(myPosition).getTeamColor()== ChessGame.TeamColor.WHITE&&myPosition.getRow()==2) {
            firstMove = true;
        } else if (board.getPiece(myPosition).getTeamColor()== ChessGame.TeamColor.BLACK&&myPosition.getRow()==7) {
            firstMove = true;
        }
        ChessPosition diagonal1 = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn()+1);
        ChessPosition diagonal2 = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn()-1);
        if (PieceMovesCalculator.inbounds(diagonal1) && PieceMovesCalculator.isOpponent(board,myPosition, diagonal1)) {
            PieceMovesCalculator.goodMove(board, myPosition, diagonal1, possibleMoves);
        }
        if (PieceMovesCalculator.inbounds(diagonal2)&& PieceMovesCalculator.isOpponent(board,myPosition, diagonal2)) {
            PieceMovesCalculator.goodMove(board, myPosition, diagonal2, possibleMoves);
        }
        ChessPosition inFront1 = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        if (!PieceMovesCalculator.occupiedSquare(board, myPosition, inFront1)) {
            PieceMovesCalculator.goodMove(board, myPosition, inFront1, possibleMoves);
            ChessPosition inFront2 = new ChessPosition(myPosition.getRow() + direction*2, myPosition.getColumn());
            if (firstMove&& PieceMovesCalculator.inbounds(inFront2)&&!PieceMovesCalculator.occupiedSquare(board, myPosition, inFront2)) {
                PieceMovesCalculator.goodMove(board, myPosition, inFront2, possibleMoves);
            }
        }

        ArrayList<ChessMove> possibleMoves2= new ArrayList<ChessMove>();
        for (ChessMove move: possibleMoves) {
            if ((move.getEndPosition().getRow()==8&&direction ==1)||(move.getEndPosition().getRow()==1&&direction ==-1)) {
                possibleMoves2.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.QUEEN));
                possibleMoves2.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.BISHOP));
                possibleMoves2.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.KNIGHT));
                possibleMoves2.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.ROOK));
            } else {
                possibleMoves2.add(move);
            }
        }
        return possibleMoves2;
    }
}
