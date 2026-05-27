package dataaccess;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;

public class MySqlUserDAO {
    public MySqlUserDAO() throws DataAccessException {
        configureDatabase();
    }
    private final String[] createStatements = {
            """
        CREATE TABLE IF NOT EXISTS users (
            username VARCHAR(50) NOT NULL PRIMARY KEY,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(100) NOT NULL
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
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
