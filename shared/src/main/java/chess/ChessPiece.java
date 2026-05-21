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

    private static final int[][] DIAGONAL_DIRECTIONS = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
    private static final int[][] STRAIGHT_DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    private static final int[][] ALL_DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
    private static final int[][] KNIGHT_JUMPS = {{1, 2}, {-1, 2}, {1, -2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};

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
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece pieceMove = board.getPiece(myPosition);

        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int[][] directions;
        if(getPieceType() == PieceType.BISHOP ||getPieceType() == PieceType.ROOK
                || getPieceType() == PieceType.QUEEN || getPieceType() == PieceType.KING){
            if (getPieceType() == PieceType.BISHOP){
                directions = DIAGONAL_DIRECTIONS;
            } else if (getPieceType() == PieceType.ROOK){
                directions = STRAIGHT_DIRECTIONS;
            } else {
                directions = ALL_DIRECTIONS;
            }
            for(int[] d : directions){
                int r = row;
                int c = col;
                while(true) {
                    r += d[0];
                    c += d[1];
                    if(r < 1 || r > 8 || c < 1 || c > 8 ){break;}
                    ChessPosition newPos = new ChessPosition(r,c);
                    ChessPiece target = board.getPiece(newPos);
                    if(target == null && getPieceType() != PieceType.KING){
                        moves.add(new ChessMove(myPosition, newPos,null));
                    }else if(target == null && getPieceType() == PieceType.KING){
                        moves.add(new ChessMove(myPosition, newPos,null));
                        break;
                    }else if(target.getTeamColor() != pieceMove.getTeamColor() ){
                        moves.add(new ChessMove(myPosition, newPos,null));
                        break;
                    } else{
                        break;
                    }
                }
            }
        }else if(getPieceType() == PieceType.KNIGHT){
            directions = KNIGHT_JUMPS;
            for(int[] d : directions){
                int r = row;
                int c = col;
                r += d[0];
                c += d[1];
                if(r < 1 || r > 8 || c < 1 || c > 8 ){continue;}
                ChessPosition newPos = new ChessPosition(r,c);
                ChessPiece target = board.getPiece(newPos);
                if(target == null){
                    moves.add(new ChessMove(myPosition, newPos,null));
                } else if(target.getTeamColor() != pieceMove.getTeamColor() ) {
                    moves.add(new ChessMove(myPosition, newPos, null));}}
        }else if(getPieceType() == PieceType.PAWN){
            int direction = (pieceMove.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
            int forwardRow = row + direction;
            boolean isPromotionRow = (pieceMove.getTeamColor() == ChessGame.TeamColor.WHITE && forwardRow == 8)
                    || (pieceMove.getTeamColor() == ChessGame.TeamColor.BLACK && forwardRow == 1);
            boolean isWhite = (pieceMove.getTeamColor() == ChessGame.TeamColor.WHITE);
            if (forwardRow >= 1 && forwardRow <= 8) {
                ChessPosition forwardPos = new ChessPosition(forwardRow, col);
                if (board.getPiece(forwardPos) == null) {
                    if (isPromotionRow) {
                        moves.add(new ChessMove(myPosition, forwardPos, PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, forwardPos, PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, forwardPos, PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, forwardPos, PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(myPosition, forwardPos, null));}}}
            // Capture
            int[][] captures = {{direction, 1}, {direction, -1}};
            for (int[] d : captures) {
                int r = row + d[0];
                int c = col + d[1];
                if (r < 1 || r > 8 || c < 1 || c > 8) {continue;}
                ChessPosition newPos = new ChessPosition(r, c);
                ChessPiece target = board.getPiece(newPos);
                if (target != null && target.getTeamColor() != pieceMove.getTeamColor()) {
                    if (isPromotionRow) {
                        moves.add(new ChessMove(myPosition,newPos, PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, newPos, PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, newPos, PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, newPos, PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(myPosition, newPos, null));}}}
            // When you go forward Double
            int startRow = isWhite ? 2 : 7;
            int doubleRow = row + (2 * direction); // So negative if Black
            if (row == startRow) {
                ChessPosition oneStep = new ChessPosition(row + direction, col);
                ChessPosition twoStep = new ChessPosition(doubleRow, col);
                if (board.getPiece(oneStep) == null &&
                        board.getPiece(twoStep) == null) {
                    if (isPromotionRow) {
                        moves.add(new ChessMove(myPosition, twoStep, PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, twoStep, PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, twoStep, PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, twoStep, PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(myPosition, twoStep, null));}}}}
        return moves;
    }
}
