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

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.osiam.client.connector.OsiamConnector;
import org.osiam.client.exception.AccessTokenValidationException;
import org.osiam.client.exception.ConflictException;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.ForbiddenException;
import org.osiam.client.exception.InvalidAttributeException;
import org.osiam.client.exception.OAuthErrorMessage;
import org.osiam.client.exception.UnauthorizedException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The AuthService provides access to the OAuth2 service used to authorize
 * requests. Please use the {@link AuthService.Builder} to construct one.
 */
public final class AuthService { // NOSONAR - Builder constructs instances of
                                 // this class

    private static final String AUTHORIZATION = "Authorization";
    private static final String ACCEPT = "Accept";
    private static final String BEARER = "Bearer ";
    private static final Charset CHARSET = Charset.forName("UTF-8");
    private final String endpoint;
    private String clientId;
    private String clientSecret;
    private String clientRedirectUri;
    private String scopes;
    private String password;
    private String userName;
    private GrantType grantType;

    private Header[] headers;
    private HttpEntity body;

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
    }

    /**
     * @see OsiamConnector#retrieveAccessToken()
     */
    public AccessToken retrieveAccessToken() {
        if (grantType == GrantType.AUTHORIZATION_CODE) {
            throw new IllegalAccessError("For the grant type " + GrantType.AUTHORIZATION_CODE
                    + " you need to retrieve a authentication code first.");
        }
        HttpResponse response = performRequest();
        int status = response.getStatusLine().getStatusCode();

        checkAndHandleHttpStatus(response, status);

        return getAccessToken(response);
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
        if (authCode == null) {
            throw new IllegalArgumentException("The given authentication code can't be null.");
        }

        HttpPost realWebResource = getWebRessourceToEchangeAuthCode(authCode);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpResponse response;
        int httpStatus;
        try {
            response = httpClient.execute(realWebResource);
            httpStatus = response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            throw new ConnectionInitializationException("Unable to setup connection", e);
        }

        if (httpStatus != SC_OK) {
            String errorMessage;
            switch (httpStatus) {
            case SC_BAD_REQUEST:
                errorMessage = getErrorMessage(response);
                throw new ConflictException(errorMessage);
            default:
                errorMessage = getErrorMessage(response);
                throw new ConnectionInitializationException(errorMessage);
            }
        }

        return getAccessToken(response);
    }

    /**
     * @see OsiamConnector#refreshAccessToken(AccessToken, Scope...)
     */
    public AccessToken refreshAccessToken(AccessToken accessToken, Scope[] scopes) {
        if (scopes.length != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Scope scope : scopes) {
                stringBuilder.append(" ").append(scope.toString());
            }
            this.scopes = stringBuilder.toString().trim();
        }
        grantType = GrantType.REFRESH_TOKEN;

        HttpResponse response = performRequest(accessToken);
        int status = response.getStatusLine().getStatusCode();

        checkAndHandleHttpStatus(response, status);

        return getAccessToken(response);
    }

    /**
     * @see OsiamConnector#getRedirectLoginUri()
     * @see <a
     *      href="https://github.com/osiam/connector4java/wiki/Login-and-getting-an-access-token#grant-authorization-code">https://github.com/osiam/connector4java/wiki/Login-and-getting-an-access-token#grant-authorization-code</a>
     */
    public URI getRedirectLoginUri() {
        if (grantType != GrantType.AUTHORIZATION_CODE) {
            throw new IllegalAccessError("You need to use the GrantType " + GrantType.AUTHORIZATION_CODE
                    + " to be able to use this method.");
        }
        URI returnUri;
        try {
            returnUri = new URIBuilder().setPath(getFinalEndpoint())
                    .addParameter("client_id", clientId)
                    .addParameter("response_type", "code")
                    .addParameter("redirect_uri", clientRedirectUri)
                    .addParameter("scope", scopes)
                    .build();
        } catch (URISyntaxException e) {
            throw new ConnectionInitializationException("Unable to create redirect URI", e);
        }
        return returnUri;
    }

    /**
     * @see OsiamConnector#validateAccessToken(AccessToken, AccessToken)
     */
    public AccessToken validateAccessToken(AccessToken tokenToValidate, AccessToken tokenToAuthorize) {
        if (tokenToValidate == null || tokenToAuthorize == null) {
            throw new IllegalArgumentException("The given accessToken can't be null.");
        }
        DefaultHttpClient httpclient = new DefaultHttpClient();

        HttpResponse response;
        try {
            URI uri = new URI(endpoint + "/token/validation/" + tokenToValidate.getToken());
            HttpPost realWebResource = new HttpPost(uri);
            realWebResource.addHeader(AUTHORIZATION, BEARER + tokenToAuthorize.getToken());
            realWebResource.addHeader(ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            response = httpclient.execute(realWebResource);
        } catch (IOException | URISyntaxException e) {
            throw new ConnectionInitializationException("", e);
        }
        int httpStatus = response.getStatusLine().getStatusCode();

        if (httpStatus == SC_BAD_REQUEST) {
            String errorMessage = getErrorMessage(response);
            throw new AccessTokenValidationException(errorMessage);
        }

        checkAndHandleHttpStatus(response, httpStatus);

        return getAccessToken(response);
    }

    private HttpResponse performRequest(AccessToken... accessTokens) {
        buildHead();
        buildBody(accessTokens);
        HttpPost post = new HttpPost(getFinalEndpoint());
        post.setHeaders(headers);
        post.setEntity(body);

        HttpClient defaultHttpClient = new DefaultHttpClient();
        final HttpResponse response;
        try {
            response = defaultHttpClient.execute(post);
        } catch (IOException e) {
            throw new ConnectionInitializationException("Unable to perform Request ", e);
        }
        return response;
    }

    private void buildHead() {
        String authHeaderValue = "Basic " + encodeClientCredentials(clientId, clientSecret);
        Header authHeader = new BasicHeader("Authorization", authHeaderValue);
        Header acceptHeader = new BasicHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());
        headers = new Header[] {
                authHeader, acceptHeader
        };
    }

    private String encodeClientCredentials(String clientId, String clientSecret) {
        String clientCredentials = clientId + ":" + clientSecret;
        clientCredentials = new String(Base64.encodeBase64(clientCredentials.getBytes(CHARSET)), CHARSET);
        return clientCredentials;
    }

    private void buildBody(AccessToken... accessTokens) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("scope", scopes));
        nameValuePairs.add(new BasicNameValuePair("grant_type", grantType.getUrlParam())); // NOSONAR
                                                                                           // -
                                                                                           // we
                                                                                           // check
                                                                                           // before
                                                                                           // that
                                                                                           // the
                                                                                           // grantType
                                                                                           // is
                                                                                           // not
                                                                                           // null
        if (grantType != GrantType.REFRESH_TOKEN) {
            if (userName != null) {
                nameValuePairs.add(new BasicNameValuePair("username", userName));
            }
            if (password != null) {
                nameValuePairs.add(new BasicNameValuePair("password", password));
            }
        } else if (grantType == GrantType.REFRESH_TOKEN && accessTokens.length != 0) {
            if (accessTokens[0].getRefreshToken() == null) {
                throw new ConnectionInitializationException(
                        "Unable to perform a refresh_token_grant request without refresh token.");
            }
            nameValuePairs.add(new BasicNameValuePair("refresh_token", accessTokens[0].getRefreshToken()));
        }

        try {
            body = new UrlEncodedFormEntity(nameValuePairs);
        } catch (UnsupportedEncodingException e) {
            throw new ConnectionInitializationException("Unable to Build Request in this encoding.", e);
        }
    }

    private void checkAndHandleHttpStatus(HttpResponse response, int status) {
        if (status != SC_OK) {
            String errorMessage;
            switch (status) {
            case SC_BAD_REQUEST:
                errorMessage = getErrorMessage(response);
                throw new ConnectionInitializationException(errorMessage);
            case SC_UNAUTHORIZED:
                errorMessage = getErrorMessage(response);
                throw new UnauthorizedException(errorMessage);
            case SC_NOT_FOUND:
                errorMessage = getErrorMessage(response);
                throw new ConnectionInitializationException(errorMessage);
            default:
                errorMessage = getErrorMessage(response);
                throw new ConnectionInitializationException(errorMessage);
            }
        }
    }

    private String getErrorMessage(HttpResponse httpResponse) {
        String errorMessage;
        InputStream content = null;
        String inputStreamStringValue = null;

        try {
            content = httpResponse.getEntity().getContent();
            inputStreamStringValue = IOUtils.toString(content, "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            OAuthErrorMessage error = mapper.readValue(inputStreamStringValue, OAuthErrorMessage.class);
            errorMessage = error.getDescription();
        } catch (Exception e) { // NOSONAR - we catch everything
            errorMessage = " Could not deserialize the error response for the status code \""
                    + httpResponse.getStatusLine().getReasonPhrase() + "\".";
            if (inputStreamStringValue != null) {
                errorMessage += " Original response: " + inputStreamStringValue;
            }
        }

        return errorMessage;
    }

    private AccessToken getAccessToken(HttpResponse response) {
        final AccessToken accessToken;
        try {
            InputStream content = response.getEntity().getContent();
            ObjectMapper mapper = new ObjectMapper();
            accessToken = mapper.readValue(content, AccessToken.class);
        } catch (IOException e) {
            throw new ConnectionInitializationException("Unable to retrieve access token: IOException", e);
        }
        return accessToken;
    }

    private String getFinalEndpoint() {
        String finalEndpoint = endpoint;
        if (grantType.equals(GrantType.AUTHORIZATION_CODE)) {// NOSONAR - we
                                                             // check before
                                                             // that the
                                                             // grantType is not
                                                             // null
            finalEndpoint += "/oauth/authorize";
        } else {
            finalEndpoint += "/oauth/token";
        }
        return finalEndpoint;
    }

    private HttpPost getWebRessourceToEchangeAuthCode(String authCode) {
        HttpPost realWebResource = new HttpPost(endpoint + "/oauth/token");
        String authHeaderValue = "Basic " + encodeClientCredentials(clientId, clientSecret);
        realWebResource.addHeader("Authorization", authHeaderValue);

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("code", authCode));
        nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
        nameValuePairs.add(new BasicNameValuePair("redirect_uri", clientRedirectUri));

        try {
            realWebResource.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new ConnectionInitializationException("Unable to Build Request in this encoding.", e);
        }
        return realWebResource;
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
