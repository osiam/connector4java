package org.osiam.client.exception;

public class ConnectionInitializationException extends RuntimeException {
    ConnectionInitializationException(String message) {
        super(message);
    }

    public ConnectionInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}