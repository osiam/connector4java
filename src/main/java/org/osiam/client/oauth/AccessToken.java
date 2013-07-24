package org.osiam.client.oauth;
/*
 * for licensing see in the license.txt
 */

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

/**
 * This class holds information about an access token. An  access token is granted by the OSIAM server
 * and allows access to restricted resources.
 */
public class AccessToken {

    @JsonProperty("access_token")
    private String token;
    @JsonProperty("token_type")
    private String type;
    @JsonProperty("expires_in")
    private int expiresIn;
    @JsonProperty
    private String scope;
    @JsonProperty("refresh_token")
    private String refreshToken;

    /* This might not be the smartest idea, but anyway */
    private long retrievedOn = new Date().getTime();

    /**
     * Retrieve the string value of the access token used to authenticate against the provider.
     *
     * @return The access token string
     */
    public String getToken() {
        return token;
    }

    /**
     * Refreshing of tokens is not yet implemented in OSIAM
     *
     * @return
     * @throws NoSuchMethodError
     */
    public String getRefreshToken() {
        throw new NoSuchMethodError("Not yet implemented");
    }

    /**
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * The number of seconds this access token is valid from the time it was retrieved.
     *
     * @return The number of seconds this access token is valid.
     */
    public int getExpiresIn() {
        return expiresIn;
    }

    public boolean isExpired() {
        long now = new Date().getTime();
        return now > retrievedOn + expiresIn * 1000;
    }

    /**
     * Retrieve the possible Scopes of this AccessToken
     *
     * @return The scopes as string
     */
    public String getScope() {
        return scope;
    }

    @Override
    public String toString() {
        StringBuilder returnToken = new StringBuilder();

        returnToken.append("AccessToken [").
                append("access_token = ").append(token).
                append(", token_type = ").append(type).
                append(", scope = ").append(scope).
                append(", expired = ").append(isExpired()).
                append("]");

        return returnToken.toString();
    }
}
