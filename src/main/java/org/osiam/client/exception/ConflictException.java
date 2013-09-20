package org.osiam.client.exception;
/*
 * for licensing see the file license.txt.
 */

/**
 * Thrown if a conflict happens while creating/deleting/modify a User or a Group
 */
public class ConflictException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ConflictException(String message) {
        super(message);
    }
}