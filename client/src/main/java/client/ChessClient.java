package client;

import chess.ChessMove;
import chess.ChessPosition;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

public class ChessClient {
    private final ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);
    private WebFacade webFacade = null;
    private final int port;
    private String authToken = null;
    private String username = null;
    private chess.ChessGame currentGame = null;
    private String currentColor = "WHITE";
    // for number -> gameID mapping
    private ServerFacade.GameData[] cachedGames = null;
    public ChessClient(int port) {
        this.port = port;
        this.server = new ServerFacade(port);
    }

    // Prelogin UI
    private String handlePrelogin(String cmd, String[] params) throws Exception {
        return switch (cmd) {
            case "help" -> preloginHelp();
            case "quit" -> null; // signals run() to exit
            case "login" -> login(params);
            case "register" -> register(params);
            default -> "Unknown command. Type 'help' for options.";
        };
    }
    private String preloginHelp() {
        return """
                Available commands:
                  help               - Show help
                  login              - Log in to previously made account
                  register           - make a new account
                  quit               - quit the program""";
    }

    private String login(String[] params) throws Exception {
        String username, password;
        if (params.length >= 2) {
            username = params[0];
            password = params[1];
        } else {
            System.out.print("Username: ");
            username = scanner.nextLine().trim();
            System.out.print("Password: ");
            password = scanner.nextLine().trim();
        }
        var auth = server.login(username, password);
        this.authToken = auth.authToken();
        this.username = auth.username();
        return "Logged in as " + this.username + ".";
    }
    // Register new user
    private String register(String[] params) throws Exception {
        String username, password, email;
        if (params.length >= 3) {
            username = params[0];
            password = params[1];
            email = params[2];
        } else {
            System.out.print("Username: ");
            username = scanner.nextLine().trim();
            System.out.print("Password: ");
            password = scanner.nextLine().trim();
            System.out.print("Email: ");
            email = scanner.nextLine().trim();
        }
        var auth = server.register(username, password, email);
        this.authToken = auth.authToken();
        this.username = auth.username();
        return "Registered and logged in as " + this.username + ".";
    }

    // Post login UI
    private String handlePostlogin(String cmd, String[] params) throws Exception {
        return switch (cmd) {
            case "help" -> postloginHelp();
            case "logout" -> logout();
            case "creategame" -> createGame(params);
            case "listgame" -> listGames();
            case "playgame" -> playGame(params);
            case "observegame" -> observeGame(params);
            default -> "Unknown command. Type 'help' for options.";
        };
    }

    private String postloginHelp() {
        return """
                Available commands:
                  help                   - Show help
                  logout                 - Log out of your account
                  creategame <name>      - Create a new game
                  listgame               - List all available games
                  playgame <#> <color>   - Join a game as WHITE or BLACK
                  observegame <#>        - Observe a game""";
    }

    private String logout() throws Exception {
        server.logout(authToken);
        authToken = null;
        username = null;
        return "Logged out.";
    }

    private String createGame(String[] params) throws Exception {
        String gameName;
        if (params.length >= 1) {
            gameName = String.join(" ", params);
        } else {
            System.out.print("Game name: ");
            gameName = scanner.nextLine().trim();
        }
        server.createGame(gameName, authToken);
        return "Game '" + gameName + "' created.";
    }

    private String listGames() throws Exception {
        cachedGames = server.listGames(authToken);
        if (cachedGames.length == 0) {
            return "No games available.";
        }
        var sb = new StringBuilder();
        for (int i = 0; i < cachedGames.length; i++) {
            var games = cachedGames[i];
            String white;
            if(games.whiteUsername() != null) {
                white = games.whiteUsername();
            }else {
                white = "(open)";
            }
            String black;
            if(games.blackUsername() != null) {
                black = games.blackUsername();
            }else {
                black = "(open)";
            }
            sb.append(String.format("  %d. %s  [W: %s | B: %s]%n", i + 1, games.gameName(), white, black));
        }
        return sb.toString().stripTrailing();
    }

    private void onServerMessage(websocket.messages.ServerMessage message) {
        System.out.println();
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                var loadMsg = (websocket.messages.LoadGameMessage) message;
                currentGame = (chess.ChessGame) loadMsg.getGame();
                boolean flip = "BLACK".equals(currentColor);
                BoardDrawer.draw(currentGame, flip);
            }
            case ERROR -> {
                var errMsg = (websocket.messages.ErrorMessage) message;
                System.out.println("[ERROR]: " + errMsg.getErrorMessage());
            }
            case NOTIFICATION -> {
                var notifMsg = (websocket.messages.NotificationMessage) message;
                System.out.println("[NOTICE]: " + notifMsg.getMessage());
            }
        }
        System.out.print("[GAME] >> ");
    }

    private void gameplayLoop(int gameID, String color) {
        System.out.println("Entered game. Type 'help' for commands.");
        while (true) {
            System.out.print("[GAME] >> ");
            String line = scanner.nextLine().trim();
            String[] tokens = line.split("\\s+");
            String cmd = tokens.length > 0 ? tokens[0].toLowerCase() : "";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            try {
                String result = handleGameplay(cmd, params, gameID, color);
                if (result == null) {
                    break; // leave signal
                }
                if (!result.isEmpty()) {
                    System.out.println(result);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private String handleGameplay(String cmd, String[] params, int gameID, String color) throws Exception {
        return switch (cmd) {
            case "help" -> gameplayHelp();
            case "redraw" -> redraw(color);
            case "leave" -> { leave(gameID); yield null; }
            case "move" -> makeMove(params, gameID, color);
            case "resign" -> resign(gameID);
            case "highlight" -> highlight(params, color);
            default -> "Unknown command. Type 'help' for options.";
        };
    }

    private String gameplayHelp() {
        return """
            Available commands:
              help                        - Show help
              redraw                      - Redraw the chess board
              leave                       - Leave the game
              move <from> <to>            - Make a move (e.g. move e2 e4)
              resign                      - Forfeit the game
              highlight <square>          - Highlight legal moves for a piece""";
    }

    private String redraw(String color) {
        if (currentGame == null) {
            return "No game loaded yet.";
        }
        BoardDrawer.draw(currentGame, "BLACK".equals(color));
        return "";
    }

    private void leave(int gameID) throws Exception {
        webFacade.sendCommand(new websocket.commands.UserGameCommand(
                websocket.commands.UserGameCommand.CommandType.LEAVE, authToken, gameID));
    }

    private String makeMove(String[] params, int gameID, String color) {
        String from, to;
        if (params.length >= 2) {
            from = params[0].toLowerCase();
            to = params[1].toLowerCase();
        } else {
            System.out.print("From square (e.g. e2): ");
            from = scanner.nextLine().trim().toLowerCase();
            System.out.print("To square (e.g. e4): ");
            to = scanner.nextLine().trim().toLowerCase();
        }

        chess.ChessPosition fromPos = parseSquare(from);
        chess.ChessPosition toPos = parseSquare(to);
        if (fromPos == null || toPos == null) {
            return "Invalid square. Use format like e2.";
        }

        var move = new chess.ChessMove(fromPos, toPos, null);
        var command = new websocket.commands.MakeMoveCommand(authToken, gameID, move);
        try {
            webFacade.sendCommand(command);
        } catch (Exception exception) {
            return "Error sending move: " + exception.getMessage();
        }
        return "";
    }

    private chess.ChessPosition parseSquare(String square) {
        if (square.length() != 2) return null;
        int col = square.charAt(0) - 'a' + 1;
        int row = square.charAt(1) - '0';
        if (col < 1 || col > 8 || row < 1 || row > 8) return null;
        return new chess.ChessPosition(row, col);
    }

    private String resign(int gameID) throws Exception {
        System.out.print("Are you sure you want to resign? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("yes")) {
            webFacade.sendCommand(new websocket.commands.UserGameCommand(
                    websocket.commands.UserGameCommand.CommandType.RESIGN, authToken, gameID));
            return "You resigned.";
        }
        return "Resign cancelled.";
    }

    private String highlight(String[] params, String color) {
        if (currentGame == null) {
            return "No game loaded yet.";
        }
        String square;
        if (params.length >= 1) {
            square = params[0].toLowerCase();
        } else {
            System.out.print("Square (e.g. e2): ");
            square = scanner.nextLine().trim().toLowerCase();
        }

        ChessPosition pos = parseSquare(square);
        if (pos == null) {
            return "Invalid square. Use format like e2.";
        }

        Collection<ChessMove> moves = currentGame.validMoves(pos);
        if (moves == null || moves.isEmpty()) {
            return "No legal moves for that piece.";
        }

        boolean flip = "BLACK".equals(color);
        BoardDrawer.draw(currentGame, flip, pos, moves);
        return "";
    }

    private String playGame(String[] params) throws Exception {
        int gameNumber;
        String color;
        if (params.length >= 2) {
            try {
                gameNumber = Integer.parseInt(params[0]);
            } catch (NumberFormatException exception) {
                return "Invalid game number.";
            }
            color = params[1].toUpperCase();
        } else {
            System.out.print("Game number: ");
            try {
                gameNumber = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException exception) {
                return "Invalid game number.";
            }
            System.out.print("Color (WHITE/BLACK): ");
            color = scanner.nextLine().trim().toUpperCase();
        }
        if (cachedGames == null) {
            return "Please run 'list' first.";
        }
        if (gameNumber < 1 || gameNumber > cachedGames.length){
            return "Invalid game number.";
        }
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            return "Color must be WHITE or BLACK.";
        }

        int gameID = cachedGames[gameNumber - 1].gameID();
        server.joinGame(gameID, color, authToken);
        webFacade = new WebFacade(port, this::onServerMessage);
        webFacade.connect(authToken, gameID);
        this.currentColor = color;
        gameplayLoop(gameID, color);
        return "";
    }

    private String observeGame(String[] params) throws Exception {
        int gameNumber;
        if (params.length >= 1) {
            gameNumber = Integer.parseInt(params[0]);
        } else {
            System.out.print("Game number: ");
            gameNumber = Integer.parseInt(scanner.nextLine().trim());
        }
        if (cachedGames == null) {
            return "Please run 'list' first.";
        }
        if (gameNumber < 1 || gameNumber > cachedGames.length) {
            return "Invalid game number.";
        }

        int gameID = cachedGames[gameNumber - 1].gameID();
        webFacade = new WebFacade(port, this::onServerMessage);
        webFacade.connect(authToken, gameID);
        this.currentColor = "WHITE";
        gameplayLoop(gameID, "WHITE");
        return "";
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess. Type 'help' to get started.");
        while (true) {
            String prompt = isLoggedIn() ? "[" + username + "] >> " : "[LOGGED_OUT] >> ";
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            String[] tokens = line.split("\\s+");
            String cmd;
            if(tokens.length > 0){
                cmd = tokens[0].toLowerCase();
            }else {
                cmd = "";
            }
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            try {
                String result = isLoggedIn() ? handlePostlogin(cmd, params) : handlePrelogin(cmd, params);
                if (result == null) {
                    break;
                    } // quit signal
                if (!result.isEmpty()) {
                    System.out.println(result);
                }
            } catch (Exception exception) {
                System.out.println("Error: " + exception.getMessage());
            }
        }
    }

    private boolean isLoggedIn() {
        return authToken != null;
    }
}
