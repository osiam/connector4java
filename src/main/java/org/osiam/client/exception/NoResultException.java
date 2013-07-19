package org.osiam.client.exception;

/**
 * The NoResultException is thrown when a query does not return any results.
 */
public class NoResultException extends RuntimeException {

    public NoResultException(String message) {
        super(message);
    }
}
