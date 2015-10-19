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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Strings;
import org.osiam.client.exception.InvalidAttributeException;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class Client {

    @JsonProperty
    private String id;

    @JsonProperty
    private int accessTokenValiditySeconds;

    @JsonProperty
    private int refreshTokenValiditySeconds;

    @JsonProperty
    private String redirectUri;

    @JsonProperty("client_secret")
    private String clientSecret;

    @JsonProperty("scope")
    private Set<String> scopes;

    @JsonProperty
    private Set<String> grants;

    @JsonProperty
    private boolean implicit;

    @JsonProperty
    private long validityInSeconds;

    private Client() {
    }

    public Client(Builder builder) {
        id = builder.clientId;
        accessTokenValiditySeconds = builder.accessTokenValiditySeconds;
        refreshTokenValiditySeconds = builder.refreshTokenValiditySeconds;
        redirectUri = builder.redirectUri;
        clientSecret = builder.clientSecret;
        scopes = builder.scopes;
        grants = builder.grants;
        implicit = builder.implicit;
        validityInSeconds = builder.validityInSeconds;
    }

    public String getId() {
        return id;
    }

    public int getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public int getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public Set<String> getGrants() {
        return grants;
    }

    public boolean isImplicit() {
        return implicit;
    }

    public long getValidityInSeconds() {
        return validityInSeconds;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id='" + id + '\'' +
                ", accessTokenValiditySeconds=" + accessTokenValiditySeconds +
                ", refreshTokenValiditySeconds=" + refreshTokenValiditySeconds +
                ", redirectUri='" + redirectUri + '\'' +
                ", scopes=" + scopes +
                ", grants=" + grants +
                ", implicit=" + implicit +
                ", validityInSeconds=" + validityInSeconds +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(accessTokenValiditySeconds, client.accessTokenValiditySeconds) &&
                Objects.equals(refreshTokenValiditySeconds, client.refreshTokenValiditySeconds) &&
                Objects.equals(implicit, client.implicit) &&
                Objects.equals(validityInSeconds, client.validityInSeconds) &&
                Objects.equals(id, client.id) &&
                Objects.equals(redirectUri, client.redirectUri) &&
                Objects.equals(clientSecret, client.clientSecret) &&
                Objects.equals(scopes, client.scopes) &&
                Objects.equals(grants, client.grants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accessTokenValiditySeconds, refreshTokenValiditySeconds, redirectUri, clientSecret,
                scopes, grants, implicit, validityInSeconds);
    }

    /**
     * The Builder class is used to construct instances of the {@link Client}.
     */
    public static class Builder {

        private String clientId;
        private String clientSecret;
        private int accessTokenValiditySeconds;
        private int refreshTokenValiditySeconds;
        private String redirectUri;
        private Set<String> scopes = new HashSet<>();
        private Set<String> grants = new HashSet<>();
        private boolean implicit;
        private long validityInSeconds;

        public Builder(String clientId, String clientSecret) {
            if (Strings.isNullOrEmpty(clientId)) {
                throw new InvalidAttributeException("clientId must not be null or empty.");
            }
            if (Strings.isNullOrEmpty(clientSecret)) {
                throw new InvalidAttributeException("clientSecret must not be null or empty.");
            }
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        public Builder accessTokenValiditySeconds(int accessTokenValiditySeconds) {
            this.accessTokenValiditySeconds = accessTokenValiditySeconds;
            return this;
        }

        public Builder refreshTokenValiditySeconds(int refreshTokenValiditySeconds) {
            this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
            return this;
        }

        public Builder redirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public Builder scopes(Set<String> scopes) {
            this.scopes = scopes;
            return this;
        }

        public Builder grants(Set<String> grants) {
            this.grants = grants;
            return this;
        }

        public Builder implicit(boolean implicit) {
            this.implicit = implicit;
            return this;
        }

        public Builder validityInSeconds(long validityInSeconds) {
            this.validityInSeconds = validityInSeconds;
            return this;
        }

        public Client build() {
            return new Client(this);
        }
    }
}
