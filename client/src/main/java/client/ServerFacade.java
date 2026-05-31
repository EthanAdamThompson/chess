package client;

import com.google.gson.Gson;
import java.io.*;
import java.net.*;
import java.util.Map;

public class ServerFacade {
    private final String serversUrl;
    private final Gson gson = new Gson();

    // creates a facade of the server.
    public ServerFacade(int port) {
        this.serversUrl = "http://localhost:" + port;
    }
    public record AuthData(String authToken, String username) {}
    public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName) {}
    private record ListGamesResponse(GameData[] games) {}

    // Authorization stuff
    public AuthData register(String username, String password, String email) throws Exception {
        var body = Map.of("username", username, "password", password, "email", email);
        return makeRequest("POST", "/user", body, null, AuthData.class);
    }

    public AuthData login(String username, String password) throws Exception {
        var body = Map.of("username", username, "password", password);
        return makeRequest("POST", "/session", body, null, AuthData.class);
    }

    public void logout(String authToken) throws Exception {
        makeRequest("DELETE", "/session", null, authToken, null);
    }

    // Game Stuff
    // HTTP stuff
}
