package client;

import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
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

}
