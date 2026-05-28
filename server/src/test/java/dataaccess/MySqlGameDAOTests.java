package dataaccess;

import model.GameData;
import org.junit.jupiter.api.*;

import java.util.List;

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
    //int createGame(String gameName) throws DataAccessException;
    // createGame - positive
    @Test
    @Order(1)
    void createGameSuccess() throws DataAccessException {
        int id = gameDAO.createGame("game1");
        assertTrue(id > 0);
    }
    // createGame - negative Should throw an exception
    @Test
    @Order(2)
    void createGameNoName() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(""));
    }
    //GameData getGame(int gameID) throws DataAccessException;
    // getGame - positive
    @Test
    @Order(3)
    void getGameSuccess() throws DataAccessException {
        int id = gameDAO.createGame("Game");
        var result = gameDAO.getGame(id);
        assertNotNull(result);
        assertEquals("Game", result.gameName());
    }
    // getGame - negative should throw exception
    @Test
    @Order(4)
    void getGameNotFound() throws DataAccessException {
        var result = gameDAO.getGame(99999);
        assertNull(result);
    }
    //List<GameData> listGames() throws DataAccessException;
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
    //void updateGame(GameData game) throws DataAccessException;
    @Test
    @Order(7)
    void deleteAuthSuccess() throws DataAccessException {
        var auth = new AuthData("tokenMEXICO", "chris");
        authDAO.createAuth(auth);
        authDAO.deleteAuth("tokenMEXICO");
        assertNull(authDAO.getAuth("tokenMEXICO"));
    }
    // deleteAuth - negative should throw exception (nothing to delete)
    @Test
    @Order(8)
    void deleteAuthNotFound() throws DataAccessException {
        assertDoesNotThrow(() -> authDAO.deleteAuth("nonexistent"));

    }
}
