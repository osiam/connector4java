package org.osiam.client.exception;
/*
 * for licensing see the file license.txt.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * Only used to serialize given error json string to extract the error message.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class OsiamErrorMessage {

    @JsonProperty("error_code")
    private String errorCode;
    private String description;

    public String getErrorCode(){
        return errorCode;
    }

    public String getDescription(){
        return description;
    }
}
