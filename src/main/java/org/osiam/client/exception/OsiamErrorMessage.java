package org.osiam.client.exception;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

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
