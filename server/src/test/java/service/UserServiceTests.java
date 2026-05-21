package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    private UserService userService;

    @BeforeEach
    void setUp() throws Exception {
        userService = new UserService(new MemoryDataAccess());
    }

    // Register the User

    @Test
    void registerSuccess() throws DataAccessException {
        var result = userService.register(new UserService.RegisterUserRequest("alice", "pw123", "alice@example.com"));
        assertEquals("alice", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void registerDuplicateUsernameFails() throws DataAccessException {
        userService.register(new UserService.RegisterUserRequest("alice", "pw123", "alice@example.com"));
        assertThrows(AlreadyTakenException.class, () ->
                userService.register(new UserService.RegisterUserRequest("alice", "other", "other@example.com")));
    }

    @Test
    void registerMissingFieldFails() {
        assertThrows(BadRequestException.class, () ->
                userService.register(new UserService.RegisterUserRequest("", "pw", "e@e.com")));
    }

    // Login the user

    @Test
    void loginSuccess() throws DataAccessException {
        userService.register(new UserService.RegisterUserRequest("bob", "secret", "bob@example.com"));
        var result = userService.login(new UserService.LoginUserRequest("bob", "secret"));
        assertEquals("bob", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void loginWrongPasswordFails() throws DataAccessException {
        userService.register(new UserService.RegisterUserRequest("bob", "secret", "bob@example.com"));
        assertThrows(UnauthorizedException.class, () ->
                userService.login(new UserService.LoginUserRequest("bob", "wrong")));
    }

    @Test
    void loginUnknownUserFails() {
        assertThrows(UnauthorizedException.class, () ->
                userService.login(new UserService.LoginUserRequest("nobody", "pw")));
    }

    // Log Out the user
    @Test
    void logoutSuccess() throws DataAccessException {
        var reg = userService.register(new UserService.RegisterUserRequest("carol", "pw", "c@c.com"));
        assertDoesNotThrow(() -> userService.logout(new UserService.LogoutUserRequest(reg.authToken())));
    }

    @Test
    void logoutInvalidTokenFails() {
        assertThrows(UnauthorizedException.class, () ->
                userService.logout(new UserService.LogoutUserRequest("bad-token")));
    }
}
