package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;

public interface DataAccess {

    // Clear
    void clear() throws DataAccessException;

    // For Authenticating
    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authTokne) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;

    //Creating the User
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;




}
