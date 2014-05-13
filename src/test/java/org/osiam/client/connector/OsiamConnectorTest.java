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

package org.osiam.client.connector;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osiam.client.AccessTokenMockProvider;
import org.osiam.client.exception.InvalidAttributeException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.GrantType;
import org.osiam.client.oauth.Scope;
import org.osiam.client.query.Query;
import org.osiam.client.query.metamodel.User_;
import org.osiam.client.user.BasicUser;
import org.osiam.resources.scim.Address;
import org.osiam.resources.scim.Email;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.MemberRef;
import org.osiam.resources.scim.Meta;
import org.osiam.resources.scim.Name;
import org.osiam.resources.scim.PhoneNumber;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class OsiamConnectorTest {

    private final static String ENDPOINT = "http://localhost:9090";
    static final String AUTH_ENDPOINT_ADDRESS = "http://localhost:9090/osiam-auth-server";
    private static final String URL_BASE_USERS = "/osiam-resource-server/Users";
    private static final String URL_BASE_ME = "/osiam-resource-server/me";
    private static final String URL_BASE_GROUPS = "/osiam-resource-server/Groups";
    private final static String userIdString = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private static final String GROUP_ID_STRING = "55bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private final static String COUNTRY = "Germany";
    private static final String IRRELEVANT = "irrelevant";
    private final static String SIMPLE_USER_QUERY_STRING = "filter=displayName+eq+BarbaraJ.";
    private final static String SIMPLE_GROUP_QUERY_STRING = "filter=displayName+eq+test_group01";
    private final static int NUMBER_OF_EXPECTED_GROUPS = 7;
    private final static String VALID_CLIENT_ID = "valid-client";
    private final static String VALID_CLIENT_SECRET = "valid_secret";
    private final static String VALID_USERNAME = "valid-username";
    private final static String VALID_PASSWORD = "valid-password";
    private final static String TOKEN_PATH = "/oauth/token";

    private AccessToken accessToken;
    private User singleUserResult;
    private BasicUser singleBasicUserResult;
    private String searchedUserID;
    private String searchedGroupId;
    private AccessTokenMockProvider tokenProvider;
    private SCIMSearchResult<User> userQueryResult;
    private SCIMSearchResult<Group> groupQueryResult;
    private List<User> allUsers;
    private List<Group> allGroups;
    private Query query;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9090); // No-args constructor defaults to port 8080
    private OsiamConnector oConnector;

    @Before
    public void setUp() throws Exception {
        oConnector = new OsiamConnector.Builder()
                .setEndpoint(ENDPOINT)
                .setGrantType(GrantType.RESOURCE_OWNER_PASSWORD_CREDENTIALS)
                .setClientId(IRRELEVANT)
                .setClientSecret(IRRELEVANT)
                .setUserName(IRRELEVANT)
                .setPassword(IRRELEVANT)
                .build();
        tokenProvider = new AccessTokenMockProvider("/__files/valid_accesstoken.json");

        givenAnUserID();
        givenAGroupID();
        givenAnAccessToken();
    }

    @Test
    public void getUser_is_transferred_correctly() throws Exception {
        givenUserIDcanBeFound();
        whenSingleIDisLookedUp();
        thenReturnedUserHasID(searchedUserID);
        thenMetaDataWasDeserializedCorrectly();
        thenAddressIsDeserializedCorrectly();
        thenPhoneNumbersAreDeserializedCorrectly();
        thenBasicValuesAreDeserializedCorrectly();
    }

    @Test
    public void getAllUsers_is_transferred_correctly() {
        givenAllUsersAreLookedUpSuccessfully();
        whenAllUsersAreLookedUp();
        thenNumberOfAllUsersIs(1);
    }

    @Test
    public void searchUsers_by_string_is_transferred_correctly() {
        givenASingleUserCanBeSearchedByQuery();
        whenSearchIsUsedByString(SIMPLE_USER_QUERY_STRING);
        thenUserQueryWasValid();
        thenNumberOfReturnedUsersIs(1);
        thenReturnedListOfSearchedUsersIsAsExpected();
    }

    @Test
    public void searchUsers_by_query_is_transferred_correctly() throws UnsupportedEncodingException {
        givenAQueryContainingDifficultCharacters();
        givenAUserCanBeSearchedByQuery();
        whenSearchIsUsedByQuery();
        thenQueryStringIsSplitCorrectly();
    }

    @Test
    public void getCurrentUserBasic_is_transferred_correctly() throws Exception {
        givenAccessTokenForCurrentUserBasicIsValid();
        whenCurrentBasicUserIsLookedUp();
        thenReturnedBasicUserMatchesExpectations();
    }

    @Test
    public void getCurrentUser_is_transferred_correctly() throws Exception {
        givenAccessTokenForCurrentUserIsValid();
        whenCurrentUserIsLookedUp();
        thenReturnedUserMatchesExpectations();
    }

    @Test
    public void getGroup_is_transferred_correctly() throws IOException {
        givenGroupIDcanBeFound();
        whenSingleGroupIsLookedUp();
        thenReturnedGroupHasID(searchedGroupId);
    }

    @Test
    public void getAllGroups_is_transferred_correctly() throws Exception {
        givenAllGroupsAreLookedUpSuccessfully();
        whenAllGroupsAreLookedUp();
        thenReturnedListOfAllGroupsIsAsExpected();
    }

    @Test
    public void searchGroups_by_string_is_transferred_correctly() {
        givenASingleGroupCanBeLookedUpByQuery();
        whenSingleGroupIsSearchedByQueryString(SIMPLE_GROUP_QUERY_STRING);
        thenGroupQueryWasValid();
        thenReturnedListOfSearchedGroupsIsAsExpected();
    }

    @Test
    public void retrieveAccessToken_is_transferred_correctly() throws Exception {
        given_a_correctly_configured_auth_service();
        given_oauth_server_issues_access_token();
        when_token_is_requested();
        then_access_token_is_valid();
    }

    @Test(expected = InvalidAttributeException.class)
    public void request_access_token_without_setting_endpoint_raises_exception() {
        givenAConfiguredServiceWithoutEndpoint();
        when_token_is_requested();
    }

    @Test(expected = InvalidAttributeException.class)
    public void request_me_user_without_setting_endpint_raises_exception() {
        givenAConfiguredServiceWithoutResourceEndpoint();
        given_oauth_server_issues_access_token();
        when_token_is_requested();
        when_me_user_is_requested();
    }

    private void givenUserIDcanBeFound() {
        stubFor(givenUserIDisLookedUp(userIdString, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("user_" + userIdString + ".json")));
    }

    private void givenGroupIDcanBeFound() {
        stubFor(givenGroupIDisLookedUp(GROUP_ID_STRING, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("group_" + GROUP_ID_STRING + ".json")));
    }

    private MappingBuilder givenUserIDisLookedUp(String id, AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE_USERS + "/" + id))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }

    private MappingBuilder givenGroupIDisLookedUp(String id, AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE_GROUPS + "/" + id))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }

    private void whenSingleIDisLookedUp() {
        singleUserResult = oConnector.getUser(searchedUserID, accessToken);
    }

    private void thenReturnedUserHasID(String id) {
        assertEquals(id.toString(), singleUserResult.getId());
    }

    private void thenMetaDataWasDeserializedCorrectly() throws ParseException {
        Meta deserializedMeta = singleUserResult.getMeta();
        Date expectedCreated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2011-08-01 20:29:49");

        assertEquals(expectedCreated, deserializedMeta.getCreated());
        assertEquals(expectedCreated, deserializedMeta.getLastModified());
        assertEquals("https://example.com/v1/Users/2819c223...", deserializedMeta.getLocation());
        assertEquals(null, deserializedMeta.getVersion());
        assertEquals("User", deserializedMeta.getResourceType());
    }

    public void thenAddressIsDeserializedCorrectly() throws Exception {
        List<Address> addresses = singleUserResult.getAddresses();
        assertEquals(1, addresses.size());
        Address address = addresses.get(0);

        assertEquals("example street 42", address.getStreetAddress());
        assertEquals("11111", address.getPostalCode());
        assertEquals(COUNTRY, address.getCountry());
        assertEquals(COUNTRY, address.getRegion());
        assertEquals(COUNTRY, address.getLocality());
    }

    public void thenPhoneNumbersAreDeserializedCorrectly() {
        List<PhoneNumber> phonenumbers = singleUserResult.getPhoneNumbers();
        assertEquals(1, phonenumbers.size());
        PhoneNumber phonenumber = phonenumbers.get(0);

        assertEquals("555-555-8377", phonenumber.getValue().toString());
        assertEquals("work", phonenumber.getType().getValue());
    }

    public void thenBasicValuesAreDeserializedCorrectly() throws Exception {
        assertEquals("bjensen", singleUserResult.getExternalId());
        assertEquals(null, singleUserResult.isActive());
        assertEquals("BarbaraJ.", singleUserResult.getDisplayName());
        assertEquals("de", singleUserResult.getLocale());
        assertEquals("Barbara", singleUserResult.getNickName());
        assertEquals("Dr.", singleUserResult.getTitle());
        assertEquals("bjensen", singleUserResult.getUserName());
    }

    private void givenAnAccessToken() throws IOException {
        accessToken = tokenProvider.valid_access_token();
    }

    private void givenAnUserID() {
        searchedUserID = userIdString;
    }

    private void givenAllUsersAreLookedUpSuccessfully() {
        stubFor(get(urlEqualTo(URL_BASE_USERS + "?count=" + Integer.MAX_VALUE))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("query_all_users.json")));
    }

    private void whenAllUsersAreLookedUp() {
        allUsers = oConnector.getAllUsers(accessToken);
    }

    private void thenNumberOfReturnedUsersIs(int numberOfUsers) {
        assertEquals(numberOfUsers, userQueryResult.getTotalResults());
        assertEquals(numberOfUsers, userQueryResult.getResources().size());
    }

    private void thenNumberOfAllUsersIs(int numberOfUsers) {
        assertEquals(numberOfUsers, allUsers.size());
    }

    private void givenASingleUserCanBeSearchedByQuery() {
        stubFor(get(urlEqualTo(URL_BASE_USERS + "?filter=displayName+eq+BarbaraJ."))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("query_user_by_name.json")));
    }

    private void whenSearchIsUsedByString(String queryString) {
        userQueryResult = oConnector.searchUsers(queryString, accessToken);
    }

    private void thenUserQueryWasValid() {
        verify(getRequestedFor(urlEqualTo(URL_BASE_USERS + "?filter=displayName+eq+BarbaraJ."))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType())));
    }

    private void thenReturnedListOfSearchedUsersIsAsExpected() {
        assertEquals(1, userQueryResult.getTotalResults());
        assertEquals("BarbaraJ.", userQueryResult.getResources().iterator().next().getDisplayName());
    }

    private void givenAQueryContainingDifficultCharacters() throws UnsupportedEncodingException {
        Query.Filter filter = new Query.Filter(User.class, User_.Name.formatted.contains("Schulz & Schulz Industries"));
        query = new Query.Builder(User.class).setFilter(filter).build();
    }

    private void givenAUserCanBeSearchedByQuery() {
        stubFor(get(urlMatching(URL_BASE_USERS + "\\?filter=.+"))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("query_user_by_name.json")));
    }

    private void whenSearchIsUsedByQuery() {
        userQueryResult = oConnector.searchUsers(query, accessToken);
    }

    private void thenQueryStringIsSplitCorrectly() {
        verify(getRequestedFor(
                urlEqualTo(URL_BASE_USERS + "?filter=name.formatted+co+%22Schulz+%26+Schulz+Industries%22"))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType())));
    }

    private void givenAccessTokenForCurrentUserBasicIsValid() {
        stubFor(givenMeIsLookedUp(accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("user_me.json")));
    }

    private void givenAccessTokenForCurrentUserIsValid() {
        givenAccessTokenForCurrentUserBasicIsValid();
        givenUserIDcanBeFound();
    }

    private MappingBuilder givenMeIsLookedUp(AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE_ME))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }

    private void whenCurrentBasicUserIsLookedUp() {
        singleBasicUserResult = oConnector.getCurrentUserBasic(accessToken);
    }

    private void whenCurrentUserIsLookedUp() {
        singleUserResult = oConnector.getCurrentUser(accessToken);
    }

    private void thenReturnedUserMatchesExpectations() throws Exception {

        User expectedUser = get_expected_user();

        assertEquals(expectedUser.getDisplayName(), singleUserResult.getDisplayName());
        assertEqualsEmailList(expectedUser.getEmails(), singleUserResult.getEmails());
        assertEquals(expectedUser.getExternalId(), singleUserResult.getExternalId());
        assertEquals(expectedUser.getLocale(), singleUserResult.getLocale());
        assertEqualsName(expectedUser.getName(), singleUserResult.getName());
        assertEquals(expectedUser.getNickName(), singleUserResult.getNickName());
        assertEquals(expectedUser.getPassword(), singleUserResult.getPassword());
        assertEquals(expectedUser.getPhotos(), singleUserResult.getPhotos());
        assertEquals(expectedUser.getPreferredLanguage(), singleUserResult.getPreferredLanguage());
        assertEquals(expectedUser.getProfileUrl(), singleUserResult.getProfileUrl());
        assertEquals(expectedUser.getTimezone(), singleUserResult.getTimezone());
        assertEquals(expectedUser.getTitle(), singleUserResult.getTitle());
        assertEquals(expectedUser.getUserName(), singleUserResult.getUserName());
        assertEquals(expectedUser.getUserType(), singleUserResult.getUserType());
        assertEquals(expectedUser.isActive(), singleUserResult.isActive());
    }

    private void thenReturnedBasicUserMatchesExpectations() throws Exception {

        BasicUser expectedUser = get_expected_basic_user();

        assertEquals(expectedUser, singleBasicUserResult);
    }

    private User get_expected_user() throws Exception {
        Reader reader = null;
        StringBuilder jsonUser = null;
        User expectedUser;
        try {
            reader = new FileReader("src/test/resources/__files/user_" + userIdString + ".json");
            jsonUser = new StringBuilder();
            for (int c; (c = reader.read()) != -1;) {
                jsonUser.append((char) c);
            }
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
            }
        }

        expectedUser = new ObjectMapper().readValue(jsonUser.toString(), User.class);
        return expectedUser;
    }

    private BasicUser get_expected_basic_user() throws Exception {
        Reader reader = null;
        StringBuilder jsonUser = null;
        BasicUser expectedUser;
        try {
            reader = new FileReader("src/test/resources/__files/user_me.json");
            jsonUser = new StringBuilder();
            for (int c; (c = reader.read()) != -1;) {
                jsonUser.append((char) c);
            }
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
            }
        }

        expectedUser = new ObjectMapper().readValue(jsonUser.toString(), BasicUser.class);
        return expectedUser;
    }

    private void assertEqualsEmailList(List<Email> expected, List<Email> actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected.size() != actual.size()) {
            fail("The expected List has not the same number of values like the actual list");
        }
        for (int count = 0; count < expected.size(); count++) {
            Email expectedAttribute = expected.get(count);
            Email actualAttribute = actual.get(count);
            assertEquals(expectedAttribute.getValue().toString(), actualAttribute.getValue().toString());
        }
    }

    private void assertEqualsName(Name expected, Name actual) {
        if (expected == null && actual == null) {
            return;
        }
        assertEquals(expected.getFamilyName(), actual.getFamilyName());
        assertEquals(expected.getGivenName(), actual.getGivenName());
        assertEquals(expected.getMiddleName(), actual.getMiddleName());
        assertEquals(expected.getHonorificPrefix(), actual.getHonorificPrefix());
        assertEquals(expected.getHonorificSuffix(), actual.getHonorificSuffix());
    }

    private void whenSingleGroupIsLookedUp() {
        oConnector.getGroup(searchedGroupId, accessToken);
    }

    private void givenAGroupID() {
        searchedGroupId = GROUP_ID_STRING;
    }

    private void thenReturnedGroupHasID(String id) {
        Group result = oConnector.getGroup(id, accessToken);
        assertEquals(id.toString(), result.getId());
    }

    private void givenAllGroupsAreLookedUpSuccessfully() {
        stubFor(get(urlEqualTo(URL_BASE_GROUPS + "?count=" + Integer.MAX_VALUE))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("query_all_groups.json")));
    }

    private void whenAllGroupsAreLookedUp() {
        allGroups = oConnector.getAllGroups(accessToken);
    }

    private void thenReturnedListOfAllGroupsIsAsExpected() {
        assertEquals(NUMBER_OF_EXPECTED_GROUPS, allGroups.size());
        for (Group currentGroup : allGroups) {
            if (currentGroup.getId().equals(GROUP_ID_STRING)) {
                assertEquals(1, currentGroup.getMembers().size());
                for (MemberRef actValue : currentGroup.getMembers()) {
                    assertEquals(userIdString, actValue.getValue().toString());
                }
                break;
            }
        }
    }

    private void givenASingleGroupCanBeLookedUpByQuery() {
        stubFor(get(urlEqualTo(URL_BASE_GROUPS + "?filter=displayName+eq+test_group01"))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("query_group_by_name.json")));
    }

    private void thenGroupQueryWasValid() {
        verify(getRequestedFor(urlEqualTo(URL_BASE_GROUPS + "?filter=displayName+eq+test_group01"))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType())));
    }

    private void whenSingleGroupIsSearchedByQueryString(String queryString) {
        groupQueryResult = oConnector.searchGroups(queryString, accessToken);
    }

    private void thenReturnedListOfSearchedGroupsIsAsExpected() {
        assertEquals(1, groupQueryResult.getTotalResults());
        assertEquals(1, groupQueryResult.getResources().size());
        assertEquals("test_group01", groupQueryResult.getResources().iterator().next().getDisplayName());
    }

    private void given_a_correctly_configured_auth_service() {
        oConnector = new OsiamConnector.Builder()
                .setEndpoint(ENDPOINT)
                .setGrantType(GrantType.RESOURCE_OWNER_PASSWORD_CREDENTIALS)
                .setClientId(VALID_CLIENT_ID)
                .setClientSecret(VALID_CLIENT_SECRET)
                .setUserName(VALID_USERNAME)
                .setPassword(VALID_PASSWORD)
                .setScope(Scope.GET)
                .build();
    }

    private void givenAConfiguredServiceWithoutEndpoint() {
        oConnector = new OsiamConnector.Builder()
                .setGrantType(GrantType.RESOURCE_OWNER_PASSWORD_CREDENTIALS)
                .setClientId(VALID_CLIENT_ID)
                .setClientSecret(VALID_CLIENT_SECRET)
                .setUserName(VALID_USERNAME)
                .setPassword(VALID_PASSWORD)
                .setScope(Scope.GET)
                .build();
    }

    private void givenAConfiguredServiceWithoutResourceEndpoint() {
        oConnector = new OsiamConnector.Builder()
                .setGrantType(GrantType.RESOURCE_OWNER_PASSWORD_CREDENTIALS)
                .setAuthServerEndpoint(AUTH_ENDPOINT_ADDRESS)
                .setClientId(VALID_CLIENT_ID)
                .setClientSecret(VALID_CLIENT_SECRET)
                .setUserName(VALID_USERNAME)
                .setPassword(VALID_PASSWORD)
                .setScope(Scope.GET)
                .build();
    }

    private void given_oauth_server_issues_access_token() {
        stubFor(post(urlEqualTo("/osiam-auth-server" + TOKEN_PATH)).
                willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBodyFile("valid_accesstoken.json")));
    }

    private void when_token_is_requested() {
        accessToken = oConnector.retrieveAccessToken();
    }

    private void when_me_user_is_requested() {
        oConnector.getCurrentUser(accessToken);
    }

    private void then_access_token_is_valid() throws Exception {
        assertFalse(accessToken.isExpired());
    }
}
