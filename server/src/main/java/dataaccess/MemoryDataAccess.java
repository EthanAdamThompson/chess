package dataaccess;

// import necessary files
import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

//Import necessary Java things
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryDataAccess implements DataAccess{
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private final Map<String, AuthData> auths = new HashMap<>();
    private final AtomicInteger nextGameID = new AtomicInteger(1);

    // for users
    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("User already exists: " + user.username());
        }
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    // for games
    @Override
    public int createGame(String gameName) throws DataAccessException {
        if (gameName == null || gameName.isBlank()) {
            throw new DataAccessException("Game name cannot be empty");
        }
        int id = nextGameID.getAndIncrement();
        games.put(id, new GameData(id, null, null, gameName, new ChessGame()));
        return id;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())) {
            throw new DataAccessException("Game not found: " + game.gameID());
        }
        games.put(game.gameID(), game);
    }

    // For Authentification

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }

    // Clear

    @Override
    public void clear() throws DataAccessException {
        users.clear();
        games.clear();
        auths.clear();
    }
}
