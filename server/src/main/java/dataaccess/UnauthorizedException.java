package dataaccess;

/**
 * Thrown when an authToken is missing, invalid, or expired.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
