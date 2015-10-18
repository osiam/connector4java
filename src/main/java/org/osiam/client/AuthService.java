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

package org.osiam.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.osiam.client.exception.BadCredentialsException;
import org.osiam.client.exception.BadRequestException;
import org.osiam.client.exception.ClientAlreadyExistsException;
import org.osiam.client.exception.ClientNotFoundException;
import org.osiam.client.exception.ConflictException;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.ForbiddenException;
import org.osiam.client.exception.OAuthErrorMessage;
import org.osiam.client.exception.OsiamClientException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Client;
import org.osiam.client.oauth.GrantType;
import org.osiam.client.oauth.Scope;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.osiam.client.OsiamConnector.objectMapper;

/**
 * The AuthService provides access to the OAuth2 service used to authorize requests. Please use the
 * {@link AuthService.Builder} to construct one.
 */
class AuthService {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String TOKEN_ENDPOINT = "/oauth/token";
    private static final String TOKEN_REVOCATION_ENDPOINT = "/token/revocation";
    private static final String TOKEN_VALIDATION_ENDPOINT = "/token/validation";
    private static final String CLIENT_ENDPOINT = "/Client";

    private final String endpoint;
    private final String clientId;
    private final String clientSecret;
    private final String clientRedirectUri;
    private final int connectionTimeout;
    private final int readTimeout;

    private final WebTarget targetEndpoint;

    private AuthService(Builder builder) {
        endpoint = builder.endpoint;

        clientId = builder.clientId;
        clientSecret = builder.clientSecret;
        clientRedirectUri = builder.clientRedirectUri;
        connectionTimeout = builder.connectTimeout;
        readTimeout = builder.readTimeout;

        targetEndpoint = OsiamConnector.getClient().target(endpoint);
    }

    public AccessToken retrieveAccessToken(Scope... scopes) {
        ensureClientCredentialsAreSet();
        String formattedScopes = getScopesAsString(scopes);
        Form form = new Form();
        form.param("scope", formattedScopes);
        form.param("grant_type", GrantType.CLIENT_CREDENTIALS.getUrlParam());

        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path(TOKEN_ENDPOINT)
                    .request(MediaType.APPLICATION_JSON)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_USERNAME, clientId)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_PASSWORD, clientSecret)
                    .property(ClientProperties.CONNECT_TIMEOUT, connectionTimeout)
                    .property(ClientProperties.READ_TIMEOUT, readTimeout)
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw createGeneralConnectionInitializationException(e);
        }

        checkAndHandleResponse(content, status, new AccessToken.Builder("n/a").build());

        return getAccessToken(content);
    }

    public AccessToken retrieveAccessToken(String userName, String password, Scope... scopes) {
        ensureClientCredentialsAreSet();
        String formattedScopes = getScopesAsString(scopes);
        Form form = new Form();
        form.param("scope", formattedScopes);
        form.param("grant_type", GrantType.RESOURCE_OWNER_PASSWORD_CREDENTIALS.getUrlParam());
        form.param("username", userName);
        form.param("password", password);

        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path(TOKEN_ENDPOINT)
                    .request(MediaType.APPLICATION_JSON)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_USERNAME, clientId)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_PASSWORD, clientSecret)
                    .property(ClientProperties.CONNECT_TIMEOUT, connectionTimeout)
                    .property(ClientProperties.READ_TIMEOUT, readTimeout)
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw createGeneralConnectionInitializationException(e);
        }

        checkAndHandleResponse(content, status, new AccessToken.Builder("n/a").build());

        return getAccessToken(content);
    }

    public AccessToken retrieveAccessToken(String authCode) {
        checkArgument(!Strings.isNullOrEmpty(authCode), "The given authentication code can't be null.");
        ensureClientCredentialsAreSet();

        Form form = new Form();
        form.param("code", authCode);
        form.param("grant_type", "authorization_code");
        form.param("redirect_uri", clientRedirectUri);

        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path(TOKEN_ENDPOINT)
                    .request(MediaType.APPLICATION_JSON)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_USERNAME, clientId)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_PASSWORD, clientSecret)
                    .property(ClientProperties.CONNECT_TIMEOUT, connectionTimeout)
                    .property(ClientProperties.READ_TIMEOUT, readTimeout)
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw createGeneralConnectionInitializationException(e);
        }

        if (status.getStatusCode() == Status.BAD_REQUEST.getStatusCode()) {
            String errorMessage = extractErrorMessage(content, status);
            throw new ConflictException(errorMessage);
        }
        checkAndHandleResponse(content, status, new AccessToken.Builder("n/a").build());

        return getAccessToken(content);
    }

    private String getScopesAsString(Scope... scopes) {
        StringBuilder scopeBuilder = new StringBuilder();
        for (Scope scope : scopes) {
            scopeBuilder.append(scope.toString()).append(" ");
        }

        return scopeBuilder.toString().trim();
    }

    public AccessToken refreshAccessToken(AccessToken accessToken, Scope... scopes) {
        checkArgument(accessToken != null, "The given accessToken code can't be null.");
        checkArgument(accessToken.getRefreshToken() != null,
                "Unable to perform a refresh_token_grant request without refresh token.");
        ensureClientCredentialsAreSet();

        String formattedScopes = getScopesAsString(scopes);

        Form form = new Form();
        form.param("scope", formattedScopes);
        form.param("grant_type", GrantType.REFRESH_TOKEN.getUrlParam());
        form.param("refresh_token", accessToken.getRefreshToken());

        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path(TOKEN_ENDPOINT)
                    .request(MediaType.APPLICATION_JSON)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_USERNAME, clientId)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_PASSWORD, clientSecret)
                    .property(ClientProperties.CONNECT_TIMEOUT, connectionTimeout)
                    .property(ClientProperties.READ_TIMEOUT, readTimeout)
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw createGeneralConnectionInitializationException(e);
        }

        // need to override default behavior of checkAndHandleResponse
        if (status.getStatusCode() == Status.BAD_REQUEST.getStatusCode()) {
            throw new ConflictException(extractErrorMessage(content, status));
        }
        checkAndHandleResponse(content, status, accessToken);

        return getAccessToken(content);
    }

    public URI getAuthorizationUri(Scope... scopes) {
        checkState(!Strings.isNullOrEmpty(clientRedirectUri), "Can't create the login uri: redirect URI was not set.");
        try {
            String formattedScopes = getScopesAsString(scopes);

            return UriBuilder.fromUri(endpoint).path("/oauth/authorize")
                    .queryParam("client_id", clientId)
                    .queryParam("response_type", "code")
                    .queryParam("redirect_uri", clientRedirectUri)
                    .queryParam("scope", formattedScopes)
                    .build();
        } catch (UriBuilderException | IllegalArgumentException e) {
            throw new OsiamClientException("Unable to create redirect URI", e);
        }
    }

    /**
     * @see OsiamConnector#validateAccessToken(AccessToken)
     */
    public AccessToken validateAccessToken(AccessToken tokenToValidate) {
        checkNotNull(tokenToValidate, "The tokenToValidate must not be null.");

        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path(TOKEN_VALIDATION_ENDPOINT)
                    .request(MediaType.APPLICATION_JSON)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_USERNAME, clientId)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_PASSWORD, clientSecret)
                    .property(ClientProperties.CONNECT_TIMEOUT, connectionTimeout)
                    .property(ClientProperties.READ_TIMEOUT, readTimeout)
                    .header(AUTHORIZATION_HEADER, BEARER + tokenToValidate.getToken())
                    .post(null);

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw createGeneralConnectionInitializationException(e);
        }

        checkAndHandleResponse(content, status, tokenToValidate);

        return getAccessToken(content);
    }

    public void revokeAccessToken(AccessToken tokenToRevoke) {
        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path(TOKEN_REVOCATION_ENDPOINT)
                    .request(MediaType.APPLICATION_JSON)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_USERNAME, clientId)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_PASSWORD, clientSecret)
                    .property(ClientProperties.CONNECT_TIMEOUT, connectionTimeout)
                    .property(ClientProperties.READ_TIMEOUT, readTimeout)
                    .header(AUTHORIZATION_HEADER, BEARER + tokenToRevoke.getToken())
                    .post(null);

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw createGeneralConnectionInitializationException(e);
        }

        checkAndHandleResponse(content, status, tokenToRevoke);
    }

    public void revokeAllAccessTokens(String id, AccessToken accessToken) {
        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path(TOKEN_REVOCATION_ENDPOINT).path(id)
                    .request(MediaType.APPLICATION_JSON)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_USERNAME, clientId)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_PASSWORD, clientSecret)
                    .property(ClientProperties.CONNECT_TIMEOUT, connectionTimeout)
                    .property(ClientProperties.READ_TIMEOUT, readTimeout)
                    .header(AUTHORIZATION_HEADER, BEARER + accessToken.getToken())
                    .post(null);

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw createGeneralConnectionInitializationException(e);
        }

        checkAndHandleResponse(content, status, accessToken);
    }

    public Client createClient(Client client, AccessToken accessToken) {
        StatusType status;
        String createdClient;

        String clientAsString;
        try {
            clientAsString = objectMapper.writeValueAsString(client);
        } catch (JsonProcessingException e) {
            throw new OsiamClientException(String.format("Unable to parse Client: %s", client), e);
        }

        try {
            Response response = targetEndpoint.path(CLIENT_ENDPOINT)
                    .request(MediaType.APPLICATION_JSON)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_USERNAME, clientId)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_PASSWORD, clientSecret)
                    .property(ClientProperties.CONNECT_TIMEOUT, connectionTimeout)
                    .property(ClientProperties.READ_TIMEOUT, readTimeout)
                    .header(AUTHORIZATION_HEADER, BEARER + accessToken.getToken())
                    .post(Entity.entity(clientAsString, MediaType.APPLICATION_JSON));

            status = response.getStatusInfo();
            createdClient = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw createGeneralConnectionInitializationException(e);
        }

        // need to override default behavior of checkAndHandleResponse
        if (status.getStatusCode() == Status.CONFLICT.getStatusCode()) {
            throw new ClientAlreadyExistsException(extractErrorMessage(createdClient, status));
        }

        checkAndHandleResponse(createdClient, status, accessToken);

        try {
            return objectMapper.readValue(createdClient, Client.class);
        } catch (IOException e) {
            throw new OsiamClientException(String.format("Unable to parse Client: %s", createdClient), e);
        }
    }

    public Client getClient(String getClientId, AccessToken accessToken) {
        StatusType status;
        String client;
        try {
            Response response = targetEndpoint.path(CLIENT_ENDPOINT).path(getClientId)
                    .request(MediaType.APPLICATION_JSON)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_USERNAME, clientId)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_PASSWORD, clientSecret)
                    .property(ClientProperties.CONNECT_TIMEOUT, connectionTimeout)
                    .property(ClientProperties.READ_TIMEOUT, readTimeout)
                    .header(AUTHORIZATION_HEADER, BEARER + accessToken.getToken())
                    .get();

            status = response.getStatusInfo();
            client = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw createGeneralConnectionInitializationException(e);
        }

        checkAndHandleResponse(client, status, accessToken);

        try {
            return objectMapper.readValue(client, Client.class);
        } catch (IOException e) {
            throw new OsiamClientException(String.format("Unable to parse Client: %s", client), e);
        }
    }

    public List<Client> getClients(AccessToken accessToken) {
        StatusType status;
        String clients;
        try {
            Response response = targetEndpoint.path(CLIENT_ENDPOINT)
                    .request(MediaType.APPLICATION_JSON)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_USERNAME, clientId)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_PASSWORD, clientSecret)
                    .property(ClientProperties.CONNECT_TIMEOUT, connectionTimeout)
                    .property(ClientProperties.READ_TIMEOUT, readTimeout)
                    .header(AUTHORIZATION_HEADER, BEARER + accessToken.getToken())
                    .get();

            status = response.getStatusInfo();
            clients = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw createGeneralConnectionInitializationException(e);
        }

        checkAndHandleResponse(clients, status, accessToken);

        try {
            return objectMapper.readValue(clients, new TypeReference<List<Client>>() {
            });
        } catch (IOException e) {
            throw new OsiamClientException(String.format("Unable to parse list of Clients: %s", clients), e);
        }
    }

    public void deleteClient(String deleteClientId, AccessToken accessToken) {
        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path(CLIENT_ENDPOINT).path(deleteClientId)
                    .request(MediaType.APPLICATION_JSON)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_USERNAME, clientId)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_PASSWORD, clientSecret)
                    .property(ClientProperties.CONNECT_TIMEOUT, connectionTimeout)
                    .property(ClientProperties.READ_TIMEOUT, readTimeout)
                    .header(AUTHORIZATION_HEADER, BEARER + accessToken.getToken())
                    .delete();

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw createGeneralConnectionInitializationException(e);
        }

        checkAndHandleResponse(content, status, accessToken);
    }

    public Client updateClient(String updateClientId, Client client, AccessToken accessToken) {
        StatusType status;
        String clientResponse;

        String clientAsString;
        try {
            clientAsString = objectMapper.writeValueAsString(client);
        } catch (JsonProcessingException e) {
            throw new OsiamClientException(String.format("Unable to parse Client: %s", client), e);
        }

        try {
            Response response = targetEndpoint.path(CLIENT_ENDPOINT).path(updateClientId)
                    .request(MediaType.APPLICATION_JSON)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_USERNAME, clientId)
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_PASSWORD, clientSecret)
                    .property(ClientProperties.CONNECT_TIMEOUT, connectionTimeout)
                    .property(ClientProperties.READ_TIMEOUT, readTimeout)
                    .header(AUTHORIZATION_HEADER, BEARER + accessToken.getToken())
                    .put(Entity.entity(clientAsString, MediaType.APPLICATION_JSON));

            status = response.getStatusInfo();
            clientResponse = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw createGeneralConnectionInitializationException(e);
        }

        checkAndHandleResponse(clientResponse, status, accessToken);

        try {
            return objectMapper.readValue(clientResponse, Client.class);
        } catch (IOException e) {
            throw new OsiamClientException(String.format("Unable to parse Client: %s", clientResponse), e);
        }
    }

    private void checkAndHandleResponse(String content, StatusType status, AccessToken accessToken) {
        if (status.getFamily() == Status.Family.SUCCESSFUL) {
            return;
        }

        if (status.getStatusCode() == Status.BAD_REQUEST.getStatusCode()) {
            String errorMessage = extractErrorMessage(content, status);
            if (errorMessage.equals("Bad credentials")) {
                throw new BadCredentialsException(errorMessage);
            } else {
                throw new BadRequestException(errorMessage);
            }
        } else if (status.getStatusCode() == Status.UNAUTHORIZED.getStatusCode()) {
            String errorMessage = extractErrorMessage(content, status);
            throw new UnauthorizedException(errorMessage);
        } else if (status.getStatusCode() == Status.FORBIDDEN.getStatusCode()) {
            String errorMessage = extractErrorMessageForbidden(accessToken);
            throw new ForbiddenException(errorMessage);
        } else if (status.getStatusCode() == Status.NOT_FOUND.getStatusCode()) {
            String errorMessage = extractErrorMessage(content, status);
            throw new ClientNotFoundException(errorMessage);
        } else if (status.getStatusCode() == Status.CONFLICT.getStatusCode()) {
            String errorMessage = extractErrorMessage(content, status);
            throw new ConflictException(errorMessage);
        } else {
            String errorMessage = extractErrorMessage(content, status);
            throw new ConnectionInitializationException(errorMessage);
        }
    }

    private String extractErrorMessage(String content, StatusType status) {
        try {
            OAuthErrorMessage error = objectMapper.readValue(content, OAuthErrorMessage.class);
            return error.getDescription();
        } catch (IOException e) {
            String errorMessage = String.format("Could not deserialize the error response for the HTTP status '%s'.",
                    status.getReasonPhrase());

            if (content != null) {
                errorMessage += String.format(" Original response: %s", content);
            }

            return errorMessage;
        }
    }

    protected String extractErrorMessageForbidden(AccessToken accessToken) {
        return "Insufficient scopes: " + accessToken.getScopes();
    }

    private AccessToken getAccessToken(String content) {
        try {
            return objectMapper.readValue(content, AccessToken.class);
        } catch (IOException e) {
            throw new OsiamClientException(String.format("Unable to parse access token: %s", content), e);
        }
    }

    private void ensureClientCredentialsAreSet() {
        checkState(!Strings.isNullOrEmpty(clientId), "The client id can't be null or empty.");
        checkState(!Strings.isNullOrEmpty(clientSecret), "The client secret can't be null or empty.");
    }

    private ConnectionInitializationException createGeneralConnectionInitializationException(Throwable e) {
        return new ConnectionInitializationException("Unable to retrieve access token.", e);
    }

    /**
     * The Builder class is used to construct instances of the {@link AuthService}.
     */
    public static class Builder {

        private String clientId;
        private String clientSecret;
        private String endpoint;
        private String clientRedirectUri;
        private int connectTimeout = OsiamConnector.DEFAULT_CONNECT_TIMEOUT;
        private int readTimeout = OsiamConnector.DEFAULT_READ_TIMEOUT;

        /**
         * Set up the Builder for the construction of an {@link AuthService} instance for the OAuth2 service at the
         * given endpoint
         *
         * @param endpoint The URL at which the OAuth2 service lives.
         */
        public Builder(String endpoint) {
            this.endpoint = endpoint;
        }

        /**
         * Add a ClientId to the OAuth2 request
         *
         * @param clientId The client-Id
         * @return The builder itself
         */
        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         * Add a Client redirect URI to the OAuth2 request
         *
         * @param clientRedirectUri the clientRedirectUri which is known to OSIAM
         * @return The builder itself
         */
        public Builder setClientRedirectUri(String clientRedirectUri) {
            this.clientRedirectUri = clientRedirectUri;
            return this;
        }

        /**
         * Add a clientSecret to the OAuth2 request
         *
         * @param clientSecret The client secret
         * @return The builder itself
         */
        public Builder setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        /**
         * Set the connect timeout per connector, in milliseconds.
         * <p/>
         * <p>
         * A value of zero (0) is equivalent to an interval of infinity. Default: 0
         * </p>
         *
         * @param connectTimeout the connect timeout per connector, in milliseconds.
         * @return The builder itself
         */
        public Builder withConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        /**
         * Set the read timeout per connector, in milliseconds.
         * <p/>
         * <p>
         * A value of zero (0) is equivalent to an interval of infinity. Default: 0
         * </p>
         *
         * @param readTimeout the read timeout per connector, in milliseconds.
         * @return The builder itself
         */
        public Builder withReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        /**
         * Construct the {@link AuthService} with the parameters passed to this builder.
         *
         * @return An {@link AuthService} configured accordingly.
         */
        public AuthService build() {
            return new AuthService(this);
        }
    }
}
