package org.osiam.client.exception;

public class OsiamClientConnectionException extends OsiamClientException {

    private static final long serialVersionUID = 2773048845284685144L;
    private final int httpStatusCode;

    public OsiamClientConnectionException(int httpStatusCode, String message) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
