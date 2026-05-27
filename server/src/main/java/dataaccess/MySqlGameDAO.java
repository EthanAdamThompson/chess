package dataaccess;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
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
}
