package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public interface PieceMovesCalculator {

    static boolean isOpponent(ChessBoard board, ChessPosition myPosition, ChessPosition finalPosition) {
        return board.getPiece(finalPosition) != null && (board.getPiece(myPosition).getTeamColor() != board.getPiece(finalPosition).getTeamColor());
    }

    static boolean inbounds(ChessPosition position) {
        return (position.getColumn()<9&& position.getColumn()>0&&position.getRow()<9&&position.getRow()>0);
    }
    static boolean sameTeam(ChessBoard board, ChessPosition myPosition, ChessPosition finalPosition) {
        return board.getPiece(finalPosition) != null && (board.getPiece(myPosition).getTeamColor() == board.getPiece(finalPosition).getTeamColor());
    }

    static  boolean occupiedSquare(ChessBoard board, ChessPosition myPosition, ChessPosition finalPosition) {
        return board.getPiece(finalPosition) != null;
    }

    static boolean goodMove(ChessBoard board, ChessPosition myPosition, ChessPosition finalPosition, ArrayList<ChessMove> possibleMoves) {
        if(! inbounds(finalPosition)) {
            return false;
        }
        if (sameTeam(board,myPosition,finalPosition)) {
            return false;
        } else if (isOpponent(board, myPosition, finalPosition)) {
            possibleMoves.add(new ChessMove(myPosition,finalPosition,null));
            return false;
        } else {
            possibleMoves.add(new ChessMove(myPosition,finalPosition,null));
            return true;
        }

    }
    public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);
}
