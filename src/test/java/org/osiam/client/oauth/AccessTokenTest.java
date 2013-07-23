package org.osiam.client.oauth;

import org.junit.Before;
import org.junit.Test;
import org.osiam.client.AccessTokenMockProvider;
import org.osiam.client.oauth.AccessToken;

import java.io.IOException;

import static org.junit.Assert.*;

public class AccessTokenTest {

    private final static String TOKEN = "c5d116cb-2758-4e7c-9aca-4a115bc4f19e";
    private final static String TOKEN_TYPE = "bearer";
    private AccessTokenMockProvider tokenProvider;
    private AccessToken accessToken;

    @Before
    public void setUp() {
        tokenProvider = new AccessTokenMockProvider("/valid_accesstoken.json");

    }

    @Test
    public void access_token_is_deserialized_without_errors() throws IOException {
        given_a_valid_access_token();
        assertEquals(TOKEN, accessToken.getToken());
        assertEquals(TOKEN_TYPE, accessToken.getType());
        assertFalse(accessToken.isExpired());
    }

    @Test
    public void expired_access_token_is_recognized_correctly() throws Exception {
        given_an_expired_access_token();
        assertTrue(accessToken.isExpired());
    }

    @Test(expected = NoSuchMethodError.class)
    public void getRefreshToken_raises_exception() throws IOException {
        given_a_valid_access_token();
        accessToken.getRefreshToken();
        fail();
    }

    private void given_an_expired_access_token() throws Exception {
        accessToken = tokenProvider.given_an_expired_access_token();
    }

    private void given_a_valid_access_token() throws IOException {
        accessToken = tokenProvider.given_a_valid_access_token();
    }

}
