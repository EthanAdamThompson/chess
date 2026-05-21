package server;

import com.google.gson.Gson;
import service.UserService;
import io.javalin.http.Context;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;


public class UserHandler {

    private final UserService userService;
    private final Gson gson = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context context) {
        try {
            var request = gson.fromJson(context.body(), UserService.RegisterUserRequest.class);
            context.status(200).json(userService.register(request));
        } catch (BadRequestException exception) {
            context.status(400).json(new ErrorResponse(exception.getMessage()));
        } catch (AlreadyTakenException exception) {
            context.status(403).json(new ErrorResponse(exception.getMessage()));
        } catch (Exception exception) {
            context.status(500).json(new ErrorResponse("Error: " + exception.getMessage()));
        }
    }

    public void login(Context context) {
        try {
            var request = gson.fromJson(context.body(), UserService.LoginUserRequest.class);
            context.status(200).json(userService.login(request));
        } catch (BadRequestException exception) {
            context.status(400).json(new ErrorResponse(exception.getMessage()));
        } catch (UnauthorizedException exception) {
            context.status(401).json(new ErrorResponse(exception.getMessage()));
        } catch (Exception exception) {
            context.status(500).json(new ErrorResponse("Error: " + exception.getMessage()));
        }
    }

    public void logout(Context context) {
        try {
            String authToken = context.header("authorization");
            userService.logout(new UserService.LogoutUserRequest(authToken));
            context.status(200).result("{}");
        } catch (UnauthorizedException exception) {
            context.status(401).json(new ErrorResponse(exception.getMessage()));
        } catch (Exception exception) {
            context.status(500).json(new ErrorResponse("Error: " + exception.getMessage()));
        }
    }


}
