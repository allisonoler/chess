package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamColor;
    private ChessBoard board;



    public ChessGame() {
        teamColor = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currPiece = board.getPiece(startPosition);
        Collection<ChessMove> returnMoves = new ArrayList<ChessMove>();
        if (currPiece ==null ) {
            return null;
        }
        Collection<ChessMove> potentialMoves = currPiece.pieceMoves(board, startPosition);
        for (ChessMove move : potentialMoves) {
            ChessPiece start_piece = board.getPiece(move.getStartPosition());
            ChessPiece end_piece = board.getPiece(move.getEndPosition());
            board.addPiece(startPosition, null);
            board.addPiece(move.getEndPosition(), start_piece);
            if (!isInCheck(start_piece.getTeamColor())) {
                returnMoves.add(move);
            }
            board.addPiece(startPosition, start_piece);
            board.addPiece(move.getEndPosition(), end_piece);
        }
        return returnMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> possibleMoves = validMoves(move.getStartPosition());
        if (possibleMoves == null || possibleMoves.isEmpty() || board.getPiece(move.getStartPosition()) == null || board.getPiece(move.getStartPosition()).getTeamColor()!=teamColor) {
            throw new InvalidMoveException();
        }
        if (!possibleMoves.contains(move)) {
            throw new InvalidMoveException();
        } else {
            if (move.getPromotionPiece()!=null) {
                board.addPiece(move.getEndPosition(), new ChessPiece(teamColor,move.getPromotionPiece()));
            } else {
                board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            }
            board.addPiece(move.getStartPosition(), null);
        }
        if (teamColor == TeamColor.BLACK) {
            setTeamTurn(TeamColor.WHITE);
        } else {
            setTeamTurn(TeamColor.BLACK);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingSpot = new ChessPosition(0,0);
        for (int i = 1; i<9; i++) {
            for (int j = 1; j<9; j++) {
                ChessPiece piece =board.getPiece(new ChessPosition(i,j));
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor()==teamColor) {
                    kingSpot = new ChessPosition(i,j);
                }
            }
        }

        for (int i= 1; i<=8; i++) {
            for (int j = 1; j<=8; j++) {
                ChessPiece piece =board.getPiece(new ChessPosition(i,j));
                if (piece!=null && piece.getTeamColor()!=teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, new ChessPosition(i,j));
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingSpot)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;

    }



    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        return noMoves(teamColor);
    }

    private boolean noMoves(TeamColor teamColor) {
        for (int i = 1; i<9; i++) {
            for (int j =1; j<9; j++) {
                if (board.getPiece(new ChessPosition(i,j))!=null&&board.getPiece(new ChessPosition(i,j)).getTeamColor()==teamColor) {
                    Collection<ChessMove> possibleMoves = validMoves(new ChessPosition(i, j));
                    if (possibleMoves != null && !possibleMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return noMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        for (int i = 1; i<9; i++) {
            for (int j = 1; j<9; j++) {
                this.board.addPiece(new ChessPosition(i,j),board.getPiece(new ChessPosition(i,j)));
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
