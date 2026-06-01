package client;

import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws Exception {
        // clear the database between each test so they don't interfere
        facade.clear();
    }

    // Testing for Register
    @Test
    void registerSuccess() throws Exception {
        var auth = facade.register("Bobby", "password1234", "bobby@email.com");
        assertNotNull(auth.authToken());
        assertTrue(auth.authToken().length() > 10);
    }

    @Test
    void registerDuplicateUsername() throws Exception {
        facade.register("Alexa", "passwordabcd", "alexa@email.com");
        assertThrows(Exception.class, () -> facade.register("Alexa", "password2", "alexa2@email.com"));
    }

    // Login tests
    @Test
    void loginSuccess() throws Exception{
        facade.register("Alexa", "password1234", "alexa@email.com");
        var auth = facade.login("Alexa", "password1234");
        assertNotNull(auth.authToken());
        assertEquals("Alexa", auth.username());
    }

    @Test
    void loginWrongPassword() throws Exception {
        facade.register("Bobby", "passwordabcd", "bobby@email.com");
        assertThrows(Exception.class, () -> facade.login("Bobby", "password1234"));
    }

    @Test
    void loginNonexistentUser() {
        assertThrows(Exception.class, () -> facade.login("nobody", "password"));
    }

    // Logout Tests

    @Test
    void logoutSuccess() throws Exception {
        var auth = facade.register("Chris", "password1234", "chris@email.com");
        assertDoesNotThrow(() -> facade.logout(auth.authToken()));
    }

    @Test
    void logoutInvalidToken() {
        assertThrows(Exception.class, () -> facade.logout("12345"));
    }

    // Create Game Tests
    @Test
    void createGameSuccess() throws Exception {
        var auth = facade.register("David", "password123", "david@email.com");
        int gameID = facade.createGame("testgame", auth.authToken());
        assertTrue(gameID > 0);
    }

    @Test
    void createGameNoAuth() {
        assertThrows(Exception.class, () -> facade.createGame("testgame", null));
    }

    // List Game Tests
    @Test
    void listGamesEmpty() throws Exception {
        var auth = facade.register("Alexa", "password1234", "alexa@email.com");
        var games = facade.listGames(auth.authToken());
        assertEquals(0, games.length);
    }

    @Test
    void listGamesWithGames() throws Exception {
        var auth = facade.register("Alexa", "password1233", "p1@email.com");
        facade.createGame("game1", auth.authToken());
        facade.createGame("game2", auth.authToken());
        var games = facade.listGames(auth.authToken());
        assertEquals(2, games.length);
    }

    @Test
    void listGamesInvalidAuth() {
        assertThrows(Exception.class, () -> facade.listGames("fake-token"));
    }

    // Join Game tests
    @Test
    void joinGameAsWhite() throws Exception {
        var auth = facade.register("Alexa", "password1234", "alexa@email.com");
        int gameID = facade.createGame("testgame", auth.authToken());
        assertDoesNotThrow(() -> facade.joinGame(gameID, "WHITE", auth.authToken()));
    }

    @Test
    void joinGameAsBlack() throws Exception {
        var auth = facade.register("Bobby", "password1234", "bobby@email.com");
        int gameID = facade.createGame("testgame", auth.authToken());
        assertDoesNotThrow(() -> facade.joinGame(gameID, "BLACK", auth.authToken()));
    }

    @Test
    void joinGameColorAlreadyTaken() throws Exception {
        var auth1 = facade.register("Alexa", "password1234", "alexa@email.com");
        var auth2 = facade.register("Bobby", "passwordabcd", "bobby@email.com");
        int gameID = facade.createGame("testgame", auth1.authToken());
        facade.joinGame(gameID, "WHITE", auth1.authToken());
        assertThrows(Exception.class, () -> facade.joinGame(gameID, "WHITE", auth2.authToken()));
    }

    @Test
    void joinGameInvalidID() throws Exception {
        var auth = facade.register("Chris", "password1234", "chris@email.com");
        assertThrows(Exception.class, () -> facade.joinGame(99999, "WHITE", auth.authToken()));
    }

    @Test
    void joinGameInvalidAuth() throws Exception {
        var auth = facade.register("David", "password1234", "david@email.com");
        int gameID = facade.createGame("testgame", auth.authToken());
        assertThrows(Exception.class, () ->
                facade.joinGame(gameID, "WHITE", "fake-token"));
    }
}
