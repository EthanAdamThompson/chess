package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
public class MySqlGameDAOTests {
    private static MySqlGameDAO gameDAO;
    private static MySqlDataAccess dataAccess;

    @BeforeAll
    static void setup() throws DataAccessException {
        dataAccess = new MySqlDataAccess();
        gameDAO = new MySqlGameDAO();
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
        var auth = new AuthData("tokenAMERICA", "alice");
        assertDoesNotThrow(() -> authDAO.createAuth(auth));
        assertNotNull(authDAO.getAuth("tokenAMERICA"));
    }
    // createAuth - negative Should throw an exception
    @Test
    @Order(2)
    void createAuthDuplicate() throws DataAccessException {
        var auth = new AuthData("tokenAMERICA", "alice");
        authDAO.createAuth(auth);
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth));
    }
    // AuthData getAuth(String authToken) throws DataAccessException;
    // getAuth - positive
    @Test
    @Order(3)
    void getAuthSuccess() throws DataAccessException {
        var auth = new AuthData("tokenCANADA", "bobby");
        authDAO.createAuth(auth);
        var result = authDAO.getAuth("tokenCANADA");
        assertNotNull(result);
        assertEquals("bobby", result.username());
    }
    // getAuth - negative should throw exception
    @Test
    @Order(4)
    void getAuthNotFound() throws DataAccessException {
        var result = authDAO.getAuth("nonexistent");
        assertNull(result);
    }
    // void deleteAuth(String authToken) throws DataAccessException;
    // deleteAuth
    @Test
    @Order(5)
    void deleteAuthSuccess() throws DataAccessException {
        var auth = new AuthData("tokenMEXICO", "chris");
        authDAO.createAuth(auth);
        authDAO.deleteAuth("tokenMEXICO");
        assertNull(authDAO.getAuth("tokenMEXICO"));
    }
    // deleteAuth - negative should throw exception (nothing to delete)
    @Test
    @Order(6)
    void deleteAuthNotFound() throws DataAccessException {
        assertDoesNotThrow(() -> authDAO.deleteAuth("nonexistent"));

    }
}
