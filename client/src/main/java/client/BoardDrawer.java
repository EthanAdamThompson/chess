package client;
import chess.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class BoardDrawer {
    // ANSI color codes
    private static final String RESET = "\u001B[0m";
    private static final String DARK_SQUARE = "\u001B[48;5;223m";  // tan (was brown)
    private static final String LIGHT_SQUARE = "\u001B[48;5;94m";  // brown (was tan)
    private static final String WHITE_PIECE = "\u001B[1;97m";      // bright white
    private static final String BLACK_PIECE = "\u001B[1;34m";      // blue
    private static final String BORDER = "\u001B[100m";            // dark gray background
    private static final String BORDER_TEXT = "\u001B[97m";        // white text

    /** Draw with no highlights */
    public static void draw(ChessGame game, boolean blackPerspective) {
        draw(game, blackPerspective, null, null);
    }

    /** Draw with a selected square and its legal move targets highlighted */
    public static void draw(ChessGame game, boolean blackPerspective,
                            ChessPosition selected, Collection<ChessMove> legalMoves) {
        Set<ChessPosition> targets = new HashSet<>();
        if (legalMoves != null) {
            for (ChessMove m : legalMoves) {
                targets.add(m.getEndPosition());
            }
        }

        String[] cols = {"a", "b", "c", "d", "e", "f", "g", "h"};
        System.out.println();
        printColLabels(cols, blackPerspective);

        if (blackPerspective) {
            for (int row = 1; row <= 8; row++) {
                printRow(game, row, blackPerspective, selected, targets);
            }
        } else {
            for (int row = 8; row >= 1; row--) {
                printRow(game, row, blackPerspective, selected, targets);
            }
        }

        printColLabels(cols, blackPerspective);
        System.out.println();
    }

    private static void printColLabels(String[] cols, boolean reversed) {
        System.out.print(BORDER + BORDER_TEXT + "   ");
        if (reversed) {
            for (int i = 7; i >= 0; i--) {
                System.out.print(" " + cols[i] + " ");
            }
        } else {
            for (String col : cols) {
                System.out.print(" " + col + " ");
            }
        }
        System.out.println("   " + RESET);
    }

    private static void printRow(ChessGame game, int row, boolean blackPerspective,
                                 ChessPosition selected, Set<ChessPosition> targets) {
        System.out.print(BORDER + BORDER_TEXT + " " + row + " " + RESET);

        for (int col = 0; col < 8; col++) {
            int actualCol = blackPerspective ? col : 7 - col;
            // ChessPosition is 1-indexed
            ChessPosition pos = new ChessPosition(row, actualCol + 1);
            boolean lightSquare = (row + actualCol) % 2 != 0;

            String squareColor;
            if (selected != null && pos.equals(selected)) {
                squareColor = HIGHLIGHT_SELECTED;
            } else if (targets.contains(pos)) {
                squareColor = HIGHLIGHT_LEGAL;
            } else {
                squareColor = lightSquare ? LIGHT_SQUARE : DARK_SQUARE;
            }

            ChessPiece piece = game.getBoard().getPiece(pos);
            String display = pieceStr(piece);
            String pieceColor = (piece != null && piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                    ? WHITE_PIECE : BLACK_PIECE;

            System.out.print(squareColor + pieceColor + " " + display + " " + RESET);
        }

        System.out.print(BORDER + BORDER_TEXT + " " + row + " " + RESET);
        System.out.println();
    }

    private static String pieceStr(ChessPiece piece) {
        if (piece == null) return " ";
        return switch (piece.getPieceType()) {
            case KING   -> "K";
            case QUEEN  -> "Q";
            case ROOK   -> "R";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case PAWN   -> "P";
        };
    }
}
