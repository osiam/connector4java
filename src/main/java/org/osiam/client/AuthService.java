package org.osiam.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.map.ObjectMapper;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.GrantType;
import org.osiam.client.oauth.OAuthClientRequest;
import org.osiam.model.AccessToken;
import org.osiam.util.AccessTokenMapping;

import java.io.IOException;
import java.io.InputStream;
/*
 * for licensing see in the license.txt
 */

/**
 * provides methods to provide easy logins
 */
public class AuthService {

    public AccessToken retrieveAccessToken(String endpoint, String clientId, String clientSecret, String username, String password) throws IOException {

        OAuthClientRequest.Builder builder = new OAuthClientRequest.Builder(endpoint);
        OAuthClientRequest request = builder.withGrantType(GrantType.PASSWORD)
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withUsername(username)
                .withPassword(password).buildRequest();
        HttpResponse response = request.perform();
        int status = response.getStatusLine().getStatusCode();

        switch (status) {
            case 400:
                throw new UnauthorizedException("You are not authorized to retrieve an access token directly. Please make sure that you have the correct grants.");
            case 401:
                StringBuilder errorMessage = new StringBuilder("You are not authorized to directly retrieve a access token.");
                if (response.toString().contains(clientId + " not found")) {
                    errorMessage.append(" Unknown client-id");
                } else {
                    errorMessage.append(" Invalid client secret");
                }
                throw new UnauthorizedException(errorMessage.toString());
            case 404:
                throw new ConnectionInitializationException("Unable to find the given OSIAM service (" + endpoint.toString() + ")");
        }

        InputStream content = response.getEntity().getContent();
        AccessToken accessToken = new AccessTokenMapping().getAccessToken(content);

        return accessToken;
    }
}