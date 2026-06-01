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
}
