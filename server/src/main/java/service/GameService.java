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

    public ListGameResult listGames(ListGameRequest request) throws DataAccessException{
        if(dataAccess.getAuth(request.authToken()) == null) {
            throw new UnauthorizedException("Error: Unauthorized");
        }
        return new ListGameResult(dataAccess.listGames());
    }

    //Create a game
    public record CreateGameRequest(String authToken, String gameName){}
    public record CreateGameResult(int gameID){}
    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException{
        if(dataAccess.getAuth(request.authToken()) == null){
            throw new UnauthorizedException("Error: Unauthorized");
        }
        if(request.gameName() == null || request.gameName().isBlank()){
            throw new BadRequestException("Error: Bad Request");
        }
        int ID = dataAccess.createGame(request.gameName());
        return new CreateGameResult(ID);
    }

    // Join a game
    public record JoinGameRequest(String authToken, String playerColor, int gameID){}
    public void JoinGame(JoinGameRequest request) throws DataAccessException{
        // Variables
        AuthData auth = dataAccess.getAuth(request.authToken());
        GameData game = dataAccess.getGame(request.gameID());
        // Start with Error handling (is it a bad request, unauthorized
        if (request.playerColor() == null || (!request.playerColor().equalsIgnoreCase("WHITE")
                && !request.playerColor().equalsIgnoreCase("BLACK"))) {
            throw new BadRequestException("Error: bad request");
        }
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        if (game == null) {
            throw new BadRequestException("Error: bad request");
        }
        // Then assign names to the different colors (check to see if the name is already taken)
        String username = auth.username();
        GameData updated;
        if (request.playerColor().equalsIgnoreCase("WHITE")) {
            if (game.whiteUsername() != null){
                throw new AlreadyTakenException("Error: already taken");
            }
            updated = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else {
            if (game.blackUsername() != null) throw new AlreadyTakenException("Error: already taken");
            updated = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        }
        // End by updating Game
        dataAccess.updateGame(updated);
    }
}
