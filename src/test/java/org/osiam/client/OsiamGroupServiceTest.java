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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.matchers.Times;
import org.osiam.client.exception.BadRequestException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.QueryBuilder;
import org.osiam.resources.scim.Group;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class OsiamGroupServiceTest {


    private static final int PORT_NUMBER = 9191;
    private static final String GROUP_ID_STRING = "55bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private static final String endpoint = String.format("http://localhost:%d/osiam", PORT_NUMBER);

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, false, PORT_NUMBER);
    private MockServerClient mockServerClient;

    private String searchedId;
    private AccessToken accessToken;
    private OsiamGroupService service;

    @Before
    public void setUp() throws IOException {
        service = new OsiamGroupService(endpoint, 0, 0, null);
        searchedId = GROUP_ID_STRING;
        accessToken = new AccessToken.Builder("c5d116cb-2758-4e7c-9aca-4a115bc4f19e")
                .setExpiresAt(new Date(System.currentTimeMillis() * 2))
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void id_is_null_by_getting_single_user_raises_exception() throws Exception {
        service.getGroup(null, accessToken);
    }

    @Test(expected = NullPointerException.class)
    public void accessToken_is_null_by_getting_single_group_raises_exception() throws Exception {
        service.getGroup(searchedId, null);
    }

    @Test(expected = NullPointerException.class)
    public void accessToken_is_null_by_getting_all_group_raises_exception() throws Exception {
        service.getAllGroups(null);
    }

    @Test(expected = NullPointerException.class)
    public void create_null_group_raises_exception() {
        service.createGroup(null, accessToken);
    }

    @Test(expected = NullPointerException.class)
    public void create_group_with_null_accestoken_raises_exception() {
        Group newGroup = new Group.Builder().build();

        service.createGroup(newGroup, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void delete_null_group_raises_exception() {
        service.deleteGroup(null, accessToken);
    }

    @Test(expected = NullPointerException.class)
    public void delete_group_with_null_accestoken_raises_exception() {
        service.deleteGroup("irrelevant", null);
    }

    @Test(expected = BadRequestException.class)
    public void invalid_filter_generates_bad_request() {
        String filter = "invalidFilterString";
        mockServerClient
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/osiam/Groups")
                                .withQueryStringParameter("filter", filter),
                        Times.once())
                .respond(response().withStatusCode(Response.Status.BAD_REQUEST.getStatusCode()));

        service.searchGroups(new QueryBuilder().filter(filter).build(), accessToken);
    }

    @Test
    public void passing_attributes_to_a_request_for_all_groups_generates_the_correct_request() {
        mockServerClient
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/osiam/Groups")
                                .withQueryStringParameter("attributes", "displayName"),
                        Times.once())
                .respond(response().withBody(
                        "{ \"schemas\":[\"urn:ietf:params:scim:api:messages:2.0:ListResponse\"],"
                                + "\"totalResults\":2,"
                                + "\"Resources\":["
                                + "{"
                                + "\"schemas\":[\"urn:ietf:params:scim:schemas:core:2.0:Group\"],"
                                + "\"id\":\"2819c223-7f76-453a-919d-413861904646\","
                                + "\"displayName\":\"finance\""
                                + "},"
                                + "{"
                                + "\"id\":\"c75ad752-64ae-4823-840d-ffa80929976c\","
                                + "\"schemas\":[\"urn:ietf:params:scim:schemas:core:2.0:Group\"],"
                                + "\"displayName\":\"operations\""
                                + "}]}"));
        service.getAllGroups(accessToken, "displayName");
    }


    @Test
    public void passing_attributes_for_single_group_creates_the_correct_request() {
        String groupId = "irrelevant";
        mockServerClient
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/osiam/Groups/" + groupId)
                                .withQueryStringParameter("attributes", "displayName"),
                        Times.once())
                .respond(response().withBody("{\"id\":\"irrelevant\", "
                        + "\"schemas\":[\"urn:ietf:params:scim:schemas:core:2.0:Group\"],"
                        + "\"id\":\"" + groupId + "\","
                        + "\"displayName\":\"finance\" "
                        + "}"));
        service.getGroup(groupId, accessToken, "displayName");
    }

}
