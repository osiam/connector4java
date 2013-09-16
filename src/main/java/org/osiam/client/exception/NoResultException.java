package org.osiam.client.exception;
/*
 * for licensing see the file license.txt.
 */

/**
 * Thrown when a query for a given ID doesn't return any results.
 */
public class NoResultException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoResultException(String message) {
        super(message);
    }
}
