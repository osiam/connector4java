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

package org.osiam.client;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import org.junit.Test;
import org.osiam.client.exception.InvalidAttributeException;
import org.osiam.client.oauth.Scope;

public class AuthServiceTest {

    private final static String ENDPOINT = "http://localhost:9090/osiam-server";
    private final static String REDIRECT_URI = "http://mypage.com";
    private final static String VALID_CLIENT_ID = "valid-client";
    private final static String VALID_CLIENT_SECRET = "valid-secret";

    private OsiamConnector service;

    @Test
    public void service_returns_valid_redirect_Uri() throws Exception {
        service = new OsiamConnector.Builder().setAuthServerEndpoint(ENDPOINT)
                .setClientId(VALID_CLIENT_ID)
                .setClientSecret(VALID_CLIENT_SECRET)
                .setClientRedirectUri(REDIRECT_URI)
                .build();

        URI redirectUri = service.getAuthorizationUri(Scope.ALL);

        URI expectedUri = new URI(String.format(
                "%s/oauth/authorize?client_id=%s&response_type=code&redirect_uri=%s&scope=%s",
                ENDPOINT, VALID_CLIENT_ID, REDIRECT_URI, encodeExpectedString(Scope.ALL.toString())));
        assertThat(redirectUri, is(equalTo(expectedUri)));
    }

    @Test(expected = InvalidAttributeException.class)
    public void request_access_token_without_setting_endpoint_raises_exception() {
        OsiamConnector oConnector = new OsiamConnector.Builder()
                .setClientId(VALID_CLIENT_ID)
                .setClientSecret(VALID_CLIENT_SECRET)
                .build();

        oConnector.retrieveAccessToken();

        fail("Exception expected");
    }

    private static String encodeExpectedString(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            fail("Filter contains non UTF-8 characters");
        }

        return "";
    }
}
