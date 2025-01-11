package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor teamColor;
    ChessPiece.PieceType pieceType;
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

    private Collection<ChessMove> bishopPieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        for (int i = myPosition.getRow() + 1, j = myPosition.getColumn() + 1; i<=8 && j<=8; i++, j++) {
            if (goodMove(board, myPosition, possibleMoves, i, j)) {
                break;
            }
        }
        for (int i = myPosition.getRow() - 1, j = myPosition.getColumn() - 1; i>=1 && j>=1; i--, j--) {
            if (goodMove(board, myPosition, possibleMoves, i, j)) {
                break;
            }
        }
        for (int i = myPosition.getRow() - 1, j = myPosition.getColumn() + 1; i>=1 && j<=8; i--, j++) {
            if (goodMove(board, myPosition, possibleMoves, i, j)) {
                break;
            }
        }
        for (int i = myPosition.getRow() + 1, j = myPosition.getColumn() - 1; i<=8 && j>=1; i++, j--) {
            if (goodMove(board, myPosition, possibleMoves, i, j)) {
                break;
            }
        }
        return possibleMoves;
    }

    private boolean goodMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves, int i, int j) {
        ChessPosition possiblePosition = new ChessPosition(i, j);
        if (board.getPiece(possiblePosition) != null) {
            if (board.getPiece(possiblePosition).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                return true;
            } else {
                possibleMoves.add(new ChessMove(myPosition, possiblePosition, null));
                return true;
            }
        }
        possibleMoves.add(new ChessMove(myPosition, possiblePosition, null));
        return false;
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
            }
            case BISHOP -> {
                possibleMoves = bishopPieceMoves(board, myPosition);
            }
        }
        return possibleMoves;
    }
}
