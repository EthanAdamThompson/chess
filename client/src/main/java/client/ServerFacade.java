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
    public int createGame(String gameName, String authToken) throws Exception {
        var body = Map.of("gameName", gameName);
        var result = makeRequest("POST", "/game", body, authToken, Map.class);
        return ((Double) result.get("gameID")).intValue();
    }
    public GameData[] listGames(String authToken) throws Exception {
        var result = makeRequest("GET", "/game", null, authToken, ListGamesResponse.class);
        return result.games();
    }
    public void joinGame(int gameID, String playerColor, String authToken) throws Exception {
        var body = Map.of("gameID", gameID, "playerColor", playerColor);
        makeRequest("PUT", "/game", body, authToken, null);
    }
    // HTTP stuff
    private <T> T makeRequest(String method, String path, Object body,
                              String authToken, Class<T> responseClass) throws Exception {
        URI uri = new URI(serversUrl + path);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);
        http.setRequestProperty("Content-Type", "application/json");
        if (authToken != null) {
            http.setRequestProperty("authorization", authToken);
        }
        if (body != null) {
            http.setDoOutput(true);
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(gson.toJson(body).getBytes());
            }
        }
        http.connect();
        int status = http.getResponseCode();
        if (status >= 400) {
            InputStream err = http.getErrorStream();
            String msg = err != null ? new String(err.readAllBytes()) : "Error " + status;
            // Pull out the server's "message" field if present
            var parsed = gson.fromJson(msg, Map.class);
            String serverMsg = parsed != null && parsed.containsKey("message")
                    ? (String) parsed.get("message") : msg;
            throw new Exception(serverMsg);
        }
        if (responseClass == null) {
            return null;
        }
        try (var inputStream = http.getInputStream()) {
            return gson.fromJson(new String(inputStream.readAllBytes()), responseClass);
        }
    }
    public void clear() throws Exception {
        makeRequest("DELETE", "/db", null, null, null);
    }
}
