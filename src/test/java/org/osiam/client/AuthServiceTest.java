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
package org.osiam.client;

import org.junit.Test;
import org.osiam.client.oauth.Scope;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import static com.jcabi.matchers.RegexMatchers.containsPattern;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class AuthServiceTest {

    private final static String ENDPOINT = "http://localhost:9090/osiam";
    private final static String REDIRECT_URI = "http://mypage.com";
    private final static String VALID_CLIENT_ID = "valid-client";
    private final static String VALID_CLIENT_SECRET = "valid-secret";

    private OsiamConnector service;

    @Test
    public void service_returns_valid_redirect_Uri() throws Exception {
        service = new OsiamConnector.Builder()
                .withEndpoint(ENDPOINT)
                .setClientId(VALID_CLIENT_ID)
                .setClientSecret(VALID_CLIENT_SECRET)
                .setClientRedirectUri(REDIRECT_URI)
                .build();

        URI redirectUri = service.getAuthorizationUri(Scope.ADMIN);

        URI expectedUri = new URI(String.format(
                "%s/oauth/authorize?client_id=%s&response_type=code&redirect_uri=%s&scope=%s",
                ENDPOINT, VALID_CLIENT_ID, encodeExpectedString(REDIRECT_URI), encodeExpectedString(Scope.ADMIN.toString())));
        assertThat(redirectUri, is(equalTo(expectedUri)));
    }

    @Test
    public void double_slash_is_removed_from_path() throws Exception {
        service = new OsiamConnector.Builder().setEndpoint(ENDPOINT + "/")
                .setClientId(VALID_CLIENT_ID)
                .setClientSecret(VALID_CLIENT_SECRET)
                .setClientRedirectUri(REDIRECT_URI)
                .build();

        URI redirectUri = service.getAuthorizationUri(Scope.ADMIN);
        System.out.println(redirectUri.toString());
        assertThat(redirectUri.getPath(), not(containsPattern("//")));
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
