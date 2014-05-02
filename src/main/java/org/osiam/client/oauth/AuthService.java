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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.osiam.client.connector.OsiamConnector;
import org.osiam.client.exception.AccessTokenValidationException;
import org.osiam.client.exception.ConflictException;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.ForbiddenException;
import org.osiam.client.exception.InvalidAttributeException;
import org.osiam.client.exception.OAuthErrorMessage;
import org.osiam.client.exception.UnauthorizedException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

/**
 * The AuthService provides access to the OAuth2 service used to authorize
 * requests. Please use the {@link AuthService.Builder} to construct one.
 */
public final class AuthService { // NOSONAR - Builder constructs instances of
                                 // this class

    private static final String BEARER = "Bearer ";
    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final Client client = ClientBuilder.newClient();

    private final String endpoint;
    private String clientId;
    private String clientSecret;
    private String clientRedirectUri;
    private String scopes;
    private String password;
    private String userName;
    private GrantType grantType;

    private final WebTarget targetEndpoint;

    /**
     * The private constructor for the AuthService. Please use the
     * {@link AuthService.Builder} to construct one.
     *
     * @param builder
     *            a valid Builder that holds all needed variables
     */
    private AuthService(Builder builder) {
        endpoint = builder.endpoint;
        scopes = builder.scopes;
        grantType = builder.grantType;
        userName = builder.userName;
        password = builder.password;
        clientId = builder.clientId;
        clientSecret = builder.clientSecret;
        clientRedirectUri = builder.clientRedirectUri;

        targetEndpoint = client.target(endpoint);
    }

    /**
     * @see OsiamConnector#retrieveAccessToken()
     */
    public AccessToken retrieveAccessToken() {
        if (grantType == GrantType.AUTHORIZATION_CODE) {
            throw new IllegalAccessError("For the grant type " + GrantType.AUTHORIZATION_CODE
                    + " you need to retrieve a authentication code first.");
        }

        String authHeaderValue = "Basic " + encodeClientCredentials();
        Form form = new Form();
        form.param("scope", scopes);
        form.param("grant_type", grantType.getUrlParam());
        // FIXME: grantType == GrantType.RESOURCE_OWNER_PASSWORD_CREDENTIALS
        if (grantType != GrantType.REFRESH_TOKEN) {
            if (userName != null) {
                form.param("username", userName);
            }
            if (password != null) {
                form.param("password", password);
            }
        }

        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path("/oauth/token").request(MediaType.APPLICATION_JSON)
                    .header("Authorization", authHeaderValue)
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw new ConnectionInitializationException("Unable to retrieve access token.", e);
        }

        checkAndHandleResponse(content, status);

        return getAccessToken(content);
    }

    /**
     * @see OsiamConnector#retrieveAccessToken(HttpResponse)
     * @see <a
     *      href="https://github.com/osiam/connector4java/wiki/Login-and-getting-an-access-token#grant-authorization-code">https://github.com/osiam/connector4java/wiki/Login-and-getting-an-access-token#grant-authorization-code</a>
     */
    public AccessToken retrieveAccessToken(HttpResponse authCodeResponse) {
        String authCode = null;
        Header header = authCodeResponse.getLastHeader("Location");
        HeaderElement[] elements = header.getElements();
        for (HeaderElement actHeaderElement : elements) {
            if (actHeaderElement.getName().contains("code")) {
                authCode = actHeaderElement.getValue();
                break;
            }
            if (actHeaderElement.getName().contains("error")) {
                throw new ForbiddenException("The user had denied the acces to his data.");
            }
        }
        if (authCode == null) {
            throw new InvalidAttributeException("Could not find any auth code or error message in the given Response");
        }
        return retrieveAccessToken(authCode);
    }

    /**
     * @see OsiamConnector#retrieveAccessToken(String)
     * @see <a
     *      href="https://github.com/osiam/connector4java/wiki/Login-and-getting-an-access-token#grant-authorization-code">https://github.com/osiam/connector4java/wiki/Login-and-getting-an-access-token#grant-authorization-code</a>
     */
    public AccessToken retrieveAccessToken(String authCode) {
        checkArgument(!Strings.isNullOrEmpty(authCode), "The given authentication code can't be null.");

        String authHeaderValue = "Basic " + encodeClientCredentials();
        Form form = new Form();
        form.param("code", authCode);
        form.param("grant_type", "authorization_code");
        form.param("redirect_uri", clientRedirectUri);

        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path("/oauth/token").request(MediaType.APPLICATION_JSON)
                    .header("Authorization", authHeaderValue)
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw new ConnectionInitializationException("Unable to retrieve access token.", e);
        }

        // TODO: consolidate with checkAndHandleResponse()
        if (status.getStatusCode() != Status.OK.getStatusCode()) {
            String errorMessage = extractErrorMessage(content, status);

            if (status.getStatusCode() == Status.BAD_REQUEST.getStatusCode()) {
                throw new ConflictException(errorMessage);
            } else {
                throw new ConnectionInitializationException(errorMessage);
            }
        }

        return getAccessToken(content);
    }

    /**
     * @see OsiamConnector#refreshAccessToken(AccessToken, Scope...)
     */
    public AccessToken refreshAccessToken(AccessToken accessToken, Scope[] newScopes) {
        if (newScopes.length != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Scope scope : newScopes) {
                stringBuilder.append(" ").append(scope.toString());
            }
            // FIXME: changing the scope? AuthService should be immutable.
            scopes = stringBuilder.toString().trim();
        }
        // FIXME: changing the grantType? AuthService should be immutable.
        grantType = GrantType.REFRESH_TOKEN;

        Form form = new Form();
        form.param("scope", scopes);
        form.param("grant_type", grantType.getUrlParam());

        // FIXME: should be a guard on method entry
        if (accessToken.getRefreshToken() == null) {
            throw new ConnectionInitializationException(
                    "Unable to perform a refresh_token_grant request without refresh token.");
        }
        form.param("refresh_token", accessToken.getRefreshToken());

        String authHeaderValue = "Basic " + encodeClientCredentials();
        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path("/oauth/token").request(MediaType.APPLICATION_JSON)
                    .header("Authorization", authHeaderValue)
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw new ConnectionInitializationException("Unable to retrieve access token.", e);
        }

        checkAndHandleResponse(content, status);

        return getAccessToken(content);
    }

    /**
     * @see OsiamConnector#getRedirectLoginUri()
     * @see <a
     *      href="https://github.com/osiam/connector4java/wiki/Login-and-getting-an-access-token#grant-authorization-code">https://github.com/osiam/connector4java/wiki/Login-and-getting-an-access-token#grant-authorization-code</a>
     */
    public URI getRedirectLoginUri() {
        if (grantType != GrantType.AUTHORIZATION_CODE) {
            // FIXME: IllegalAccessError must be replaced with
            // IllegalStateException
            throw new IllegalAccessError("You need to use the GrantType " + GrantType.AUTHORIZATION_CODE
                    + " to be able to use this method.");
        }

        try {
            return UriBuilder.fromUri(endpoint).path("/oauth/authorize")
                    .queryParam("client_id", clientId)
                    .queryParam("response_type", "code")
                    .queryParam("redirect_uri", clientRedirectUri)
                    .queryParam("scope", scopes)
                    .build();
        } catch (UriBuilderException | IllegalArgumentException e) {
            // FIXME: must be replaced with a different exception
            throw new ConnectionInitializationException("Unable to create redirect URI", e);
        }
    }

    /**
     * @see OsiamConnector#validateAccessToken(AccessToken, AccessToken)
     */
    public AccessToken validateAccessToken(AccessToken tokenToValidate, AccessToken tokenToAuthorize) {
        checkNotNull(tokenToValidate, "The tokenToValidate must not be null.");
        checkNotNull(tokenToAuthorize, "The tokenToAuthorize must not be null.");

        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path("/token/validation/").path(tokenToValidate.getToken())
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", BEARER + tokenToAuthorize.getToken())
                    .post(null);

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw new ConnectionInitializationException("Unable to retrieve access token.", e);
        }

        if (status.getStatusCode() == Status.BAD_REQUEST.getStatusCode()) {
            String errorMessage = extractErrorMessage(content, status);
            throw new AccessTokenValidationException(errorMessage);
        }

        checkAndHandleResponse(content, status);

        return getAccessToken(content);
    }

    private String encodeClientCredentials() {
        String clientCredentials = clientId + ":" + clientSecret;
        clientCredentials = new String(Base64.encodeBase64(clientCredentials.getBytes(CHARSET)), CHARSET);
        return clientCredentials;
    }

    private void checkAndHandleResponse(String content, StatusType status) {
        if (status.getStatusCode() == Status.OK.getStatusCode()) {
            return;
        }

        final String errorMessage = extractErrorMessage(content, status);

        if (status.getStatusCode() == Status.UNAUTHORIZED.getStatusCode()) {
            throw new UnauthorizedException(errorMessage);
        } else {
            throw new ConnectionInitializationException(errorMessage);
        }
    }

    private String extractErrorMessage(String content, StatusType status) {
        try {
            OAuthErrorMessage error = new ObjectMapper().readValue(content, OAuthErrorMessage.class);

            return error.getDescription();
        } catch (Exception e) { // NOSONAR - we catch everything
            String errorMessage = String.format("Could not deserialize the error response for the HTTP status '%s'.",
                    status.getReasonPhrase());
            if (content != null) {
                errorMessage += String.format(" Original response: %s", content);
            }

            return errorMessage;
        }
    }

    private AccessToken getAccessToken(String content) {
        try {
            return new ObjectMapper().readValue(content, AccessToken.class);
        } catch (IOException e) {
            // FIXME: replace with an other exception. IOExceptions means that
            // jackson could not map/parse json
            throw new ConnectionInitializationException("Unable to retrieve access token.", e);
        } catch (ProcessingException e) {
            throw new ConnectionInitializationException("Unable to retrieve access token.", e);
        }
    }

    /**
     * The Builder class is used to construct instances of the {@link AuthService}.
     */
    public static class Builder {

        private String clientId;
        private String clientSecret;
        private GrantType grantType;
        private String scopes;
        private String endpoint;
        private String password;
        private String userName;
        private String clientRedirectUri;

        /**
         * Set up the Builder for the construction of an {@link AuthService}
         * instance for the OAuth2 service at the given endpoint
         *
         * @param endpoint
         *            The URL at which the OAuth2 service lives.
         */
        public Builder(String endpoint) {
            this.endpoint = endpoint;
        }

        /**
         * Use the given {@link Scope} to for the request.
         *
         * @param scope
         *            the needed scope
         * @param scopes
         *            the needed scopes
         * @return The builder itself
         */
        public Builder setScope(Scope scope, Scope... scopes) {
            Set<Scope> scopeSet = new HashSet<>();

            scopeSet.add(scope);
            for (Scope actScope : scopes) {
                scopeSet.add(actScope);
            }

            if (scopeSet.contains(Scope.ALL)) {
                this.scopes = Scope.ALL.toString();
            } else {
                StringBuilder scopeBuilder = new StringBuilder();
                for (Scope actScope : scopeSet) {
                    scopeBuilder.append(" ").append(actScope.toString());
                }
                this.scopes = scopeBuilder.toString().trim();
            }
            return this;
        }

        /**
         * The needed access token scopes as String like 'GET PATCH'
         *
         * @param scope
         *            the needed scopes
         * @return The builder itself
         */
        public Builder setScope(String scope) {
            scopes = scope;
            return this;
        }

        /**
         * Use the given {@link GrantType} to for the request.
         *
         * @param grantType
         *            of the requested AuthCode
         * @return The builder itself
         */
        public Builder setGrantType(GrantType grantType) {
            this.grantType = grantType;
            return this;
        }

        /**
         * Add a ClientId to the OAuth2 request
         *
         * @param clientId
         *            The client-Id
         * @return The builder itself
         */
        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         * Add a Client redirect URI to the OAuth2 request
         *
         * @param clientRedirectUri
         *            the clientRedirectUri which is known to the OSIAM server
         * @return The builder itself
         */
        public Builder setClientRedirectUri(String clientRedirectUri) {
            this.clientRedirectUri = clientRedirectUri;
            return this;
        }

        /**
         * Add a clientSecret to the OAuth2 request
         *
         * @param clientSecret
         *            The client secret
         * @return The builder itself
         */
        public Builder setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        /**
         * Add the given userName to the OAuth2 request
         *
         * @param userName
         *            The userName
         * @return The builder itself
         */
        public Builder setUsername(String userName) {
            this.userName = userName;
            return this;
        }

        /**
         * Add the given password to the OAuth2 request
         *
         * @param password
         *            The password
         * @return The builder itself
         */
        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        /**
         * Construct the {@link AuthService} with the parameters passed to this
         * builder.
         *
         * @return An {@link AuthService} configured accordingly.
         */
        public AuthService build() {
            ensureAllNeededParameterAreCorrect();
            return new AuthService(this);
        }

        private void ensureAllNeededParameterAreCorrect() {// NOSONAR - this is
                                                           // a test method the
                                                           // Cyclomatic
                                                           // Complexity
                                                           // can be over 10.
            if (clientId == null || clientSecret == null) {
                throw new IllegalArgumentException("The provided client credentials are incomplete.");
            }
            if (scopes == null) {
                throw new IllegalArgumentException("At least one scope needs to be set.");
            }
            if (grantType == null) {
                throw new IllegalArgumentException("The grant type is not set.");
            }
            if (grantType.equals(GrantType.RESOURCE_OWNER_PASSWORD_CREDENTIALS)
                    && userName == null && password == null) {
                throw new IllegalArgumentException("The grant type 'password' requires username and password");
            }
            if ((grantType.equals(GrantType.CLIENT_CREDENTIALS) || grantType.equals(GrantType.AUTHORIZATION_CODE))
                    && (userName != null || password != null)) {
                throw new IllegalArgumentException("For the grant type '" + grantType
                        + "' setting of password and username are not allowed.");
            }
            if (grantType.equals(GrantType.AUTHORIZATION_CODE) && clientRedirectUri == null) {
                throw new IllegalArgumentException("For the grant type '" + grantType + "' the redirect Uri is needed.");
            }
        }
    }
}
