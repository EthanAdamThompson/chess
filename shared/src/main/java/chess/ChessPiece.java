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
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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

        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // Declaration of Variables
        Collection<ChessMove> moveTaken = new ArrayList<>(); // Creates new List called
        ChessPiece pieceMove = board.getPiece(myPosition);

        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        if (pieceMove.getPieceType() == PieceType.BISHOP) {
            int[][] directionsBishop;
            // Defines the 4 directions bishops can take
            directionsBishop = new int[][]{{1,1}, {1,-1}, {-1,1}, {-1,-1}};
            for (int[] d : directionsBishop) { // Increments through each different possible direction
                int r = row;
                int c = col;
                // Goes through and increments both row and column
                while (true) {
                    r += d[0];// chooses the first in the list of directions
                    c += d[1];// chooses the second
                    // Checks if tile is on the board
                    if (r < 1 || r > 8 || c < 1 || c > 8) {
                        break;
                    }
                    ChessPosition newPos = new ChessPosition(r, c);
                    ChessPiece target = board.getPiece(newPos);
                    // Check to see if the square is empty
                    if (target == null) { //
                        moveTaken.add(new ChessMove(myPosition, newPos, null));
                    } else {
                        // Checks to see if you are capturing an enemy piece
                        if (target.getTeamColor() != pieceMove.getTeamColor()) {
                            moveTaken.add(new ChessMove(myPosition, newPos, null));
                        }
                        break;
                    }
                }
            }
        }
        else if(pieceMove.getPieceType() == PieceType.ROOK) {
            int[][] directionsRook;
            // Creates all 4 different directions that a rook can go
            directionsRook = new int[][]{{1,0}, {-1,0}, {0,1}, {0,-1}};
                for (int[] d : directionsRook) {
                    int r = row;
                    int c = col;
                    // Goes through and increments the row and columns
                    while (true) {
                        r += d[0]; // Goes based on the first position of the direction list
                        c += d[1]; // Goes based on second
                        // Checks for an out of bounds
                        if (r < 1 || r > 8 || c < 1 || c > 8) {
                            break;
                        }

                        ChessPosition newPos = new ChessPosition(r, c);
                        ChessPiece target = board.getPiece(newPos);
                        // Check to see if the square is empty
                        if (target == null) {
                            moveTaken.add(new ChessMove(myPosition, newPos, null));
                        } else {
                            // Checks to see if you are capturing an enemy piece
                            if (target.getTeamColor() != pieceMove.getTeamColor()) {
                                moveTaken.add(new ChessMove(myPosition, newPos, null));
                            }
                            break;
                        }
                    }
                }
            }
        else if(pieceMove.getPieceType() == PieceType.QUEEN) {
            int[][] directionsQueen;
            // Checks all 8 possible directions for a queen to move (bishop and rook combined)
            directionsQueen = new int[][]{{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {1,-1}, {-1,1}, {-1,-1}};
            for (int[] d : directionsQueen) {
                int r = row;
                int c = col;
                // Go through and increments until it finds and edge or a blocker
                while (true) {
                    r += d[0]; // 0th Index for directionQueen list
                    c += d[1]; // 1st Index
                    // Checks for out of bound
                    if (r < 1 || r > 8 || c < 1 || c > 8) {
                        break;
                    }

                    ChessPosition newPos = new ChessPosition(r, c);
                    ChessPiece target = board.getPiece(newPos);
                    // Check to see if the square is empty
                    if (target == null) {
                        moveTaken.add(new ChessMove(myPosition, newPos, null));
                    } else {
                        // Checks to see if you are capturing an enemy piece
                        if (target.getTeamColor() != pieceMove.getTeamColor()) {
                            moveTaken.add(new ChessMove(myPosition, newPos, null));
                        }
                        break;
                    }
                }
            }
        }
        else if(pieceMove.getPieceType() == PieceType.KING) {
            int[][] directionsKing;
            // Checks all 8 directions the king can go (same as queen)
            directionsKing = new int[][]{{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {1,-1}, {-1,1}, {-1,-1}};
            for (int[] d : directionsKing) {
                int r = row;
                int c = col;
                    // No while loop because the king only can move one square
                    r += d[0];
                    c += d[1];
                    // Checks for out of bound
                    if (r < 1 || r > 8 || c < 1 || c > 8) {
                        continue; // continue instead of break
                    }

                    ChessPosition newPos = new ChessPosition(r, c);
                    ChessPiece target = board.getPiece(newPos);
                    // Check to see if the square is empty
                    if (target == null) {
                        moveTaken.add(new ChessMove(myPosition, newPos, null));
                    } else {
                        // Checks to see if you are capturing an enemy piece
                        if (target.getTeamColor() != pieceMove.getTeamColor()) {
                            moveTaken.add(new ChessMove(myPosition, newPos, null));
                        }
                    }
            }
        }
        else if(pieceMove.getPieceType() == PieceType.KNIGHT) {
            int[][] directionsKnight;
            // Checks all 8 spots where the knight can move
            directionsKnight = new int[][]{{1,2}, {-1,2}, {1,-2}, {-1,-2}, {2,1}, {2,-1}, {-2,1}, {-2,-1}};
            for (int[] d : directionsKnight) {
                int r = row;
                int c = col;
                    // Like king no while loop needed
                    r += d[0];
                    c += d[1];
                    // Checks for out of bound
                    if (r < 1 || r > 8 || c < 1 || c > 8) {
                        continue; // Continue instead of break
                    }

                    ChessPosition newPos = new ChessPosition(r, c);
                    ChessPiece target = board.getPiece(newPos);
                    // Check to see if the square is empty
                    if (target == null) {
                        moveTaken.add(new ChessMove(myPosition, newPos, null));
                    } else {
                        // Checks to see if you are capturing an enemy piece
                        if (target.getTeamColor() != pieceMove.getTeamColor()) {
                            moveTaken.add(new ChessMove(myPosition, newPos, null));
                        }
                    }
                }
            }
        else if (pieceMove.getPieceType() == PieceType.PAWN) {
            // Declaration of variables
            int direction = (pieceMove.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
            // forward move
            int forwardRow = row + direction;
            // boolean to see if the piece is on the promotion row
            boolean isPromotionRow = (pieceMove.getTeamColor() == ChessGame.TeamColor.WHITE && forwardRow == 8)
                    || (pieceMove.getTeamColor() == ChessGame.TeamColor.BLACK && forwardRow == 1);
            // Assigns the color for ease of use later
            boolean isWhite = pieceMove.getTeamColor() == ChessGame.TeamColor.WHITE;

            if (forwardRow >= 1 && forwardRow <= 8) {
                ChessPosition forwardPos = new ChessPosition(forwardRow, col);

                if (board.getPiece(forwardPos) == null) {
                    if (isPromotionRow) {
                        moveTaken.add(new ChessMove(myPosition, forwardPos, PieceType.QUEEN));
                        moveTaken.add(new ChessMove(myPosition, forwardPos, PieceType.ROOK));
                        moveTaken.add(new ChessMove(myPosition, forwardPos, PieceType.BISHOP));
                        moveTaken.add(new ChessMove(myPosition, forwardPos, PieceType.KNIGHT));
                    } else {
                        moveTaken.add(new ChessMove(myPosition, forwardPos, null));
                    }
                }
            }

            // diagonal captures
            int[][] captures = {{direction, 1}, {direction, -1}};

            for (int[] d : captures) {
                int r = row + d[0];
                int c = col + d[1];

                if (r < 1 || r > 8 || c < 1 || c > 8) {
                    continue;
                }

                ChessPosition newPos = new ChessPosition(r, c);
                ChessPiece target = board.getPiece(newPos);

                if (target != null && target.getTeamColor() != pieceMove.getTeamColor()) {
                    if (isPromotionRow) {
                        moveTaken.add(new ChessMove(myPosition,newPos, PieceType.QUEEN));
                        moveTaken.add(new ChessMove(myPosition, newPos, PieceType.ROOK));
                        moveTaken.add(new ChessMove(myPosition, newPos, PieceType.BISHOP));
                        moveTaken.add(new ChessMove(myPosition, newPos, PieceType.KNIGHT));
                    } else {
                        moveTaken.add(new ChessMove(myPosition, newPos, null));
                    }
                }
            }
            // When you go forward Double
            int startRow = isWhite ? 2 : 7;
            int doubleRow = row + (2 * direction); // So negative if Black

            if (row == startRow) {
                ChessPosition oneStep = new ChessPosition(row + direction, col);
                ChessPosition twoStep = new ChessPosition(doubleRow, col);

                if (board.getPiece(oneStep) == null &&
                        board.getPiece(twoStep) == null) {

                    if (isPromotionRow) {
                        moveTaken.add(new ChessMove(myPosition, twoStep, PieceType.QUEEN));
                        moveTaken.add(new ChessMove(myPosition, twoStep, PieceType.ROOK));
                        moveTaken.add(new ChessMove(myPosition, twoStep, PieceType.BISHOP));
                        moveTaken.add(new ChessMove(myPosition, twoStep, PieceType.KNIGHT));
                    } else {
                        moveTaken.add(new ChessMove(myPosition, twoStep, null));
                    }
                }
            }
        }
        return moveTaken;
    }
}
