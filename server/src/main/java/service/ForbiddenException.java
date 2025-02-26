package service;

/**
 * Indicates there was an error connecting to the database
 */
public class ForbiddenException extends Exception{
    public ForbiddenException(String message) {
        super(message);
    }
}
