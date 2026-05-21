package service;

import dataaccess.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {
    @Test
    void clearSuccess() throws Exception {
        var dataAccess = new MemoryDataAccess();
        var userService = new UserService(dataAccess);
        var gameService = new GameService(dataAccess);
        var clearService = new ClearService(dataAccess);

        String token = userService.register(new UserService.RegisterUserRequest("user", "pw", "u@u.com")).authToken();
        gameService.createGame(new GameService.CreateGameRequest(token, "MyGame"));

        assertDoesNotThrow(clearService::clear);

        // After clearing, same username should register fine again
        assertDoesNotThrow(() ->
                userService.register(new UserService.RegisterUserRequest("user", "pw", "u@u.com")));
    }
}
