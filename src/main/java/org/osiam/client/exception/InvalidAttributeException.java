package org.osiam.client.exception;
/*
 * for licensing see the file license.txt.
 */

/**
 * Thrown when a invalid attribute is set to one of the Builder
 */
public class InvalidAttributeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidAttributeException(String message) {
        super(message);
    }
}
