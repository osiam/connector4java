/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.client.oauth;

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
    private final static String REFRESH_TOKEN = "c5d116cb-5568-4e7c-9aca-4a115bc4f19e";
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
        String builder = "[access_token = " + TOKEN + ", token_type = " + TOKEN_TYPE + ", scope = DELETE GET PATCH POST PUT" + ", expired = false, refresh_token = " + REFRESH_TOKEN + "]";
        assertEquals(builder, accessToken.toString());
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(AccessToken.class)
                .usingGetClass()
                .suppress(Warning.NULL_FIELDS, Warning.NONFINAL_FIELDS) // Neither Null nor modification is possible
                .verify();
    }
    
    @Test
    public void staticOfCreatesEqualAccessTokens(){
        AccessToken accessToken01 = AccessToken.of("token");
        AccessToken accessToken02 = AccessToken.of("token");
        assertTrue(accessToken01.equals(accessToken02));
    }

    private void given_an_expired_access_token() throws Exception {
        accessToken = tokenProvider.expired_access_token();
    }

    private void given_a_valid_access_token() throws IOException {
        accessToken = tokenProvider.valid_access_token();
    }

}
