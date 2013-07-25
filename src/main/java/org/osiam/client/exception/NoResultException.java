package org.osiam.client.exception;
/*
 * for licensing see in the license.txt
 */

/**
 * This exception is thrown when a query does not return any results.
 */
public class NoResultException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoResultException(String message) {
        super(message);
    }
}
