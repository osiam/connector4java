package org.osiam.client.exception;

/**
 * will be thrown if the actual session is not authorized to access the OSIAM service.
 * For example if the access token is not valid anymore
 *
 */
public class UnauthorizedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnauthorizedException(String message) {
        super(message);
    }
}