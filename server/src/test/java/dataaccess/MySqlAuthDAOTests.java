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

    // createUser - positive
    @Test
    @Order(1)
    void createUserSuccess() throws DataAccessException {
        var user = new UserData("alice", "password123", "alice@example.com");
        assertDoesNotThrow(() -> authDAO.createUser(user));
        assertNotNull(authDAO.getUser("alice"));
    }
    // createUser - negative Should throw an exception
    @Test
    @Order(2)
    void createUserDuplicate() throws DataAccessException {
        var user = new UserData("alice", "password123", "alice@example.com");
        authDAO.createUser(user);
        assertThrows(DataAccessException.class, () -> authDAO.createUser(user));
    }
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
}
