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
}
