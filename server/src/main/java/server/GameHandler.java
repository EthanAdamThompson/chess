package server;
import com.google.gson.Gson;
import service.GameService;
import io.javalin.http.Context;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import com.google.gson.JsonObject;

public class GameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void listGames(Context context) {
        try {
            String authToken = context.header("authorization");
            context.status(200).json(gameService.listGames(new GameService.ListGameRequest(authToken)));
        } catch (UnauthorizedException exception) {
            context.status(401).json(new ErrorResponse(exception.getMessage()));
        } catch (Exception exception) {
            context.status(500).json(new ErrorResponse("Error: " + exception.getMessage()));
        }
    }

    public void createGame(Context context) {
        try {
            String authToken = context.header("authorization");
            JsonObject body = gson.fromJson(context.body(), JsonObject.class);
            String gameName = body != null && body.has("gameName") ? body.get("gameName").getAsString() : null;
            context.status(200).json(gameService.createGame(new GameService.CreateGameRequest(authToken, gameName)));
        } catch (BadRequestException exception) {
            context.status(400).json(new ErrorResponse(exception.getMessage()));
        } catch (UnauthorizedException exception) {
            context.status(401).json(new ErrorResponse(exception.getMessage()));
        } catch (Exception exception) {
            context.status(500).json(new ErrorResponse("Error: " + exception.getMessage()));
        }
    }

    public void joinGame(Context context) {
        try {
            String authToken = context.header("authorization");
            JsonObject body = gson.fromJson(context.body(), JsonObject.class);
            String playerColor = body != null && body.has("playerColor") ? body.get("playerColor").getAsString() : null;
            int gameID = (body != null) && body.has("gameID") ? body.get("gameID").getAsInt() : 0;
            gameService.JoinGame(new GameService.JoinGameRequest(authToken, playerColor, gameID));
            context.status(200).result("{}");
        } catch (BadRequestException exception) {
            context.status(400).json(new ErrorResponse(exception.getMessage()));
        } catch (UnauthorizedException exception) {
            context.status(401).json(new ErrorResponse(exception.getMessage()));
        } catch (AlreadyTakenException exception) {
            context.status(403).json(new ErrorResponse(exception.getMessage()));
        } catch (Exception exception) {
            context.status(500).json(new ErrorResponse("Error: " + exception.getMessage()));
        }
    }
}
