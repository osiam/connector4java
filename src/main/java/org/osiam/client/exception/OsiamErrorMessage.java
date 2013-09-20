package org.osiam.client.exception;
/*
 * for licensing see the file license.txt.
 */

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;


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
