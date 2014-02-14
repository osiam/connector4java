/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.client.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Objects;

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

    /**
     * Retrieve the refresh token for this access token
     * @return The refresh token as String
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public String toString() {
        StringBuilder returnToken = new StringBuilder();

        returnToken.append("[access_token = ").append(token).
                append(", token_type = ").append(type).
                append(", scope = ").append(scope).
                append(", expired = ").append(isExpired()).
                append(", refresh_token = ").append(refreshToken).
                append("]");

        return returnToken.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AccessToken that = (AccessToken) o;

        if (expiresIn != that.expiresIn) {
            return false;
        }
        if (!refreshToken.equals(that.refreshToken)) {
            return false;
        }
        if (!scope.equals(that.scope)) {
            return false;
        }
        if (!token.equals(that.token)) {
            return false;
        }
        if (!type.equals(that.type)) {
            return false;
        }

        return this.isExpired() == that.isExpired();
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, type, expiresIn, scope, refreshToken, this.isExpired());
    }
}
