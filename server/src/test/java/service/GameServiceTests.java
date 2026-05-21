package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    private UserService userService;
    private GameService gameService;
    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        var dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        authToken = userService.register(new UserService.RegisterUserRequest("player1", "pw", "p@p.com")).authToken();
    }

    // ---- Create Game ----

    @Test
    void createGameSuccess() throws DataAccessException {
        var result = gameService.createGame(new GameService.CreateGameRequest(authToken, "TestGame"));
        assertTrue(result.gameID() > 0);
    }

    @Test
    void createGameBadAuthFails() {
        assertThrows(UnauthorizedException.class, () ->
                gameService.createGame(new GameService.CreateGameRequest("bad-token", "TestGame")));
    }

    @Test
    void createGameNoNameFails() {
        assertThrows(BadRequestException.class, () ->
                gameService.createGame(new GameService.CreateGameRequest(authToken, "")));
    }

    // ---- List Games ----

    @Test
    void listGamesSuccess() throws DataAccessException {
        gameService.createGame(new GameService.CreateGameRequest(authToken, "Game1"));
        gameService.createGame(new GameService.CreateGameRequest(authToken, "Game2"));
        var result = gameService.listGames(new GameService.ListGameRequest(authToken));
        assertEquals(2, result.games().size());
    }

    @Test
    void listGamesBadAuthFails() {
        assertThrows(UnauthorizedException.class, () ->
                gameService.listGames(new GameService.ListGameRequest("bad-token")));
    }

    // ---- Join Game ----

    @Test
    void joinGameSuccessWhite() throws DataAccessException {
        int id = gameService.createGame(new GameService.CreateGameRequest(authToken, "JoinMe")).gameID();
        assertDoesNotThrow(() ->
                gameService.joinGame(new GameService.JoinGameRequest(authToken, "WHITE", id)));
    }

    @Test
    void joinGameColorAlreadyTakenFails() throws DataAccessException {
        int id = gameService.createGame(new GameService.CreateGameRequest(authToken, "Full")).gameID();
        gameService.joinGame(new GameService.JoinGameRequest(authToken, "WHITE", id));
        String token2 = userService.register(new UserService.RegisterUserRequest("player2", "pw", "q@q.com")).authToken();
        assertThrows(AlreadyTakenException.class, () ->
                gameService.joinGame(new GameService.JoinGameRequest(token2, "WHITE", id)));
    }

    @Test
    void joinGameNotFoundFails() {
        assertThrows(BadRequestException.class, () ->
                gameService.joinGame(new GameService.JoinGameRequest(authToken, "BLACK", 9999)));
    }

    @Test
    void joinGameBadAuthFails() throws DataAccessException {
        int id = gameService.createGame(new GameService.CreateGameRequest(authToken, "Auth")).gameID();
        assertThrows(UnauthorizedException.class, () ->
                gameService.joinGame(new GameService.JoinGameRequest("bad-token", "BLACK", id)));
    }
}
