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
    private void broadcastAll(int gameID, String message) {
        var sessions = gameSessions.get(gameID);
        if (sessions != null) {
            for (var session : sessions) {
                session.send(message);
            }
        }
    }
    private void handleMakeMove(WsContext ctx, UserGameCommand command) {
        try {
            AuthData auth = dataAccess.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(ctx, "Error: invalid auth token");
                return;
            }
            String username = auth.username();
            GameData gameData = dataAccess.getGame(command.getGameID());
            if (gameData == null) {
                sendError(ctx, "Error: game not found");
                return;
            }
            chess.ChessGame game = gameData.game();
            // Check game is not already over
            if (game.isOver()) {
                sendError(ctx, "Error: game is already over");
                return;
            }
            // Check it's this player's turn
            chess.ChessGame.TeamColor playerColor = null;
            if (username.equals(gameData.whiteUsername())) {
                playerColor = chess.ChessGame.TeamColor.WHITE;
            } else if (username.equals(gameData.blackUsername())) {
                playerColor = chess.ChessGame.TeamColor.BLACK;
            }
            if (playerColor == null) {
                sendError(ctx, "Error: you are not a player in this game");
                return;
            }
            if (game.getTeamTurn() != playerColor) {
                sendError(ctx, "Error: it is not your turn");
                return;
            }
            // Get the move from the command
            websocket.commands.MakeMoveCommand moveCommand = gson.fromJson(
                    gson.toJson(command), websocket.commands.MakeMoveCommand.class);
            chess.ChessMove move = moveCommand.getMove();
            // Attempt the move
            try {
                game.makeMove(move);
            } catch (chess.InvalidMoveException exception) {
                sendError(ctx, "Error: invalid move - " + exception.getMessage());
                return;
            }
            // Save updated game
            dataAccess.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
            // Broadcast LOAD_GAME to all clients
            String loadJson = gson.toJson(new LoadGameMessage(dataAccess.getGame(command.getGameID())));
            broadcastAll(command.getGameID(), loadJson);

            // Notify others of the move
            String moveDesc = move.getStartPosition().toString() + " to " + move.getEndPosition().toString();
            broadcast(command.getGameID(), ctx, gson.toJson(
                    new NotificationMessage(username + " moved " + moveDesc)));

            // Check for check, checkmate, stalemate
            chess.ChessGame.TeamColor opponent =
                    playerColor == chess.ChessGame.TeamColor.WHITE
                            ? chess.ChessGame.TeamColor.BLACK
                            : chess.ChessGame.TeamColor.WHITE;

            if (game.isInCheckmate(opponent)) {
                String name = opponent == chess.ChessGame.TeamColor.WHITE
                        ? gameData.whiteUsername() : gameData.blackUsername();
                broadcastAll(command.getGameID(), gson.toJson(
                        new NotificationMessage(name + " is in checkmate! Game over.")));
                game.setOver(true);
                dataAccess.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
            } else if (game.isInStalemate(opponent)) {
                broadcastAll(command.getGameID(), gson.toJson(
                        new NotificationMessage("Stalemate! Game over.")));
                game.setOver(true);
                dataAccess.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
            } else if (game.isInCheck(opponent)) {
                String name = opponent == chess.ChessGame.TeamColor.WHITE
                        ? gameData.whiteUsername() : gameData.blackUsername();
                broadcastAll(command.getGameID(), gson.toJson(
                        new NotificationMessage(name + " is in check!")));
            }

        } catch (Exception exception) {
            sendError(ctx, "Error: " + exception.getMessage());
        }
    }
    private void handleLeave(WsContext ctx, UserGameCommand command) {
        try {
            AuthData auth = dataAccess.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(ctx, "Error: invalid auth token");
                return;
            }
            String username = auth.username();

            GameData gameData = dataAccess.getGame(command.getGameID());
            if (gameData == null) {
                sendError(ctx, "Error: game not found");
                return;
            }

            // Remove player from game if they are a player (not observer)
            if (username.equals(gameData.whiteUsername())) {
                dataAccess.updateGame(new GameData(gameData.gameID(), null,
                        gameData.blackUsername(), gameData.gameName(), gameData.game()));
            } else if (username.equals(gameData.blackUsername())) {
                dataAccess.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(),
                        null, gameData.gameName(), gameData.game()));
            }

            // Remove session
            var sessions = gameSessions.get(command.getGameID());
            if (sessions != null) {
                sessions.remove(ctx);
            }

            // Notify others
            broadcast(command.getGameID(), ctx, gson.toJson(
                    new NotificationMessage(username + " left the game")));

        } catch (Exception exception) {
            sendError(ctx, "Error: " + exception.getMessage());
        }
    }
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
