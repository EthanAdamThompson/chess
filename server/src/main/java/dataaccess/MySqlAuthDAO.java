package dataaccess;
import model.AuthData;
import java.sql.SQLException;

public class MySqlAuthDAO {
    public MySqlAuthDAO() throws DataAccessException {
        configureDatabase();
    }
}
