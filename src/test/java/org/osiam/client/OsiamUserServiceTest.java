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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.matchers.Times;
import org.osiam.client.exception.BadRequestException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.QueryBuilder;
import org.osiam.resources.scim.User;

import javax.ws.rs.core.Response;
import java.util.Date;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class OsiamUserServiceTest {

    private static final int PORT_NUMBER = 9090;
    private static final String USER_ID = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private static final String endpoint = String.format("http://localhost:%d/osiam", PORT_NUMBER);

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, PORT_NUMBER);
    private MockServerClient mockServerClient;

    private String searchedID;
    private AccessToken accessToken;
    private OsiamUserService service;

    @Before
    public void setUp() throws Exception {
        service = new OsiamUserService.Builder(endpoint).build();
        searchedID = USER_ID;
        accessToken = new AccessToken.Builder("c5d116cb-2758-4e7c-9aca-4a115bc4f19e")
                .setExpiresAt(new Date(System.currentTimeMillis() * 2))
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void id_is_null_by_getting_single_user_raises_exception() throws Exception {
        service.getUser(null, accessToken);
    }

    @Test(expected = NullPointerException.class)
    public void access_token_is_null_by_getting_single_user_raises_exception() throws Exception {
        service.getUser(searchedID, null);
    }

    @Test(expected = NullPointerException.class)
    public void access_token_is_null_by_getting_all_group_raises_exception() throws Exception {
        service.getAllUsers(null);
    }

    @Test(expected = NullPointerException.class)
    public void create_null_user_raises_exception() {
        service.createUser(null, accessToken);
    }

    @Test(expected = NullPointerException.class)
    public void create_user_with_null_access_token_raises_exception() {
        User newUser = new User.Builder().build();
        service.createUser(newUser, null);
    }

    @Test(expected = BadRequestException.class)
    public void invalid_filter_generates_bad_request() {
        String filter = "invalidFilterString";
        mockServerClient
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/osiam/Users")
                                .withQueryStringParameter("filter", filter),
                        Times.once())
                .respond(response().withStatusCode(Response.Status.BAD_REQUEST.getStatusCode()));

        service.searchUsers(new QueryBuilder().filter(filter).build(), accessToken);
    }

    @Test(expected = IllegalArgumentException.class)
    public void delete_null_user_raises_exception() {
        service.deleteUser(null, accessToken);
    }

    @Test(expected = NullPointerException.class)
    public void delete_user_with_null_access_token_raises_exception() {
        service.deleteUser("irrelevant", null);
    }
}
