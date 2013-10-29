package org.osiam.client;
/*
 * for licensing see the file license.txt.
 */

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.osiam.client.oauth.AccessToken;

public class AccessTokenMockProvider {

    private static final String INVALID_ACCESS_TOKEN = "99d116cb-2758-4e7c-9aca-4a115bc4f19e";
    private ObjectMapper mapper;
    private String path;

    public AccessTokenMockProvider(String path) {
        this.path = path;
        mapper = new ObjectMapper();
    }

    public AccessToken valid_access_token() throws IOException {
        InputStream is = readFile(path);
        return mapper.readValue(is, AccessToken.class);
    }

    public AccessToken expired_access_token() throws IOException, NoSuchFieldException, IllegalAccessException {
        InputStream is = readFile(path);
        AccessToken accessToken = mapper.readValue(is, AccessToken.class);
        Field expiredInField = accessToken.getClass().getDeclaredField("expiresIn");
        expiredInField.setAccessible(true);
        expiredInField.set(accessToken, -1);
        expiredInField.setAccessible(false);
        return accessToken;
    }

    public AccessToken invalid_access_token() throws IOException, NoSuchFieldException, IllegalAccessException {
        InputStream is = readFile(path);
        AccessToken accessToken = mapper.readValue(is, AccessToken.class);
        Field token = accessToken.getClass().getDeclaredField("token");
        token.setAccessible(true);
        token.set(accessToken, INVALID_ACCESS_TOKEN);
        token.setAccessible(false);
        return accessToken;
    }

    private InputStream readFile(String path) {
        return getClass().getResourceAsStream(path);

    }
}
