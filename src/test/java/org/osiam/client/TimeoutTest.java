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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.oauth.AccessToken;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class TimeoutTest {

    private final static String ENDPOINT = "http://localhost:9090";
    private static final String URL_BASE_USERS = "/osiam-resource-server/Users";
    private static final String URL_BASE_GROUPS = "/osiam-resource-server/Groups";
    private static final String userIdString = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private static final String GROUP_ID_STRING = "55bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private static final String VALID_CLIENT_ID = "valid-client";
    private static final String VALID_CLIENT_SECRET = "valid_secret";
    private static final String TOKEN_PATH = "/oauth/token";

    private AccessToken accessToken;
    private String searchedUserID;
    private String searchedGroupId;
    private AccessTokenMockProvider tokenProvider;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9090); // No-args constructor defaults to port 8080
    private OsiamConnector oConnector;

    @Before
    public void setUp() throws Exception {
        tokenProvider = new AccessTokenMockProvider("/__files/valid_accesstoken.json");
        givenAnUserID();
        givenAGroupID();
        givenAnAccessToken();
    }
    
    @Test (expected = ConnectionInitializationException.class)
    public void retrieveAccessToken_is_transferred_correctly() throws Exception {
        given_a_correctly_configured_service_with_short_readTimeOut();
        given_oauth_server_issues_access_token_with_long_delay();
        oConnector.retrieveAccessToken(); 
        fail("ReadTimeout should have happen.");
    }
    
    @Test (expected = ConnectionInitializationException.class)
    public void retrieve_user_with_short_read_timeout_fails() throws Exception {
        given_a_correctly_configured_service_with_short_readTimeOut();
        given_user_resource_server_issues_with_long_delay();
        oConnector.getUser(searchedUserID, accessToken);
        fail("ReadTimeout should have happen.");
    }
    
    @Test (expected = ConnectionInitializationException.class)
    public void retrieve_group_with_short_read_timeout_fails() throws Exception {
        given_a_correctly_configured_service_with_short_readTimeOut();
        given_groups_resource_server_issues_with_long_delay();
        oConnector.getGroup(searchedGroupId, accessToken);
        fail("ReadTimeout should have happen.");
    }
    
    private void given_a_correctly_configured_service_with_short_readTimeOut() {
        oConnector = new OsiamConnector.Builder()
                .setEndpoint(ENDPOINT)
                .setClientId(VALID_CLIENT_ID)
                .setClientSecret(VALID_CLIENT_SECRET)
                .setReadTimeout(100)
                .build();
    }

    private void given_groups_resource_server_issues_with_long_delay(){
        stubFor(givenGroupIDisLookedUp(GROUP_ID_STRING, accessToken)
                .willReturn(aResponse()
                        .withFixedDelay(1000)
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                        .withBodyFile("group_" + GROUP_ID_STRING + ".json")));
    }

    private MappingBuilder givenGroupIDisLookedUp(String id, AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE_GROUPS + "/" + id))
                .withHeader("Accept", equalTo(MediaType.APPLICATION_JSON))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }
    
    private void given_user_resource_server_issues_with_long_delay(){
        stubFor(givenUserIDisLookedUp(userIdString, accessToken)
                .willReturn(aResponse()
                        .withFixedDelay(20000)
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                        .withBodyFile("user_" + userIdString + ".json")));
    }

    private MappingBuilder givenUserIDisLookedUp(String id, AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE_USERS + "/" + id))
                .withHeader("Accept", equalTo(MediaType.APPLICATION_JSON))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }
    
    private void given_oauth_server_issues_access_token_with_long_delay() {
        stubFor(post(urlEqualTo("/osiam-auth-server" + TOKEN_PATH)).
                willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withFixedDelay(20000)
                        
                        .withBodyFile("valid_accesstoken.json")));
    }
    
    private void givenAnUserID() {
        searchedUserID = userIdString;
    }
    
    private void givenAGroupID() {
        searchedGroupId = GROUP_ID_STRING;
    }
    
    private void givenAnAccessToken() throws IOException {
        accessToken = tokenProvider.valid_access_token();
    }

}
