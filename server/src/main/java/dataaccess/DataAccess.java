package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;

public interface DataAccess extends UserDAO, AuthDAO, GameDAO {
    // Clear
    void clear() throws DataAccessException;
}
