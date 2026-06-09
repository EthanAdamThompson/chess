package server;

import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import service.GameService;
import service.UserService;
import com.google.gson.Gson;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

    public void onMessage(WsContext ctx) {
    }

    public void onClose(WsContext ctx) {
    }

    public void onError(WsContext ctx) {
    }
}
