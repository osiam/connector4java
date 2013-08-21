package org.osiam.client.exception;

public class InvalidAttributeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidAttributeException(String message) {
        super(message);
    }
}
