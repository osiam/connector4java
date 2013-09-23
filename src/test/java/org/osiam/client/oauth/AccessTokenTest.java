package org.osiam.client.oauth;
/*
 * for licensing see the file license.txt.
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Before;
import org.junit.Test;
import org.osiam.client.AccessTokenMockProvider;

public class AccessTokenTest {

    private final static String TOKEN = "c5d116cb-2758-4e7c-9aca-4a115bc4f19e";
    private final static String TOKEN_TYPE = "bearer";
    private AccessTokenMockProvider tokenProvider;
    private AccessToken accessToken;

    @Before
    public void setUp() {
        tokenProvider = new AccessTokenMockProvider("/__files/valid_accesstoken.json");

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

    @Test
    public void toString_behaves_as_expected() throws IOException {
        given_a_valid_access_token();
        String builder = "[access_token = " + TOKEN + ", token_type = " + TOKEN_TYPE + ", scope = DELETE GET PATCH POST PUT" + ", expired = false]";
        assertEquals(builder, accessToken.toString());
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(AccessToken.class)
                .usingGetClass()
                .suppress(Warning.NULL_FIELDS, Warning.NONFINAL_FIELDS) // Neither Null nor modification is possible
                .verify();
    }

    private void given_an_expired_access_token() throws Exception {
        accessToken = tokenProvider.expired_access_token();
    }

    private void given_a_valid_access_token() throws IOException {
        accessToken = tokenProvider.valid_access_token();
    }

}
