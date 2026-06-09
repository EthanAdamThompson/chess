package server;

import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import service.GameService;
import service.UserService;
import com.google.gson.Gson;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import websocket.commands.UserGameCommand;

public class WebHandler {
    private final GameService gameService;
    private final UserService userService;
    private final Gson gson = new Gson();
    private final Map<Integer, Set<WsContext>> gameSessions = new ConcurrentHashMap<>();

    public WebHandler(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    public void onConnect(WsContext ctx) {
    }

    private void handleConnect(WsContext ctx, UserGameCommand command) {}
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
