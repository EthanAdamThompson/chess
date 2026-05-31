package client;

import java.util.Arrays;
import java.util.Scanner;

public class ChessCleint {
    private final ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);

    private String authToken = null;
    private String username = null;
    // for number -> gameID mapping
    private ServerFacade.GameData[] cachedGames = null;

}
