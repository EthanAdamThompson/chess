package service;

import dataaccess.*;
import model.GameData;
import model.AuthData;

import java.util.List;

public class GameService {
    private final DataAccess dataAccess;
    public GameService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    // Create a list of all the games
    public record ListGameRequest(String authToken){}
    public record ListGameResult(List<GameData> games) {}

    public ListGameResult listGames(ListGameRequest req) throws DataAccessException{
        if(dataAccess.getAuth(req.authToken()) == null) {
            throw new UnauthorizedException("Error: Unauthorized");
        }
        return new ListGameResult(dataAccess.listGames());
    }

    //Create a game
    public record CreateGameRequest(String authToken, String gameName){}
    public record CreateGameResult(int gameID){}
    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException{
        int ID = dataAccess.createGame(request.gameName());
        return new CreateGameResult(ID);
    }

    // Join a game

    public record JoinGameRequest(String authToken, String playerColor, int gameID){}
    public void JoinGame(JoinGameRequest request) throws DataAccessException{
        // Need some logic here about how to go about adding joining the game.
    }
}
