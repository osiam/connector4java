/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.client.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

public class AccessTokenTest {

    private final static String TOKEN = "c5d116cb-2758-4e7c-9aca-4a115bc4f19e";
    private AccessToken accessToken;

    @Test
    public void access_token_is_deserialized_without_errors() throws IOException {
        accessToken = new AccessToken.Builder("c5d116cb-2758-4e7c-9aca-4a115bc4f19e")
                .setExpiresAt(new Date(System.currentTimeMillis() * 2))
                .build();

        assertEquals(TOKEN, accessToken.getToken());
        assertFalse(accessToken.isExpired());
    }

    @Test
    public void expired_access_token_is_recognized_correctly() throws Exception {
        accessToken = new AccessToken.Builder("c5d116cb-2758-4e7c-9aca-4a115bc4f19e")
                .setExpiresAt(new Date(Long.MIN_VALUE))
                .build();

        assertTrue(accessToken.isExpired());
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(AccessToken.class)
                .usingGetClass()
                .suppress(Warning.NULL_FIELDS, Warning.NONFINAL_FIELDS)
                .verify();
    }

}
