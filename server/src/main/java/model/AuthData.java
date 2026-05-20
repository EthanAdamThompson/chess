package model;

//Using record because it is much shorter and simpler than class
public record AuthData(String authToken, String username) {
}
