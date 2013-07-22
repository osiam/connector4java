package org.osiam.client.oauth;

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
import org.osiam.client.exception.ConnectionInitializationException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OAuthClientRequest {

    private static final Charset CHARSET = Charset.forName("UTF-8");
    private HttpPost post;


    private OAuthClientRequest(Builder builder) {
        post = new HttpPost(builder.endpoint);
        post.setHeaders(builder.headers);
        post.setEntity(builder.body);
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



    public static class Builder {

        // Client credentials
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

        public OAuthClientRequest buildRequest() {
            if (clientId == null || clientSecret == null) {
                throw new ConnectionInitializationException("The provided client credentials are incomplete.");
            }
            if (grantType.equals(GrantType.PASSWORD) && !(requestParameters.containsKey("username") && requestParameters.containsKey("password"))) {
                throw new ConnectionInitializationException("The grant type 'password' requires username and password");
            }
            requestParameters.put("grant_type", grantType.getUrlParam());
            this.body = buildBody();
            this.headers = buildHead();
            return new OAuthClientRequest(this);
        }

        private String encodeClientCredentials(String clientId, String clientSecret) {
            String clientCredentials = clientId + ":" + clientSecret;
            return new String(Base64.encodeBase64(clientCredentials.getBytes(CHARSET)), CHARSET);
        }

        private Header[] buildHead() {
            String authHeaderValue = "Basic " + encodeClientCredentials(clientId, clientSecret);
            Header authHeader = new BasicHeader("Authorization", authHeaderValue);
            Header[] headers = {
                    authHeader
            };
            return headers;
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
