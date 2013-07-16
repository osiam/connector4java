package org.osiam.client.exception;

/**
 * will be thrown if the connection to the given OSIAM server could not be opened
 *
 */
public class ConnectionInitializationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	ConnectionInitializationException(String message) {
        super(message);
    }

    public ConnectionInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}