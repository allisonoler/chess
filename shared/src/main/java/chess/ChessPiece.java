package chess;

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

    private Collection<ChessMove> kingPieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        goodMove(board, myPosition, possibleMoves, myPosition.getRow()+1, myPosition.getColumn());
        goodMove(board, myPosition, possibleMoves, myPosition.getRow()+1, myPosition.getColumn() + 1);
        goodMove(board, myPosition, possibleMoves, myPosition.getRow()+1, myPosition.getColumn() -1);
        goodMove(board, myPosition, possibleMoves, myPosition.getRow()-1, myPosition.getColumn());
        goodMove(board, myPosition, possibleMoves, myPosition.getRow()-1, myPosition.getColumn() + 1);
        goodMove(board, myPosition, possibleMoves, myPosition.getRow()-1, myPosition.getColumn() - 1);
        goodMove(board, myPosition, possibleMoves, myPosition.getRow(), myPosition.getColumn() - 1);
        goodMove(board, myPosition, possibleMoves, myPosition.getRow(), myPosition.getColumn() + 1);
        return possibleMoves;
    }

    private Collection<ChessMove> knightPieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        goodMove(board, myPosition, possibleMoves, myPosition.getRow()+2, myPosition.getColumn()-1);
        goodMove(board, myPosition, possibleMoves, myPosition.getRow()+2, myPosition.getColumn() + 1);
        goodMove(board, myPosition, possibleMoves, myPosition.getRow()-2, myPosition.getColumn() -1);
        goodMove(board, myPosition, possibleMoves, myPosition.getRow()-2, myPosition.getColumn()+1);
        goodMove(board, myPosition, possibleMoves, myPosition.getRow()+1, myPosition.getColumn() + 2);
        goodMove(board, myPosition, possibleMoves, myPosition.getRow()+1, myPosition.getColumn() - 2);
        goodMove(board, myPosition, possibleMoves, myPosition.getRow()-1, myPosition.getColumn() - 2);
        goodMove(board, myPosition, possibleMoves, myPosition.getRow()-1, myPosition.getColumn() + 2);
        return possibleMoves;
    }

    private boolean enemyHere(ChessBoard board, ChessPosition myPosition, ChessPosition newPosition) {
        if (inBounds(newPosition.getRow(), newPosition.getColumn())&& board.getPiece(newPosition) != null) {
            if (board.getPiece(newPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                return true;
            }
        }
        return false;
    }
    private Collection<ChessMove> pawnPieceMoves(ChessBoard board, ChessPosition myPosition) {
        boolean initial_move = false;
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7) {
            initial_move = true;
        }
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) {
            initial_move = true;
        }
        int direction = 1;
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            direction = -1;
        }
        Collection<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        ChessPosition inFront = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        if (board.getPiece(inFront)== null) {
            possibleMoves.add(new ChessMove(myPosition, inFront, null));
        }
        if (initial_move) {
            ChessPosition doubleInFront = new ChessPosition(myPosition.getRow() + direction *2, myPosition.getColumn());
            if (board.getPiece(doubleInFront)== null && board.getPiece(inFront) == null) {
                possibleMoves.add(new ChessMove(myPosition, doubleInFront, null));
            }
        }
        ChessPosition diagonalOne = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + 1);
        ChessPosition diagonalTwo = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() - 1);

        if (enemyHere(board, myPosition, diagonalOne)) {
            possibleMoves.add(new ChessMove(myPosition, diagonalOne, null));
        }
        if (enemyHere(board, myPosition, diagonalTwo)) {
            possibleMoves.add(new ChessMove(myPosition, diagonalTwo, null));

        }

        Iterator<ChessMove> iter = possibleMoves.iterator();
        Collection<ChessMove> possibleMoves2 = new ArrayList<ChessMove>();
        while (iter.hasNext()) {
            ChessMove move = iter.next();

            if ((direction == 1 && move.getEndPosition().getRow() == 8) || (direction == -1 && move.getEndPosition().getRow() == 1)) {
                possibleMoves2.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), PieceType.QUEEN));
                possibleMoves2.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), PieceType.ROOK));
                possibleMoves2.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), PieceType.BISHOP));
                possibleMoves2.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), PieceType.KNIGHT));
//                iter.remove();
            } else {
                possibleMoves2.add(move);
            }
        }

        return possibleMoves2;
    }

    private boolean inBounds(int row, int col) {
        return row > 0 && row < 9 && col > 0 && col < 9;
    }

    private boolean goodMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves, int i, int j) {
        if (!inBounds(i, j)) {
            return false;
        }
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
                possibleMoves = kingPieceMoves(board, myPosition);
            }
            case BISHOP -> {
                possibleMoves = bishopPieceMoves(board, myPosition);
            }
            case KNIGHT -> {
                possibleMoves = knightPieceMoves(board, myPosition);
            }
            case PAWN -> {
                possibleMoves = pawnPieceMoves(board, myPosition);
            }
        }
        return possibleMoves;
    }
}
