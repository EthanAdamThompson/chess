package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
public class MySqlAuthDAOTests {
    private static MySqlAuthDAO authDAO;
    private static MySqlDataAccess dataAccess;

    @BeforeAll
    static void setup() throws DataAccessException {
        dataAccess = new MySqlDataAccess();
        authDAO = new MySqlAuthDAO();
    }

    @BeforeEach
    void clear() throws DataAccessException {
        dataAccess.clear();
    }
    // void createAuth(AuthData auth) throws DataAccessException;

    // createAuth - positive
    @Test
    @Order(1)
    void createAuthSuccess() throws DataAccessException {
        var auth = new AuthData("token123", "alice");
        assertDoesNotThrow(() -> authDAO.createAuth(auth));
        assertNotNull(authDAO.getAuth("alice"));
    }
    // createAuth - negative Should throw an exception
    @Test
    @Order(2)
    void createUserDuplicate() throws DataAccessException {
        var auth = new AuthData("token123", "alice");
        authDAO.createAuth(auth);
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth));
    }
    // AuthData getAuth(String authToken) throws DataAccessException;
    // getUser - positive
    @Test
    @Order(3)
    void getUserSuccess() throws DataAccessException {
        var user = new UserData("bob", "pass456", "bob@example.com");
        authDAO.createUser(user);
        var result = authDAO.getUser("bob");
        assertNotNull(result);
        assertEquals("bob", result.username());
    }
    // getUser - negative should throw exception
    @Test
    @Order(4)
    void getUserNotFound() throws DataAccessException {
        var result = authDAO.getUser("nonexistent");
        assertNull(result);
    }
    // void deleteAuth(String authToken) throws DataAccessException;
    @Test
    @Order(3)
    void getUserSuccess() throws DataAccessException {
        var user = new UserData("bob", "pass456", "bob@example.com");
        authDAO.createUser(user);
        var result = authDAO.getUser("bob");
        assertNotNull(result);
        assertEquals("bob", result.username());
    }
    // getUser - negative should throw exception
    @Test
    @Order(4)
    void getUserNotFound() throws DataAccessException {
        var result = authDAO.getUser("nonexistent");
        assertNull(result);
    }
}
