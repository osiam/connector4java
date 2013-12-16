package org.osiam.client.exception;

public class OsiamClientException extends RuntimeException {
    public OsiamClientException(String message) {
        super(message);
    }

    public OsiamClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
