package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    // Register the user
    public record RegisterUserRequest(String username, String password, String email) {}
    public record RegisterUserResult(String username, String authToken) {}
    public RegisterUserResult register(RegisterUserRequest request) throws DataAccessException {
        if (request.username() == null || request.username().isBlank() || request.password() == null
                || request.password().isBlank() || request.email() == null || request.email().isBlank()) {
            throw new BadRequestException("Error: bad request");
        }
        if (dataAccess.getUser(request.username()) != null) {
            throw new AlreadyTakenException("Error: already taken");
        }
        dataAccess.createUser(new UserData(request.username(), request.password(), request.email()));
        String token = UUID.randomUUID().toString();
        dataAccess.createAuth(new AuthData(token, request.username()));
        return new RegisterUserResult(request.username(), token);
    }

    // User Login
    public record LoginUserRequest(String username, String password) {}
    public record LoginUserResult(String username, String authToken) {}
    public LoginUserResult login(LoginUserRequest request) throws DataAccessException {
        if (request.username() == null || request.username().isBlank() || request.password() == null || request.password().isBlank()) {
            throw new BadRequestException("Error: bad request");
        }
        UserData user = dataAccess.getUser(request.username());
        if (user == null || !BCrypt.checkpw(request.password(), user.password())) {
            throw new UnauthorizedException("Error: Unauthorized");
        }
        String token = UUID.randomUUID().toString();
        dataAccess.createAuth(new AuthData(token, request.username()));
        return new LoginUserResult(request.username(), token);
    }

    // User Logout
    public record LogoutUserRequest(String authToken){}
    public void logout(LogoutUserRequest request) throws DataAccessException{
        if(dataAccess.getAuth(request.authToken()) == null){
            throw new UnauthorizedException("Error: Unauthorized");
        }
        dataAccess.deleteAuth(request.authToken);
    }

}
