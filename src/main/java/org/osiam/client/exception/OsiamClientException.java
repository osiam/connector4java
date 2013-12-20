package org.osiam.client.exception;

public class OsiamClientException extends RuntimeException {

    private static final long serialVersionUID = 5047126186987317227L;

    public OsiamClientException(String message) {
        super(message);
    }

    public OsiamClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
