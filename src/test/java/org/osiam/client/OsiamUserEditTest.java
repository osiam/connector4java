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
import static org.junit.Assert.fail;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osiam.client.oauth.AccessToken;
import org.osiam.resources.scim.UpdateUser;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class OsiamUserEditTest {

    private static final String URL_BASE = "/osiam-server/Users";
    private static final String ENDPOINT = "http://localhost:9090/osiam-server";
    private static final String UPDATE_USER_ID = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9090);

    private AccessToken accessToken;
    private OsiamUserService service = new OsiamUserService.Builder(ENDPOINT).build();

    @Before
    public void setUp() throws Exception {
        accessToken = new AccessTokenMockProvider("/__files/valid_accesstoken.json").valid_access_token();
        prepareWireMock();
    }

    @Test(expected = IllegalArgumentException.class)
    public void id_is_null_by_updating_single_user_raises_exception() throws Exception {
        UpdateUser updateUser = new UpdateUser.Builder().build();

        service.updateUser(null, updateUser, accessToken);

        fail("Exception expected");
    }

    @Test(expected = NullPointerException.class)
    public void null_access_token__by_updating_user_rases_exception() throws Exception {
        UpdateUser updateUser = new UpdateUser.Builder().build();

        service.updateUser(UPDATE_USER_ID, updateUser, null);

        fail("Exception expected");
    }

    @Test(expected = IllegalArgumentException.class)
    public void null_UpdateUser_rases_exception() throws Exception {
        service.updateUser(UPDATE_USER_ID, null, accessToken);

        fail("Exception expected");
    }

    @Test(expected = NullPointerException.class)
    public void user_is_null_by_getting_single_user_raises_exception() throws Exception {
        service.updateResource(UPDATE_USER_ID, null, accessToken);

        fail("Exception expected");
    }

    private void prepareWireMock() {
        stubFor(createGetMapping(accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                        .withBodyFile("query_all_users.json")));
    }

    private MappingBuilder createGetMapping(AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE + "/"))
                .withHeader("Accept", equalTo(MediaType.APPLICATION_JSON))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }

}