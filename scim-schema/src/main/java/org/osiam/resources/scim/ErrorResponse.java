package org.osiam.resources.scim;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Arrays;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ErrorResponse {
    public static final String ERROR_URN = "urn:ietf:params:scim:api:messages:2.0:Error";
    private String[] schemas = {ERROR_URN};
    private String status;
    private String detail;

    public ErrorResponse(@JsonProperty("status") int statusCode, @JsonProperty("detail") String message) {
        status = Integer.toString(statusCode);
        detail = message;
    }

    public String[] getSchemas() {
        return Arrays.copyOf(schemas, schemas.length);
    }

    public String getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }
}
