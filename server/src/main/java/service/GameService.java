package service;

import dataaccess.*;
import model.GameData;

import java.util.List;

public class GameService {
    private final DataAccess dataAccess;
    public GameService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public record ListGameRequest(String authToken){}
    public record ListGameResult(List<GameData> games) {}

    public ListGameResult listGames(ListGameRequest req) throws DataAccessException{
        if(dataAccess.getAuth(req.authToken()) == null) {
            throw new UnauthorizedException("Error: Unauthorized");
        }
        return new ListGameResult(dataAccess.listGames());
    }
}
