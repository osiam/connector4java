package org.osiam.client.exception;
/*
 * for licensing see the file license.txt.
 */

/**
 * This exception is thrown, if a connection to the OSIAM service can not be created.
 */
public class ConnectionInitializationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ConnectionInitializationException(String message) {
        super(message);
    }

    public ConnectionInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}