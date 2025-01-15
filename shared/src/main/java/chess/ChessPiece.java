package chess;

import chess.moveCalculators.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor teamColor;
     private ChessPiece.PieceType pieceType;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        teamColor = pieceColor;
        pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.teamColor;

    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.pieceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        switch (this.pieceType) {
            case KING -> {
                possibleMoves = new KingMovesCalculator().pieceMoves(board, myPosition);
            }
            case BISHOP -> {
                possibleMoves = new BishopMovesCalculator().pieceMoves(board, myPosition);
            }
            case KNIGHT -> {
                possibleMoves = new KnightMovesCalculator().pieceMoves(board, myPosition);
            }
            case PAWN -> {
                possibleMoves = new PawnMovesCalculator().pieceMoves(board, myPosition);
            }
            case QUEEN -> {
                possibleMoves = new QueenMovesCalculator().pieceMoves(board, myPosition);;
            }
            case ROOK -> {
                possibleMoves = new RookMovesCalculator().pieceMoves(board, myPosition);
            }
        }
        return possibleMoves;
    }
}
