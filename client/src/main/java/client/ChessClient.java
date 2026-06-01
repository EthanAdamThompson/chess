package client;

import java.util.Scanner;

public class ChessClient {
    private final ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);

    private String authToken = null;
    private String username = null;
    // for number -> gameID mapping
    private ServerFacade.GameData[] cachedGames = null;
    public ChessClient(int port) {
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

    // Post login UI
    private String handlePostlogin(String cmd, String[] params) throws Exception {
        return switch (cmd) {
            case "help" -> postloginHelp();
            case "logout" -> logout();
            case "createGame" -> createGame(params);
            case "listGame" -> listGames();
            case "playGame" -> playGame(params);
            case "observeGame" -> observeGame(params);
            default -> "Unknown command. Type 'help' for options.";
        };
    }

    private String postloginHelp() {
        return """
                Available commands:
                  help                   - Show help
                  logout                 - Log out of your account
                  createGame <name>      - Create a new game
                  listGame               - List all available games
                  playGame <#> <color>   - Join a game as WHITE or BLACK
                  observeGame <#>        - Observe a game""";
    }
}
