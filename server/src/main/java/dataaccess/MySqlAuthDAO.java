package dataaccess;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class MySqlAuthDAO {
    public MySqlAuthDAO() throws DataAccessException {
        configureDatabase();
    }
    private final String[] createStatements = {
            """
        CREATE TABLE IF NOT EXISTS users (
            authToken VARCHAR(255) NOT NULL PRIMARY KEY,
            username VARCHAR(50) NOT NULL
        )
        """
    };

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

    // void createAuth(AuthData auth) throws DataAccessException;
    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        var sql = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, auth.authToken());
            ps.setString(2, auth.username());
            ps.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException(exception.getMessage());
        }
    }
    // AuthData getAuth(String authToken) throws DataAccessException;
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
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
    // void deleteAuth(String authToken) throws DataAccessException;
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
