package org.osiam.resources.exception;

import java.net.URISyntaxException;

public class SCIMDataValidationException extends SCIMException{

    private static final long serialVersionUID = 7416418339453997681L;

    public SCIMDataValidationException(String message) {
        super(message);
    }
    
    public SCIMDataValidationException(String message, Throwable e) {
        super(message, e);
    }

}
