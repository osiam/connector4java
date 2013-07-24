package org.osiam.client.oauth;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.*;
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

    public URI getUri() {
        return post.getURI();
    }

    public HttpResponse perform() throws ConnectionInitializationException {
        HttpClient defaultHttpClient = new DefaultHttpClient();
        final HttpResponse response;
        try {
            response = defaultHttpClient.execute(post);
        } catch (IOException e) {
            throw new ConnectionInitializationException("Unable to perform Request ", e);
        }
        return response;
    }

    public AccessToken retrieveAccessToken() throws IOException {
        HttpResponse response = perform();
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

        InputStream content = response.getEntity().getContent();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(content, AccessToken.class);
    }


    public static class Builder {

        private String clientId;
        private String clientSecret;
        private GrantType grantType;
        private final String DEFAULT_SCOPE = "GET POST PUT PATCH DELETE";
        private Header[] headers;

        private Map<String, String> requestParameters = new HashMap<>();
        private String endpoint;
        private HttpEntity body;

        public Builder(String endpoint) {
            requestParameters.put("scope", DEFAULT_SCOPE);
            this.endpoint = endpoint;
        }

        public Builder withGrantType(GrantType grantType) {
            if (!grantType.equals(GrantType.PASSWORD)) {
                throw new UnsupportedOperationException(grantType.getUrlParam() + " grant type not supported at this time");
            }
            this.grantType = grantType;
            return this;
        }

        public Builder withClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }


        public Builder withClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder withUsername(String username) {
            requestParameters.put("username", username);
            return this;
        }

        public Builder withPassword(String password) {
            requestParameters.put("password", password);
            return this;
        }

        public AuthService build() {
            if (clientId == null || clientSecret == null) {
                throw new ConnectionInitializationException("The provided client credentials are incomplete.");
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
