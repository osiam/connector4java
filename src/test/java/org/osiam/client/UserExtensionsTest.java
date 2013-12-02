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
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.http.entity.ContentType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osiam.client.oauth.AccessToken;
import org.osiam.resources.scim.User;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class UserExtensionsTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9090);

    private final static String URL_BASE = "/osiam-auth-server//Users";
    private final static String ENDPOINT = "http://localhost:9090/osiam-auth-server/";
    private final static String USER_ID_WITHOUT_EXTENSION = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private final static String USER_ID_WITH_EXTENSION = "a4bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private final static String ENTERPRISE_EXTENSION_URN = "urn:scim:schemas:extension:enterprise:2.0:User";
    private AccessToken accessToken;
    private AccessTokenMockProvider tokenProvider;
    private OsiamUserService service;
    private User singleUserResult;
    private String userIdToRetrieve;

    @Before
    public void setUp() throws Exception {
        service = new OsiamUserService.Builder(ENDPOINT).build();
        tokenProvider = new AccessTokenMockProvider("/__files/valid_accesstoken.json");

        givenAnAccessToken();
    }

    @Test
    public void the_extension_map_is_empty_if_the_user_has_no_extensions() {
        givenAnUserIdWithoutExtension();
        givenUserIdIsLookedUp();

        whenSingleUserisRetrieved();

        assertEquals(0, singleUserResult.getAllExtensions().size());
    }

    @Test
    public void if_an_extension_is_defined_its_urn_is_listed_in_the_schema_set() {
        givenAnUserIdWithExtension();
        givenUserIdIsLookedUp();

        whenSingleUserisRetrieved();

        assertTrue(singleUserResult.getAllExtensions().containsKey(ENTERPRISE_EXTENSION_URN));
    }


    private void givenAnUserIdWithoutExtension() {
        userIdToRetrieve = USER_ID_WITHOUT_EXTENSION;
    }

    private void givenAnUserIdWithExtension() {
        userIdToRetrieve = USER_ID_WITH_EXTENSION;
    }

    private void whenSingleUserisRetrieved() {
        singleUserResult = service.getUser(userIdToRetrieve, accessToken);
    }

    private void givenAnAccessToken() throws IOException {
        this.accessToken = tokenProvider.valid_access_token();
    }

    private void givenUserIdIsLookedUp() {
        stubFor(givenIDisLookedUp(userIdToRetrieve, accessToken).willReturn(
                aResponse().withStatus(SC_OK).withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("user_" + userIdToRetrieve + ".json")));
    }

    private MappingBuilder givenIDisLookedUp(String id, AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE + "/" + id)).withHeader("Content-Type",
                equalTo(ContentType.APPLICATION_JSON.getMimeType())).withHeader("Authorization",
                equalTo("Bearer " + accessToken.getToken()));
    }
}
