package dataaccess;

/**
 * Thrown when attempting to create a resource that already exists,
 * e.g. registering a username that is taken.
 */
public class AlreadyTakenException extends DataAccessException {
    public AlreadyTakenException(String message) {
        super(message);
    }
}