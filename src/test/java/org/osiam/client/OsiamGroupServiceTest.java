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
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.MemberRef;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class OsiamGroupServiceTest {

    private static final String URL_BASE = "/osiam-server/Groups";
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9090); // No-args constructor defaults to port 8080

    private static final String GROUP_ID_STRING = "55bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private static final String INVALID_GROUP_ID_STRING = "55bbe688-4b1e-4e4e-80e7-e5ba5c4d";
    private static final String USER_ID_STRING = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private static final String ENDPOINT = "http://localhost:9090/osiam-server";
    private static final int NUMBER_OF_EXPECTED_GROUPS = 7;

    private String searchedId;
    private AccessToken accessToken;
    private AccessTokenMockProvider tokenProvider;
    private Group singleGroupResult;
    private List<Group> allGroups;
    private OsiamGroupService service;

    @Before
    public void setUp() throws IOException {
        service = new OsiamGroupService.Builder(ENDPOINT).build();
        tokenProvider = new AccessTokenMockProvider("/__files/valid_accesstoken.json");

        givenAGroupID();
        givenAnAccessToken();
    }

    @Test
    public void existing_group_is_returned() throws IOException {
        givenIDcanBeFound();
        whenSingleGroupIsLookedUp();
        thenReturnedGroupHasID(searchedId);
    }

    @Test
    public void group_has_valid_values() throws Exception {
        givenIDcanBeFound();
        whenSingleGroupIsLookedUp();
        thenReturnedGroupIsAsExpected();
    }

    @Test
    public void list_of_groups_is_returned() throws Exception {
        givenAllGroupsAreLookedUpSuccessfully();
        whenAllGroupsAreLookedUp();
        thenReturnedListOfAllGroupsIsAsExpected();
    }

    @Test(expected = IllegalArgumentException.class)
    public void id_is_null_by_getting_single_user_raises_exception() throws Exception {
        givenIDisEmpty();
        searchedId = null;
        whenSingleGroupIsLookedUp();
        fail("Exception expected");
    }

    @Test(expected = NullPointerException.class)
    public void accessToken_is_null_by_getting_single_group_raises_exception() throws Exception {
        givenIDisEmpty();
        accessToken = null;
        whenSingleGroupIsLookedUp();
        fail("Exception expected");
    }

    @Test(expected = NullPointerException.class)
    public void accessToken_is_null_by_getting_all_group_raises_exception() throws Exception {
        givenIDisEmpty();
        accessToken = null;
        whenAllGroupsAreLookedUp();
        fail("Exception expected");
    }

    @Test(expected = NoResultException.class)
    public void group_does_not_exist() throws IOException {
        givenIDcanNotBeFound();
        whenSingleGroupIsLookedUp();
        fail("Exception expected");
    }

    @Test(expected = NoResultException.class)
    public void invalid_ID_search() throws IOException {
        givenIDisInvalid();
        whenSingleGroupIsLookedUp();
        fail("Exception expected");
    }

    @Test(expected = NoResultException.class)
    public void invalid_ID_is_star() throws IOException {
        givenIDisSpecial("*");
        whenSingleGroupIsLookedUp();
        fail("Exception expected");
    }

    @Test(expected = NoResultException.class)
    public void invalid_ID_is_dot() throws IOException {
        givenIDisSpecial(".");
        whenSingleGroupIsLookedUp();
        fail("Exception expected");
    }

    @Test(expected = UnauthorizedException.class)
    public void expired_access_token() throws Exception {
        givenExpiredAccessTokenIsUsedForLookup();
        whenSingleGroupIsLookedUp();
        fail("Exception expected");
    }

    @Test(expected = UnauthorizedException.class)
    public void invalid_access_token() throws Exception {
        givenInvalidAccessTokenIsUsedForLookup();
        whenSingleGroupIsLookedUp();
        fail("Exception expected");
    }

    @Test (expected = NullPointerException.class)
    public void create_null_group_raises_exception(){
        Group newGroup = null;
        service.createGroup(newGroup, accessToken);
        Assert.fail("Exception excpected");
    }

    @Test (expected = NullPointerException.class)
    public void create_group_with_null_accestoken_raises_exception(){
        Group newGroup = new Group.Builder().build();
        service.createGroup(newGroup, null);
        Assert.fail("Exception excpected");
    }

    @Test (expected = IllegalArgumentException.class)
    public void delete_null_group_raises_exception(){
        String groupID = null;
        service.deleteGroup(groupID, accessToken);
        Assert.fail("Exception excpected");
    }

    @Test(expected = NullPointerException.class)
    public void delete_group_with_null_accestoken_raises_exception(){
        String id = "HelloWorld";
        service.deleteGroup(id, null);
        Assert.fail("Exception excpected");
    }

    private void givenAnAccessToken() throws IOException {
        accessToken = tokenProvider.valid_access_token();
    }

    private void givenAGroupID() {
        searchedId = GROUP_ID_STRING;
    }

    private void whenSingleGroupIsLookedUp() {
        singleGroupResult = service.getGroup(searchedId, accessToken);
    }

    private void whenAllGroupsAreLookedUp() {
        allGroups = service.getAllGroups(accessToken);
    }

    private void givenExpiredAccessTokenIsUsedForLookup() {
        stubFor(givenIDisLookedUp(GROUP_ID_STRING, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_UNAUTHORIZED)
                        .withHeader("WWW-Authenticate", "None")));
    }

    private void givenInvalidAccessTokenIsUsedForLookup() {
        stubFor(givenIDisLookedUp(GROUP_ID_STRING, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_UNAUTHORIZED)
                        .withHeader("WWW-Authenticate", "None")));
    }

    private void givenIDcanNotBeFound() {
        stubFor(givenIDisLookedUp(GROUP_ID_STRING, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_NOT_FOUND)));
    }

    private void givenIDisInvalid() {
        stubFor(givenIDisLookedUp(INVALID_GROUP_ID_STRING, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_NOT_FOUND)));
    }

    private void givenIDisSpecial(String wildcard) {
        stubFor(givenIDisLookedUp(wildcard, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_CONFLICT)));
    }

    private void givenIDcanBeFound() {
        stubFor(givenIDisLookedUp(GROUP_ID_STRING, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                        .withBodyFile("group_" + GROUP_ID_STRING + ".json")));
    }

    private void givenIDisEmpty() {
        stubFor(givenIDisLookedUp("", accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                        .withBodyFile("query_all_groups.json")));
    }

    private void givenAllGroupsAreLookedUpSuccessfully() {
        stubFor(get(urlEqualTo(URL_BASE + "?count=2147483647"))
                .withHeader("Accept", equalTo(MediaType.APPLICATION_JSON))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                        .withBodyFile("query_all_groups.json")));
    }

    private MappingBuilder givenIDisLookedUp(String id, AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE + "/" + id))
                .withHeader("Accept", equalTo(MediaType.APPLICATION_JSON))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }

    private void thenReturnedGroupHasID(String id) {
        Group result = service.getGroup(id, accessToken);
        assertEquals(id.toString(), result.getId());
    }

    private void thenReturnedListOfAllGroupsIsAsExpected() {
        assertEquals(NUMBER_OF_EXPECTED_GROUPS, allGroups.size());
        for (Group currentGroup : allGroups) {
            if (currentGroup.getId().equals(GROUP_ID_STRING)) {
                assertEquals(1, currentGroup.getMembers().size());
                for (MemberRef actValue : currentGroup.getMembers()) {
                    assertEquals(USER_ID_STRING, actValue.getValue().toString());
                }
                break;
            }
        }
    }

    private void thenReturnedGroupIsAsExpected() throws Exception {

        assertEquals(GROUP_ID_STRING, singleGroupResult.getId());
        assertEquals("Group", singleGroupResult.getMeta().getResourceType());

        Date created = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2011-08-01 20:29:49");

        assertEquals(created, singleGroupResult.getMeta().getCreated());
        assertEquals(created, singleGroupResult.getMeta().getLastModified());
        assertEquals("test_group01", singleGroupResult.getDisplayName());

        Set<MemberRef> users = singleGroupResult.getMembers();
        int count = 0;
        for (MemberRef multiValuedAttribute : users) {
            Object value = multiValuedAttribute.getValue();
            assertTrue(value.getClass().equals(String.class));
            String userId = multiValuedAttribute.getValue();
            assertEquals(USER_ID_STRING, userId);
            count++;
        }
        assertEquals(1, count);
    }
}
