package dataaccess;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;

public class MySqlUserDAO {
    public MySqlUserDAO() throws DataAccessException {
        configureDatabase();
    }
}
