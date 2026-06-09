package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import io.javalin.Javalin;
import org.jetbrains.annotations.NotNull;
import service.ClearService;
import service.GameService;
import service.UserService;


public class Server {

    private final Javalin javalin;

    public Server() {
        // Creation
        DataAccess dataAccess;
        try {
            dataAccess = new MySqlDataAccess();
        } catch (DataAccessException exception) {
            throw new RuntimeException("Failed to initialize database: " + exception.getMessage());
        }
        var userService = new UserService(dataAccess);
        var gameService = new GameService(dataAccess);
        var clearService = new ClearService(dataAccess);
        var userHandler = new UserHandler(userService);
        var gameHandler = new GameHandler(gameService);
        var webHandler = new WebHandler(gameService, userService);

        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new io.javalin.json.JsonMapper() {
                private final Gson gson = new Gson();
                @NotNull
                @Override
                public String toJsonString(@NotNull Object object, @NotNull java.lang.reflect.Type type) {
                    return gson.toJson(object);
                }
                @NotNull
                @Override
                public <T> T fromJsonString(@NotNull String json, @NotNull java.lang.reflect.Type targetType) {
                    return gson.fromJson(json, targetType);
                }
            });
        });
        javalin.ws("/ws", ws -> {
            ws.onConnect(webHandler::onConnect);
            ws.onMessage(webHandler::onMessage);
            ws.onClose(webHandler::onClose);
            ws.onError(webHandler::onError);
        });

        // Clear the database
        javalin.delete("/db", ctx -> {
            try {
                clearService.clear();
                ctx.status(200).result("{}");
            } catch (Exception e) {
                ctx.status(500).json(new ErrorResponse("Error: " + e.getMessage()));
            }
        });

        // Register your endpoints and exception handlers here.
        // User endpoints
        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler::logout);

        // Game endpoints
        javalin.get("/game", gameHandler::listGames);
        javalin.post("/game", gameHandler::createGame);
        javalin.put("/game", gameHandler::joinGame);

        // Global exception handler
        javalin.exception(Exception.class, (e, ctx) ->
                ctx.status(500).json(new ErrorResponse("Error: " + e.getMessage())));

    }

    //Given by default
    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    //Given by default
    public void stop() {
        javalin.stop();
    }
}
