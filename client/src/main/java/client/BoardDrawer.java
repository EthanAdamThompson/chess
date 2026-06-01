package client;

public class BoardDrawer {
    // ANSI color codes
    private static final String RESET = "\u001B[0m";
    private static final String DARK_SQUARE = "\u001B[48;5;94m";   // brown
    private static final String LIGHT_SQUARE = "\u001B[48;5;223m"; // tan
    private static final String WHITE_PIECE = "\u001B[1;97m";      // bright white
    private static final String BLACK_PIECE = "\u001B[1;34m";      // blue
    private static final String BORDER = "\u001B[100m";            // dark gray background
    private static final String BORDER_TEXT = "\u001B[97m";        // white text

    private static final String[] BACK_ROW = {"R", "N", "B", "Q", "K", "B", "N", "R"};
    private static final String EMPTY = " ";

    public static void draw(boolean blackPerspective) {
        System.out.println();
        String[] cols = {"a", "b", "c", "d", "e", "f", "g", "h"};

        if (blackPerspective) {
            // Reverse columns and rows
            printColLabels(cols, true);
            for (int row = 1; row <= 8; row++) {
                printRow(row, blackPerspective);
            }
            printColLabels(cols, true);
        } else {
            printColLabels(cols, false);
            for (int row = 8; row >= 1; row--) {
                printRow(row, blackPerspective);
            }
            printColLabels(cols, false);
        }
        System.out.println();
    }

    private static void printColLabels(String[] cols, boolean reversed) {
        System.out.print(BORDER + BORDER_TEXT + "   ");
        if (reversed) {
            for (int i = 7; i >= 0; i--) System.out.print(" " + cols[i] + " ");
        } else {
            for (String col : cols) System.out.print(" " + col + " ");
        }
        System.out.println("   " + RESET);
    }

    private static void printRow(int row, boolean blackPerspective) {
        System.out.print(BORDER + BORDER_TEXT + " " + row + " " + RESET);

        for (int col = 0; col < 8; col++) {
            int actualCol = blackPerspective ? 7 - col : col;
            boolean lightSquare = (row + actualCol) % 2 != 0;
            String squareColor = lightSquare ? LIGHT_SQUARE : DARK_SQUARE;
            String piece = getPiece(row, actualCol);
            String pieceColor = isPieceWhite(row) ? WHITE_PIECE : BLACK_PIECE;

            System.out.print(squareColor + pieceColor + " " + piece + " " + RESET);
        }

        System.out.print(BORDER + BORDER_TEXT + " " + row + " " + RESET);
        System.out.println();
    }

    private static String getPiece(int row, int col) {
        if (row == 1) return BACK_ROW[col];
        if (row == 2) return "P";
        if (row == 7) return "P";
        if (row == 8) return BACK_ROW[col];
        return EMPTY;
    }

    private static boolean isPieceWhite(int row) {
        return row == 2 || row == 1;
    }
}
