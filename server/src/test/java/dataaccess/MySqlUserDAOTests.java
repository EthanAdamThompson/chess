package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MySqlUserDAOTests {
    private static MySqlUserDAO userDAO;
    private static MySqlDataAccess dataAccess;

    @BeforeAll
    static void setup() throws DataAccessException {
        dataAccess = new MySqlDataAccess();
        userDAO = new MySqlUserDAO();
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
        assertDoesNotThrow(() -> userDAO.createUser(user));
        assertNotNull(userDAO.getUser("alice"));
    }
    // createUser - negative Should throw an exception
    @Test
    @Order(2)
    void createUserDuplicate() throws DataAccessException {
        var user = new UserData("alice", "password123", "alice@example.com");
        userDAO.createUser(user);
        assertThrows(DataAccessException.class, () -> userDAO.createUser(user));
    }
    // getUser - positive
    @Test
    @Order(3)
    void getUserSuccess() throws DataAccessException {
        var user = new UserData("bob", "pass456", "bob@example.com");
        userDAO.createUser(user);
        var result = userDAO.getUser("bob");
        assertNotNull(result);
        assertEquals("bob", result.username());
    }
    // getUser - negative
    @Test
    @Order(4)
    void getUserNotFound() throws DataAccessException {
        var result = userDAO.getUser("nonexistent");
        assertNull(result);
    }


}
