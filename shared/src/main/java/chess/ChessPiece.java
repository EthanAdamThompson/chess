package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece pieceMove = board.getPiece(myPosition);

        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        if (pieceMove.getPieceType() == PieceType.BISHOP) {
            int[][] directionsBishop;
            directionsBishop = new int[][]{{1,1}, {1,-1}, {-1,1}, {-1,-1}};
            for (int[] d : directionsBishop) {
                int r = row;
                int c = col;

                while (true) {
                    r += d[0];
                    c += d[1];

                    if (r < 1 || r > 8 || c < 1 || c > 8) break;

                    ChessPosition newPos = new ChessPosition(r, c);
                    ChessPiece target = board.getPiece(newPos);
                    // Check to see if the square is empty
                    if (target == null) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    } else {
                        // Checks to see if you are capturing an enemy piece
                        if (target.getTeamColor() != pieceMove.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, newPos, null));
                        }
                        break;
                    }

                }
            }
        }
            else if(pieceMove.getPieceType() == PieceType.ROOK) {
                int[][] directionsRook;
                directionsRook = new int[][]{{1,0}, {-1,0}, {0,1}, {0,-1}};
                for (int[] d : directionsRook) {
                    int r = row;
                    int c = col;

                    while (true) {
                        r += d[0];
                        c += d[1];

                        if (r < 1 || r > 8 || c < 1 || c > 8) break;

                        ChessPosition newPos = new ChessPosition(r, c);
                        ChessPiece target = board.getPiece(newPos);
                        // Check to see if the square is empty
                        if (target == null) {
                            moves.add(new ChessMove(myPosition, newPos, null));
                        } else {
                            // Checks to see if you are capturing an enemy piece
                            if (target.getTeamColor() != pieceMove.getTeamColor()) {
                                moves.add(new ChessMove(myPosition, newPos, null));
                            }
                            break;
                        }

                    }
                }
            }else if(pieceMove.getPieceType() == PieceType.QUEEN) {
            int[][] directionsQueen;
            directionsQueen = new int[][]{{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {1,-1}, {-1,1}, {-1,-1}};
            for (int[] d : directionsQueen) {
                int r = row;
                int c = col;

                while (true) {
                    r += d[0];
                    c += d[1];

                    if (r < 1 || r > 8 || c < 1 || c > 8) break;

                    ChessPosition newPos = new ChessPosition(r, c);
                    ChessPiece target = board.getPiece(newPos);
                    // Check to see if the square is empty
                    if (target == null) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    } else {
                        // Checks to see if you are capturing an enemy piece
                        if (target.getTeamColor() != pieceMove.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, newPos, null));
                        }
                        break;
                    }
                }
            }
        }else if(pieceMove.getPieceType() == PieceType.KING) {
            int[][] directionsKing;
            directionsKing = new int[][]{{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {1,-1}, {-1,1}, {-1,-1}};
            for (int[] d : directionsKing) {
                int r = row;
                int c = col;

                    r += d[0];
                    c += d[1];

                    if (r < 1 || r > 8 || c < 1 || c > 8) continue;

                    ChessPosition newPos = new ChessPosition(r, c);
                    ChessPiece target = board.getPiece(newPos);
                    // Check to see if the square is empty
                    if (target == null) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    } else {
                        // Checks to see if you are capturing an enemy piece
                        if (target.getTeamColor() != pieceMove.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, newPos, null));
                        }
                    }

            }
        }else if(pieceMove.getPieceType() == PieceType.KNIGHT) {
            int[][] directionsKnight;
            directionsKnight = new int[][]{{1,2}, {-1,2}, {1,-2}, {-1,-2}, {2,1}, {2,-1}, {-2,1}, {-2,-1}};
            for (int[] d : directionsKnight) {
                int r = row;
                int c = col;

                    r += d[0];
                    c += d[1];

                    if (r < 1 || r > 8 || c < 1 || c > 8) continue;

                    ChessPosition newPos = new ChessPosition(r, c);
                    ChessPiece target = board.getPiece(newPos);
                    // Check to see if the square is empty
                    if (target == null) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    } else {
                        // Checks to see if you are capturing an enemy piece
                        if (target.getTeamColor() != pieceMove.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, newPos, null));
                        }
                        continue;
                    }
                }
            }else if (pieceMove.getPieceType() == PieceType.PAWN) {

            int direction = (pieceMove.getTeamColor() == pieceColor.WHITE) ? 1 : -1;

            // 1. forward move
            int forwardRow = row + direction;
            int r = row;
            int c = col;

            if (forwardRow >= 1 && forwardRow <= 8) {
                ChessPosition forwardPos = new ChessPosition(forwardRow, col);

                if (board.getPiece(forwardPos) == null) {
                    moves.add(new ChessMove(myPosition, forwardPos, null));
                }
            }

            // 2. diagonal captures
            int[][] captures = {
                    {direction, 1},
                    {direction, -1}
            };

            for (int[] d : captures) {
                r += d[0];
                c += d[1];

                if (r < 1 || r > 8 || c < 1 || c > 8) continue;

                ChessPosition newPos = new ChessPosition(r, c);
                ChessPiece target = board.getPiece(newPos);

                if (target != null && target.getTeamColor() != pieceMove.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                }
            }
        }
        return moves;
    }
}
