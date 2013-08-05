package org.osiam.client.oauth;
/*
 * for licensing see the file license.txt.
 */

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.UnauthorizedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.http.HttpStatus.*;

/**
 * The AuthService provides access to the OAuth2 service used to authorize requests. Please use the
 * {@link AuthService.Builder} to construct one.
 */
public class AuthService {

    private static final Charset CHARSET = Charset.forName("UTF-8");
    private final HttpPost post;
    private final String clientId;
    private final String endpoint;

    private AuthService(Builder builder) {
        post = new HttpPost(builder.endpoint);
        post.setHeaders(builder.headers);
        post.setEntity(builder.body);
        clientId = builder.clientId;
        endpoint = builder.endpoint;
    }

    /**
     * Provide the {@link URI} this AuthService uses to authenticate.
     *
     * @return The URI used by this AuthService
     */
    public URI getUri() {
        return post.getURI();
    }

    private HttpResponse performRequest() throws ConnectionInitializationException {
        HttpClient defaultHttpClient = new DefaultHttpClient();
        final HttpResponse response;
        try {
            response = defaultHttpClient.execute(post);
        } catch (IOException e) {
            throw new ConnectionInitializationException("Unable to perform Request ", e);
        }
        return response;
    }

    /**
     * Provide an {@link AccessToken} for the given parameters of this service.
     *
     * @return a valid AccessToken
     * @throws ConnectionInitializationException
     *                               If the Service is unable to connect to the configured OAuth2 service.
     * @throws UnauthorizedException If the configured credentials for this service are not permitted
     *                               to retrieve an {@link AccessToken}
     */
    public AccessToken retrieveAccessToken() {
        HttpResponse response = performRequest();
        int status = response.getStatusLine().getStatusCode();

        switch (status) {
            case SC_BAD_REQUEST:
                throw new ConnectionInitializationException("Unable to create Connection. Please make sure that you have the correct grants.");
            case SC_UNAUTHORIZED:
                StringBuilder errorMessage = new StringBuilder("You are not authorized to directly retrieve a access token.");
                if (response.toString().contains(clientId + " not found")) {
                    errorMessage.append(" Unknown client-id");
                } else {
                    errorMessage.append(" Invalid client secret");
                }
                throw new UnauthorizedException(errorMessage.toString());
            case SC_NOT_FOUND:
                throw new ConnectionInitializationException("Unable to find the given OSIAM service (" + endpoint + ")");
        }

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


    /**
     * The Builder class is used to construct instances of the {@link AuthService}.
     */
    public static class Builder {

        private String clientId;
        private String clientSecret;
        private GrantType grantType;
        private final String DEFAULT_SCOPE = "GET POST PUT PATCH DELETE";
        private Header[] headers;

        private Map<String, String> requestParameters = new HashMap<>();
        private String endpoint;
        private HttpEntity body;

        /**
         * Set up the Builder for the construction of  an {@link AuthService} instance for the OAuth2 service at
         * the given endpoint
         *
         * @param endpoint The URL at which the OAuth2 service lives.
         */
        public Builder(String endpoint) {
            requestParameters.put("scope", DEFAULT_SCOPE);
            this.endpoint = endpoint + "/oauth/token";
        }

        /**
         * Use the given {@link GrantType} to for the request. At this point only the grant type 'password' is supported.
         *
         * @param grantType of the requested AuthCode
         * @return The builder itself
         * @throws UnsupportedOperationException If the GrantType is anything else than GrantType.PASSWORD
         */
        public Builder withGrantType(GrantType grantType) {
            if (!grantType.equals(GrantType.PASSWORD)) {
                throw new UnsupportedOperationException(grantType.getUrlParam() + " grant type not supported at this time");
            }
            this.grantType = grantType;
            return this;
        }

        /**
         * Add a ClientId to the OAuth2 request
         *
         * @param clientId The client-Id
         * @return The builder itself
         */
        public Builder withClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         * Add a clientSecret to the OAuth2 request
         *
         * @param clientSecret The client secret
         * @return The builder itself
         */
        public Builder withClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        /**
         * Add the given username to the OAuth2 request
         *
         * @param username The username
         * @return The builder itself
         */
        public Builder withUsername(String username) {
            requestParameters.put("username", username);
            return this;
        }

        /**
         * Add the given password to the OAuth2 request
         *
         * @param password The password
         * @return The builder itself
         */
        public Builder withPassword(String password) {
            requestParameters.put("password", password);
            return this;
        }

        /**
         * Construct the {@link AuthService} with the parameters passed to this builder.
         *
         * @return An AuthService configured accordingly.
         * @throws ConnectionInitializationException
         *          If either the provided client credentials (clientId/clientSecret)
         *          or, if the requested grant type is 'password', the user credentials (userName/password) are incomplete.
         */
        public AuthService build() {
            if (clientId == null || clientSecret == null) {
                throw new ConnectionInitializationException("The provided client credentials are incomplete.");
            }
            if (grantType == null) {
                throw new ConnectionInitializationException("The grant type is not set.");
            }
            if (grantType.equals(GrantType.PASSWORD) && !(requestParameters.containsKey("username") && requestParameters.containsKey("password"))) {
                throw new ConnectionInitializationException("The grant type 'password' requires username and password");
            }
            requestParameters.put("grant_type", grantType.getUrlParam());
            this.body = buildBody();
            this.headers = buildHead();
            return new AuthService(this);
        }

        private String encodeClientCredentials(String clientId, String clientSecret) {
            String clientCredentials = clientId + ":" + clientSecret;
            return new String(Base64.encodeBase64(clientCredentials.getBytes(CHARSET)), CHARSET);
        }

        private Header[] buildHead() {
            String authHeaderValue = "Basic " + encodeClientCredentials(clientId, clientSecret);
            Header authHeader = new BasicHeader("Authorization", authHeaderValue);
            return new Header[]{
                    authHeader
            };
        }

        private HttpEntity buildBody() {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            for (String key : requestParameters.keySet()) {
                nameValuePairs.add(new BasicNameValuePair(key, requestParameters.get(key)));
            }
            try {
                return new UrlEncodedFormEntity(nameValuePairs);
            } catch (UnsupportedEncodingException e) {
                throw new ConnectionInitializationException("Unable to Build Request in this encoding.", e);
            }
        }
    }
}
