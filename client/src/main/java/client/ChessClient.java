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
        if (cachedGames.length == 0) return "No games available.";
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

    private String playGame(String[] params) throws Exception {
        int gameNumber;
        String color;
        if (params.length >= 2) {
            gameNumber = Integer.parseInt(params[0]);
            color = params[1].toUpperCase();
        } else {
            System.out.print("Game number: ");
            gameNumber = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Color (WHITE/BLACK): ");
            color = scanner.nextLine().trim().toUpperCase();
        }
        if (cachedGames == null) return "Please run 'list' first.";
        if (gameNumber < 1 || gameNumber > cachedGames.length) return "Invalid game number.";
        if (!color.equals("WHITE") && !color.equals("BLACK")) return "Color must be WHITE or BLACK.";

        int gameID = cachedGames[gameNumber - 1].gameID();
        server.joinGame(gameID, color, authToken);
        BoardDrawer.draw(color.equals("BLACK")); // not created yet
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
        if (cachedGames == null) return "Please run 'list' first.";
        if (gameNumber < 1 || gameNumber > cachedGames.length) return "Invalid game number.";

        BoardDrawer.draw(false); // observers see white's perspective
        return "";
    }
}
