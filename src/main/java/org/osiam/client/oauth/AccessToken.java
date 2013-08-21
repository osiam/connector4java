package org.osiam.client.oauth;
/*
 * for licensing see the file license.txt.
 */

import java.util.Date;
import java.util.Objects;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Objects of this type  represent an access token. Access tokens are granted by the OSIAM server
 * and allows access to restricted resources.
 */
public class AccessToken {

    private static final int MILLIS = 1000;
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
     * @return Nothing at this point.
     * @throws NoSuchMethodError
     */
    public String getRefreshToken() {
        throw new NoSuchMethodError("Not yet implemented");
    }

    /**
     * type of the access token
     *
     * @return the type of the access token
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

    /**
     * checks if the time the access token will be valid are over
     *
     * @return true if the access token is not valid anymore
     */
    public boolean isExpired() {
        long now = new Date().getTime();
        return now > retrievedOn + (long) expiresIn * MILLIS;
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

        returnToken.append("[access_token = ").append(token).
                append(", token_type = ").append(type).
                append(", scope = ").append(scope).
                append(", expired = ").append(isExpired()).
                append("]");

        return returnToken.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {  // NOSONAR - stmt in if is correct
            return true;
        }
        
        if (o == null || getClass() != o.getClass()) {  // NOSONAR - stmt in if is correct
            return false;
        }

        AccessToken that = (AccessToken) o;

        if (expiresIn != that.expiresIn) { // NOSONAR - stmt in if is correct
            return false;
        }
        if (!refreshToken.equals(that.refreshToken)) { // NOSONAR - stmt in if is correct
            return false;
        }
        if (!scope.equals(that.scope)) { // NOSONAR - stmt in if is correct
            return false;
        }
        if (!token.equals(that.token)) { // NOSONAR - stmt in if is correct
            return false;
        }
        if (!type.equals(that.type)) { // NOSONAR - stmt in if is correct
            return false;
        }
        
        return this.isExpired() == that.isExpired();
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, type, expiresIn, scope, refreshToken, this.isExpired());
    }
}
