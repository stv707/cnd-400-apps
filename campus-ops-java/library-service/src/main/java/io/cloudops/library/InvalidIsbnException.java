package io.cloudops.library;

/** Raised when a supplied ISBN does not match basic structural validation. */
public class InvalidIsbnException extends RuntimeException {

    /**
     * Creates a new InvalidIsbnException.
     *
     * @param message descriptive error message
     */
    public InvalidIsbnException(final String message) {
        super(message);
    }
}
