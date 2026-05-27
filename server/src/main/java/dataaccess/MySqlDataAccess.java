package dataaccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import java.util.List;

// Wrapper to deal with all the MySql files
public class MySqlDataAccess implements DataAccess {
    private final MySqlUserDAO userDAO;
    private final MySqlAuthDAO authDAO;
    private final MySqlGameDAO gameDAO;

    public MySqlDataAccess() throws DataAccessException {
        userDAO = new MySqlUserDAO();
        authDAO = new MySqlAuthDAO();
        gameDAO = new MySqlGameDAO();
    }

    @Override
    public void clear() throws DataAccessException {
        for (var table : new String[]{"users", "auth", "games"}) {
            var sql = "TRUNCATE TABLE " + table;
            try (var conn = DatabaseManager.getConnection();
                 var ps = conn.prepareStatement(sql)) {
                ps.executeUpdate();
            } catch (Exception exception) {
                throw new DataAccessException(exception.getMessage());
            }
        }
    }
    // userDAO wrapper
    @Override
    public void createUser(UserData user) throws DataAccessException { userDAO.createUser(user); }
    @Override
    public UserData getUser(String username) throws DataAccessException { return userDAO.getUser(username); }
    // authDAO wrapper
    @Override
    public void createAuth(AuthData auth) throws DataAccessException { authDAO.createAuth(auth); }
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException { return authDAO.getAuth(authToken); }
    @Override
    public void deleteAuth(String authToken) throws DataAccessException { authDAO.deleteAuth(authToken); }
    // gameDao Wrapper
    @Override
    public int createGame(String gameName) throws DataAccessException { return gameDAO.createGame(gameName); }
    @Override
    public GameData getGame(int gameID) throws DataAccessException { return gameDAO.getGame(gameID); }
    @Override
    public List<GameData> listGames() throws DataAccessException { return gameDAO.listGames(); }
    @Override
    public void updateGame(GameData game) throws DataAccessException { gameDAO.updateGame(game); }
}
