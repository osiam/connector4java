package org.osiam.model;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class AccessTokenTest {

    private final static String TOKEN = "c5d116cb-2758-4e7c-9aca-4a115bc4f19e";
    private final static String TOKEN_TYPE = "bearer";
    private InputStream is;
    private ObjectMapper mapper;
    private AccessToken accessToken;

    @Before
    public void setUp() {
        is = getClass().getResourceAsStream("/valid_accesstoken.json");
        mapper = new ObjectMapper();
    }

    @Test
    public void access_token_is_deserialized_without_errors() throws IOException {
        given_a_valid_access_token();
        System.out.println(accessToken);
        assertEquals(TOKEN, accessToken.getToken());
        assertEquals(TOKEN_TYPE, accessToken.getType());
        assertFalse(accessToken.isExpired());
    }

    @Test
    public void expired_access_token_is_recognized_correctly() throws IllegalAccessException, NoSuchFieldException, IOException {
        given_an_expired_access_token();
        assertTrue(accessToken.isExpired());
    }

    @Test(expected = NotImplementedException.class)
    public void getRefreshToken_raises_exception() throws IOException {
        given_a_valid_access_token();
        accessToken.getRefreshToken();
        fail();
    }

    private void given_a_valid_access_token() throws IOException {
        accessToken = mapper.readValue(is, AccessToken.class);
    }

    private void given_an_expired_access_token() throws IOException, NoSuchFieldException, IllegalAccessException {
        accessToken = mapper.readValue(is, AccessToken.class);
        Field expiredInField = accessToken.getClass().getDeclaredField("expiresIn");
        expiredInField.setAccessible(true);
        expiredInField.set(accessToken, -1);
        expiredInField.setAccessible(false);
    }
}
