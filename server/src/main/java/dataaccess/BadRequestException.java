package dataaccess;

/**
 * Thrown when a request is missing required fields or contains invalid data.
 */
public class BadRequestException extends DataAccessException {
    public BadRequestException(String message) {
        super(message);
    }
}
