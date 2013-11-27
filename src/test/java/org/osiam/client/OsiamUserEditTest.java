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
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;

import org.apache.http.entity.ContentType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.update.UpdateUser;
import org.osiam.resources.scim.User;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class OsiamUserEditTest {

    private static final String URL_BASE = "/osiam-server//Users";
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9090); // No-args constructor defaults to port 8080

    final static private String ENDPOINT = "http://localhost:9090/osiam-server/";
    private static String UPDATE_USER_ID = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private UpdateUser updateUser;
    private AccessToken accessToken;
    private AccessTokenMockProvider tokenProvider;
    private User singleUserResult;
    private OsiamUserService service;

    @Before
    public void setUp() throws Exception {
        service = new OsiamUserService.Builder(ENDPOINT).build();
        tokenProvider = new AccessTokenMockProvider("/__files/valid_accesstoken.json");

        givenAnAccessToken();
        givenAnUpdateUser();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void id_is_null_by_updating_single_user_raises_exception() throws Exception {
    	givenIDisEmpty();
        UPDATE_USER_ID = null;
        updateSingleUser();
        fail("Exception expected");
    }
 
    @Test(expected = IllegalArgumentException.class)
    public void null_access_token__by_updating_user_rases_exception() throws Exception {
        givenIDisEmpty();
        accessToken = null;
        updateSingleUser();
        fail("Exception expected");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void null_UpdateUser_rases_exception() throws Exception {
        givenIDisEmpty();
        updateUser = null;
        updateSingleUser();
        fail("Exception expected");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void user_is_null_by_getting_single_user_raises_exception() throws Exception {
        givenIDisEmpty();

        updateSingleUserWithEmptyResource();
        fail("Exception expected");
    }

    private void givenAnAccessToken() throws IOException {
        this.accessToken = tokenProvider.valid_access_token();
    }
    
    private void givenAnUpdateUser() {
        this.updateUser = new UpdateUser.Builder().build();
    }

    private void updateSingleUser() {
        service.updateUser(UPDATE_USER_ID, updateUser, accessToken);
    }
    
    private void updateSingleUserWithEmptyResource() {
    	singleUserResult = null;
    	service.updateResource(UPDATE_USER_ID, singleUserResult, accessToken);
    }
    
    private void givenIDisEmpty() {
        stubFor(givenIDisLookedUp("", accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("query_all_users.json")));
    }    

    private MappingBuilder givenIDisLookedUp(String id, AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE + "/" + id))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }

}
