package org.osiam.client;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import org.codehaus.jackson.map.ObjectMapper;
import org.osiam.client.oauth.AccessToken;

public class AccessTokenMockProvider {

    private ObjectMapper mapper;
    private String path;

    public AccessTokenMockProvider(String path) {
        this.path = path;
        mapper = new ObjectMapper();
    }

    public AccessToken given_a_valid_access_token() throws IOException {
        InputStream is = readFile(path);
        return mapper.readValue(is, AccessToken.class);
    }

    public AccessToken given_an_expired_access_token() throws IOException, NoSuchFieldException, IllegalAccessException {
        InputStream is = readFile(path);
        AccessToken accessToken = mapper.readValue(is, AccessToken.class);
        Field expiredInField = accessToken.getClass().getDeclaredField("expiresIn");
        expiredInField.setAccessible(true);
        expiredInField.set(accessToken, -1);
        expiredInField.setAccessible(false);
        return accessToken;
    }
    
    public AccessToken given_an_invalid_access_token() throws IOException, NoSuchFieldException, IllegalAccessException {
        InputStream is = readFile(path);
        AccessToken accessToken = mapper.readValue(is, AccessToken.class);
        Field expiredInField = accessToken.getClass().getDeclaredField("token");
        expiredInField.setAccessible(true);
        expiredInField.set(accessToken, "99d116cb-2758-4e7c-9aca-4a115bc4f19e");
        expiredInField.setAccessible(false);
        return accessToken;
    }

    private InputStream readFile(String path) {
        return getClass().getResourceAsStream(path);

    }
}
