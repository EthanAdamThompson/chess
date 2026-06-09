package server;

import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import model.GameData;
import service.GameService;
import service.UserService;
import dataaccess.DataAccess;
import com.google.gson.Gson;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import model.AuthData;
import websocket.messages.NotificationMessage;
import websocket.messages.ErrorMessage;

public class WebHandler {
    private final GameService gameService;
    private final UserService userService;
    private final DataAccess dataAccess;
    private final Gson gson = new Gson();
    private final Map<Integer, Set<WsContext>> gameSessions = new ConcurrentHashMap<>();

    public WebHandler(GameService gameService, UserService userService, DataAccess dataAccess) {
        this.gameService = gameService;
        this.userService = userService;
        this.dataAccess = dataAccess;
    }

    public void onConnect(WsContext ctx) {
    }

    private void handleConnect(WsContext ctx, UserGameCommand command) {
        try {
            AuthData auth = dataAccess.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(ctx, "Error: invalid auth token");
                return;
            }
            String username = auth.username();
            // Validate auth token
            if (username == null) {
                sendError(ctx, "Error: invalid auth token");
                return;
            }

            // Load the game
            GameData game = dataAccess.getGame(command.getGameID());
            if (game == null) {
                sendError(ctx, "Error: game not found");
                return;
            }

            // Add session to the map
            gameSessions.computeIfAbsent(command.getGameID(), k -> ConcurrentHashMap.newKeySet()).add(ctx);

            // Send LOAD_GAME to root client
            var loadGameMessage = new LoadGameMessage(game);
            ctx.send(gson.toJson(loadGameMessage));

            // Send NOTIFICATION to all other clients
            String notification = username + " joined the game";
            var notificationMessage = new NotificationMessage(notification);
            broadcast(command.getGameID(), ctx, gson.toJson(notificationMessage));

        } catch (Exception exception) {
            sendError(ctx, "Error: " + exception.getMessage());
        }
    }

    private void sendError(WsContext ctx, String message) {
        ctx.send(gson.toJson(new ErrorMessage(message)));
    }

    private void broadcast(int gameID, WsContext exclude, String message) {
        var sessions = gameSessions.get(gameID);
        if (sessions != null) {
            for (var session : sessions) {
                if (!session.equals(exclude)) {
                    session.send(message);
                }
            }
        }
    }
    private void handleMakeMove(WsContext ctx, UserGameCommand command) {}
    private void handleLeave(WsContext ctx, UserGameCommand command) {}
    private void handleResign(WsContext ctx, UserGameCommand command) {}
    public void onMessage(WsMessageContext ctx) {
        UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> handleConnect(ctx, command);
            case MAKE_MOVE -> handleMakeMove(ctx, command);
            case LEAVE -> handleLeave(ctx, command);
            case RESIGN -> handleResign(ctx, command);
        }
    }

    public void onClose(WsContext ctx) {
    }

    public void onError(WsContext ctx) {
    }
}
