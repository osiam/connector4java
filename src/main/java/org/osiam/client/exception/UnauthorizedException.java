package org.osiam.client.exception;
/*
 * for licensing see in the license.txt
 */

/**
 * This exception is thrown if the current session is not authorized to access the OSIAM service,
 * For example if the access token expired.
 */
public class UnauthorizedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnauthorizedException(String message) {
        super(message);
    }
}