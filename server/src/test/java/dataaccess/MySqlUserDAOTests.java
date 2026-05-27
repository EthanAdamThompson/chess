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

}
