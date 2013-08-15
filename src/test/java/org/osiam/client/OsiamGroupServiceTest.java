package org.osiam.client;
/*
 * for licensing see the file license.txt.
 */

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.QueryResult;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.MultiValuedAttribute;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class OsiamGroupServiceTest {

    private static final String URL_BASE = "/osiam-server//Groups";
    @Rule
    public WireMockClassRule wireMockRule = new WireMockClassRule(9090); // No-args constructor defaults to port 8080

    final static private String GROUP_UUID_STRING = "55bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    final static private String INVALID_GROUP_UUID_STRING = "55bbe688-4b1e-4e4e-80e7-e5ba5c4d";
    final static private String USER_UUID_STRING = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";

    final static private String endpoint = "http://localhost:9090/osiam-server/";

    final static private int NUMBER_OF_EXPECTED_GROUPS = 7;
    final static private String SIMPLE_QUERY_STRING = "filter=displayName eq test_group01";

    private UUID SEARCHED_UUID;

    private AccessToken accessToken;
    private AccessTokenMockProvider tokenProvider;

    private Group singleGroupResult;
    private QueryResult<Group> queryResult;

    OsiamGroupService service;

    @Before
    public void setUp() throws IOException {
        service = new OsiamGroupService.Builder(endpoint).build();
        tokenProvider = new AccessTokenMockProvider("/__files/valid_accesstoken.json");

        givenAGroupUUID();
        givenAnAccessToken();
    }

    @Test
    public void service_returns_correct_uri() throws Exception {
        assertEquals(new URI(endpoint + "/Groups"), service.getUri());
    }

    @Test
    public void existing_group_is_returned() throws IOException {
        givenUUIDcanBeFound();
        whenSingleGroupIsLookedUp();
        thenReturnedGroupHasUUID(SEARCHED_UUID);
    }

    @Test
    public void group_has_valid_values() throws Exception {
        givenUUIDcanBeFound();
        whenSingleGroupIsLookedUp();
        thenReturnedGroupIsAsExpected();
    }

    @Test
    public void list_of_groups_is_returned() throws Exception {
        givenAllGroupsAreLookedUpSuccessfully();
        whenAllGroupsAreLookedUp();
        thenReturnedListOfAllGroupsIsAsExpected();
    }

    @Test
    public void search_for_single_group_is_successful() {
        givenASingleGroupCanBeLookedUpByQuery();
        whenSingleGroupIsSearchedByQueryString(SIMPLE_QUERY_STRING);
        thenQueryWasValid();
        thenReturnedListOfSearchedGroupsIsAsExpected();
    }

    @Test(expected = IllegalArgumentException.class)
    public void uuid_is_null_by_getting_single_user_raises_exception() throws Exception {
        givenUUIDisEmpty();
        SEARCHED_UUID = null;
        whenSingleGroupIsLookedUp();
        fail("Exception expected");
    }

    @Test(expected = IllegalArgumentException.class)
    public void accessToken_is_null_by_getting_single_group_raises_exception() throws Exception {
        givenUUIDisEmpty();
        accessToken = null;
        whenSingleGroupIsLookedUp();
        fail("Exception expected");
    }

    @Test(expected = IllegalArgumentException.class)
    public void accessToken_is_null_by_getting_all_group_raises_exception() throws Exception {
        givenUUIDisEmpty();
        accessToken = null;
        whenAllGroupsAreLookedUp();
        fail("Exception expected");
    }

    @Test(expected = IllegalArgumentException.class)
    public void accessToken_is_null_by_searching_for_group_by_string_raises_exception() throws Exception {
        givenUUIDisEmpty();
        accessToken = null;
        whenSingleGroupIsSearchedByQueryString("filter=meta.version=3");
        fail("Exception expected");
    }

    @Test(expected = NoResultException.class)
    public void group_does_not_exist() throws IOException {
        givenUUIDcanNotBeFound();
        whenSingleGroupIsLookedUp();
        fail("Exception expected");
    }

    @Test(expected = NoResultException.class)
    public void invalid_UUID_search() throws IOException {
        givenUUIDisInvalid();
        whenSingleGroupIsLookedUp();
        fail("Exception expected");
    }

    @Test(expected = NoResultException.class)
    public void invalid_UUID_is_star() throws IOException {
        givenUUIDisSpecial("*");
        whenSingleGroupIsLookedUp();
        fail("Exception expected");
    }

    @Test(expected = NoResultException.class)
    public void invalid_UUID_is_dot() throws IOException {
        givenUUIDisSpecial(".");
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

    private void givenAnAccessToken() throws IOException {
        this.accessToken = tokenProvider.valid_access_token();
    }

    private void givenAGroupUUID() {
        this.SEARCHED_UUID = UUID.fromString(GROUP_UUID_STRING);
    }

    private void whenSingleGroupIsLookedUp() {
        singleGroupResult = service.getGroupByUUID(SEARCHED_UUID, accessToken);
    }

    private void whenAllGroupsAreLookedUp() {
        queryResult = service.getAllGroups(accessToken);
    }

    private void whenSingleGroupIsSearchedByQueryString(String queryString) {
        queryResult = service.searchGroups(queryString, accessToken);
    }

    private void givenExpiredAccessTokenIsUsedForLookup() {
        stubFor(givenUUIDisLookedUp(GROUP_UUID_STRING, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_UNAUTHORIZED)));
    }

    private void givenInvalidAccessTokenIsUsedForLookup() {
        stubFor(givenUUIDisLookedUp(GROUP_UUID_STRING, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_UNAUTHORIZED)));
    }

    private void givenUUIDcanNotBeFound() {
        stubFor(givenUUIDisLookedUp(GROUP_UUID_STRING, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_NOT_FOUND)));
    }

    private void givenUUIDisInvalid() {
        stubFor(givenUUIDisLookedUp(INVALID_GROUP_UUID_STRING, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_NOT_FOUND)));
    }


    private void givenUUIDisSpecial(String wildcard) {
        stubFor(givenUUIDisLookedUp(wildcard, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_CONFLICT)));
    }

    private void givenASingleGroupCanBeLookedUpByQuery() {
        stubFor(get(urlEqualTo(URL_BASE + "?access_token=" + accessToken.getToken()
                + "&filter=displayName+eq+test_group01"))
                .withHeader("Content-Type", equalTo(APPLICATION_JSON))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBodyFile("query_group_by_name.json")));
    }

    private void givenUUIDcanBeFound() {
        stubFor(givenUUIDisLookedUp(GROUP_UUID_STRING, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBodyFile("group_" + GROUP_UUID_STRING + ".json")));
    }

    private void givenUUIDisEmpty() {
        stubFor(givenUUIDisLookedUp("", accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBodyFile("query_all_groups.json")));
    }


    private void givenAllGroupsAreLookedUpSuccessfully() {
        stubFor(get(urlEqualTo(URL_BASE))
                .withHeader("Content-Type", equalTo(APPLICATION_JSON))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBodyFile("query_all_groups.json")));
    }

    private MappingBuilder givenUUIDisLookedUp(String uuidString, AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE + "/" + uuidString))
                .withHeader("Content-Type", equalTo(APPLICATION_JSON))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }

    private void thenReturnedGroupHasUUID(UUID uuid) {
        Group result = service.getGroupByUUID(uuid, accessToken);
        assertEquals(uuid.toString(), result.getId());
    }

    private void thenQueryWasValid() {
        verify(getRequestedFor(urlEqualTo(URL_BASE + "?access_token=" + accessToken.getToken()
                + "&filter=displayName+eq+test_group01"))
                .withHeader("Content-Type", equalTo(APPLICATION_JSON)));
    }

    private void thenReturnedListOfSearchedGroupsIsAsExpected() {
        assertEquals(1, queryResult.getTotalResults());
        assertEquals(1, queryResult.getResources().size());
        assertEquals("test_group01", queryResult.getResources().iterator().next().getDisplayName());
    }

    private void thenReturnedListOfAllGroupsIsAsExpected() {
        assertEquals(NUMBER_OF_EXPECTED_GROUPS, queryResult.getTotalResults());
        for (Group currentGroup : queryResult.getResources()) {
            if (currentGroup.getId().equals(GROUP_UUID_STRING)) {
                assertEquals(1, currentGroup.getMembers().size());
                for (MultiValuedAttribute actValue : currentGroup.getMembers()) {
                    assertEquals(USER_UUID_STRING, actValue.getValue().toString());
                }
                break;
            }
        }
    }

    private void thenReturnedGroupIsAsExpected() throws Exception {

        assertEquals(GROUP_UUID_STRING, singleGroupResult.getId());
        assertEquals("Group", singleGroupResult.getMeta().getResourceType());

        Date created = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2011-08-01 20:29:49");

        assertEquals(created, singleGroupResult.getMeta().getCreated());
        assertEquals(created, singleGroupResult.getMeta().getLastModified());
        assertEquals("test_group01", singleGroupResult.getDisplayName());

        Set<MultiValuedAttribute> users = singleGroupResult.getMembers();
        int count = 0;
        for (MultiValuedAttribute multiValuedAttribute : users) {
            Object value = multiValuedAttribute.getValue();
            assertTrue(value.getClass().equals(String.class));
            String userId = (String) multiValuedAttribute.getValue();
            assertEquals(USER_UUID_STRING, userId);
            count++;
        }
        assertEquals(1, count);
    }
}
