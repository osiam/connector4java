package org.osiam.client;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.model.AccessToken;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
/*
 * for licensing see in the license.txt
 */

/**
 * provides static methods to provide easy logins
 */
public class AuthService {

    static final Charset CHARSET = Charset.forName("UTF-8");

    public static AccessToken retrieveAccessToken(URI endpoint, String clientId, String clientSecret) throws ClientProtocolException, IOException {

        String tokenLocation = endpoint + "/oauth/token";
        String combined = clientId + ":" + clientSecret;
        String encoding = new String(Base64.encodeBase64(combined.getBytes(CHARSET)), CHARSET);

        HttpPost post = new HttpPost(tokenLocation);
        HttpClient defaultHttpClient = new DefaultHttpClient();

        post.addHeader("Authorization", "Basic " + encoding);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("grant_type", "client_credentials"));
        nameValuePairs.add(new BasicNameValuePair("scope", "GET POST PUT PATCH DELETE"));
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = defaultHttpClient.execute(post);
        int status = response.getStatusLine().getStatusCode();
        switch (status) {
            case 400:
                throw new UnauthorizedException("You are not authorized to directly retrieve a access token. Please make sure that you have the correct grants.");
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
        ObjectMapper mapper = new ObjectMapper();
        AccessToken accessToken = mapper.readValue(content, AccessToken.class);

        return accessToken;
    }
}