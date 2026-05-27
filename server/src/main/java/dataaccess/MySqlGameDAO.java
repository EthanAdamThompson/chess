package dataaccess;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlGameDAO {
    private final Gson gson = new Gson();

    public MySqlGameDAO() throws DataAccessException {
        configureDatabase();
    }
    // Creat the string to display for the statements
    private final String[] createStatements = {
            """
        CREATE TABLE IF NOT EXISTS games (
            gameID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
            whiteUsername VARCHAR(50),
            blackUsername VARCHAR(50),
            gameName VARCHAR(100) NOT NULL,
            game TEXT NOT NULL
        )
        """
    };
    // Identical to User
    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException(exception.getMessage());
        }
    }

    // Copy and Pasted from MySqlUserDAO
    @Override
    public void createGame(String gameName) throws DataAccessException {
        var sql = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.username());
            ps.setString(2, hashed);
            ps.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException("User already exists: " + user.username());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var sql = "SELECT username, password, email FROM users WHERE username=?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return new UserData(rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"));
            }
            return null;
        } catch (SQLException exception) {
            throw new DataAccessException(exception.getMessage());
        }
    }
}
