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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.*;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.oauth.AccessToken;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class TimeoutTest {

    private final static String ENDPOINT = "http://localhost:9090";

    private static final String URL_BASE_USERS = "/osiam-resource-server/Users";
    private static final String URL_BASE_GROUPS = "/osiam-resource-server/Groups";
    private static final String URL_BASE_TOKEN = "/oauth/token";

    private static final String USER_ID = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private static final String GROUP_ID = "55bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private static final String VALID_CLIENT_ID = "valid-client";
    private static final String VALID_CLIENT_SECRET = "valid_secret";

    private static final int READ_TIMEOUT_DEFAULT = 5000;
    private static final int READ_TIMEOUT = 100;

    private static final OsiamConnector connector = new OsiamConnector.Builder()
            .setEndpoint(ENDPOINT)
            .setClientId(VALID_CLIENT_ID)
            .setClientSecret(VALID_CLIENT_SECRET)
            .build();

    private static AccessToken accessToken;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9090); // No-args constructor defaults to port 8080

    @BeforeClass
    public static void initializeAccessToken() throws Exception {
        accessToken = new AccessTokenMockProvider("/__files/valid_accesstoken.json").valid_access_token();
    }

    @BeforeClass
    public static void setCustomTimeout() throws Exception {
        OsiamConnector.setReadTimeout(READ_TIMEOUT);
    }

    @AfterClass
    public static void restoreDefaultTimeout() throws Exception {
        OsiamConnector.setReadTimeout(READ_TIMEOUT_DEFAULT);
    }


    @Test(expected = ConnectionInitializationException.class)
    public void retrieve_access_token_with_short_read_timeout_fails() throws Exception {
        stubFor(post(urlEqualTo("/osiam-auth-server" + URL_BASE_TOKEN))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withFixedDelay(READ_TIMEOUT * 10)
                        .withBodyFile("valid_accesstoken.json")));

        connector.retrieveAccessToken();

        fail("read timeout should have happened.");
    }

    @Test(expected = ConnectionInitializationException.class)
    public void retrieve_user_with_short_read_timeout_fails() throws Exception {
        stubFor(get(urlEqualTo(URL_BASE_USERS + "/" + USER_ID))
                .withHeader("Accept", equalTo(MediaType.APPLICATION_JSON))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()))
                .willReturn(aResponse()
                        .withFixedDelay(READ_TIMEOUT * 2)
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                        .withBodyFile("user_" + USER_ID + ".json")));

        connector.getUser(USER_ID, accessToken);

        fail("read timeout should have happened.");
    }

    @Test(expected = ConnectionInitializationException.class)
    public void retrieve_group_with_short_read_timeout_fails() throws Exception {
        stubFor(get(urlEqualTo(URL_BASE_GROUPS + "/" + GROUP_ID))
                .withHeader("Accept", equalTo(MediaType.APPLICATION_JSON))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()))
                .willReturn(aResponse()
                        .withFixedDelay(READ_TIMEOUT * 2)
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                        .withBodyFile("group_" + GROUP_ID + ".json")));

        connector.getGroup(GROUP_ID, accessToken);

        fail("read timeout should have happened.");
    }

}
