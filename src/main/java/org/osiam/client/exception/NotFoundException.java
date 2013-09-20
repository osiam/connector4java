package org.osiam.client.exception;
/*
 * for licensing see the file license.txt.
 */

/**
 * Thrown when a User or a Group you want to update doesn't exist
 */
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) {
        super(message);
    }
}
