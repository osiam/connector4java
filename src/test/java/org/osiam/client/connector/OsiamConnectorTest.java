package org.osiam.client.connector;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osiam.client.AccessTokenMockProvider;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.GrantType;
import org.osiam.client.oauth.Scope;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryResult;
import org.osiam.client.query.metamodel.User_;
import org.osiam.resources.scim.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: dmoeb
 * Date: 23.08.13
 * Time: 10:09
 * To change this template use File | Settings | File Templates.
 */
public class OsiamConnectorTest {

    final static private String endpoint = "http://localhost:9090/osiam-server/";
    private static final String URL_BASE_USERS = "/osiam-server//Users";
    private static final String URL_BASE_GROUPS = "/osiam-server//Groups";
    final static private String userUuidString = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private static final String GROUP_UUID_STRING = "55bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private AccessToken accessToken;
    private User singleUserResult;
    private Group singleGroupResult;
    private UUID searchedUserUUID;
    private UUID SEARCHED_GROUP_UUID;
    final static private String COUNTRY = "Germany";
    private static final String IRRELEVANT = "irrelevant";
    private AccessTokenMockProvider tokenProvider;
    private QueryResult<User> userQueryResult;
    private QueryResult<Group> groupQueryResult;
    final static private String SIMPLE_USER_QUERY_STRING = "filter=displayName+eq+BarbaraJ.";
    final static private String SIMPLE_GROUP_QUERY_STRING = "filter=displayName+eq+test_group01";
    private Query query;
    final static private int NUMBER_OF_EXPECTED_GROUPS = 7;
    private final static String VALID_CLIENT_ID = "valid-client";
    private final static String VALID_CLIENT_SECRET = "valid_secret";
    private final static String VALID_USERNAME = "valid-username";
    private final static String VALID_PASSWORD = "valid-password";
    private final static String TOKEN_PATH = "/oauth/token";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9090); // No-args constructor defaults to port 8080

    OsiamConnector oConnector;

    @Before
    public void setUp() throws Exception {
        oConnector = new OsiamConnector.Builder(endpoint).setGrantType(GrantType.PASSWORD)
                .setClientId(IRRELEVANT)
                .setClientSecret(IRRELEVANT)
                .setUserName(IRRELEVANT)
                .setPassword(IRRELEVANT)
                .build();
        tokenProvider = new AccessTokenMockProvider("/__files/valid_accesstoken.json");

        // use happy path for default
        givenAnUserUUID();
        givenAGroupUUID();
        givenAnAccessToken();
    }

    @Test
    public void getUser_is_transferred_correctly() throws Exception {
        givenUserUUIDcanBeFound();
        whenSingleUUIDisLookedUp();
        thenReturnedUserHasUUID(searchedUserUUID);
        thenMetaDataWasDeserializedCorrectly();
        thenAddressIsDeserializedCorrectly();
        thenPhoneNumbersAreDeserializedCorrectly();
        thenBasicValuesAreDeserializedCorrectly();
    }

    @Test
    public void getAllUsers_is_transferred_correctly() {
        givenAllUsersAreLookedUpSuccessfully();
        whenAllUsersAreLookedUp();
        thenNumberOfReturnedUsersIs(1);
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
    public void getMe_is_transferred_correctly() throws Exception {
        givenAccessTokenForMeIsValid();
        whenMeIsLookedUp();
        thenReturnedUserMatchesExpectations();
    }

    @Test
    public void getGroup_is_transferred_correctly() throws IOException {
        givenGroupUUIDcanBeFound();
        whenSingleGroupIsLookedUp();
        thenReturnedGroupHasUUID(SEARCHED_GROUP_UUID);
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

    private void givenUserUUIDcanBeFound() {
        stubFor(givenUserUUIDisLookedUp(userUuidString, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("user_" + userUuidString + ".json")));
    }

    private void givenGroupUUIDcanBeFound() {
        stubFor(givenGroupUUIDisLookedUp(GROUP_UUID_STRING, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("group_" + GROUP_UUID_STRING + ".json")));
    }

    private MappingBuilder givenUserUUIDisLookedUp(String uuidString, AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE_USERS + "/" + uuidString))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }

    private MappingBuilder givenGroupUUIDisLookedUp(String uuidString, AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE_GROUPS + "/" + uuidString))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }

    private void whenSingleUUIDisLookedUp() {
        singleUserResult = oConnector.getUser(searchedUserUUID, accessToken);
    }

    private void thenReturnedUserHasUUID(UUID uuid) {
        assertEquals(uuid.toString(), singleUserResult.getId());
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
        List<MultiValuedAttribute> phonenumbers = singleUserResult.getPhoneNumbers();
        assertEquals(1, phonenumbers.size());
        MultiValuedAttribute phonenumber = phonenumbers.get(0);

        assertEquals("555-555-8377", phonenumber.getValue().toString());
        assertEquals("work", phonenumber.getType());
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
        this.accessToken = tokenProvider.valid_access_token();
    }

    private void givenAnUserUUID() {
        this.searchedUserUUID = UUID.fromString(userUuidString);
    }

    private void givenAllUsersAreLookedUpSuccessfully() {
        stubFor(get(urlEqualTo(URL_BASE_USERS))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("query_all_users.json")));
    }

    private void whenAllUsersAreLookedUp() {
        userQueryResult = oConnector.getAllUsers(accessToken);
    }

    private void thenNumberOfReturnedUsersIs(int numberOfUsers) {
        assertEquals(numberOfUsers, userQueryResult.getTotalResults());
        assertEquals(numberOfUsers, userQueryResult.getResources().size());
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
        verify(getRequestedFor(urlEqualTo(URL_BASE_USERS + "?filter=name.formatted+co+%22Schulz+%26+Schulz+Industries%22"))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType())));
    }

    private void givenAccessTokenForMeIsValid() {
        stubFor(givenMeIsLookedUp(accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("user_" + userUuidString + ".json")));
    }

    private MappingBuilder givenMeIsLookedUp(AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE_USERS + "/me"))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }

    private void whenMeIsLookedUp() {
        singleUserResult = oConnector.getMe(accessToken);
    }

    private void thenReturnedUserMatchesExpectations() throws Exception {

        User expectedUser = get_expected_user();

        assertEquals(expectedUser.getDisplayName(), singleUserResult.getDisplayName());
        assertEqualsMultiValueList(expectedUser.getEmails(), singleUserResult.getEmails());
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

    private User get_expected_user() throws Exception {
        Reader reader = null;
        StringBuilder jsonUser = null;
        User expectedUser;
        try {
            reader = new FileReader("src/test/resources/__files/user_" + userUuidString + ".json");
            jsonUser = new StringBuilder();
            for (int c; (c = reader.read()) != -1; )
                jsonUser.append((char) c);
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
            }
        }

        expectedUser = new ObjectMapper().readValue(jsonUser.toString(), User.class);
        return expectedUser;
    }

    private void assertEqualsMultiValueList(List<MultiValuedAttribute> expected, List<MultiValuedAttribute> actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected.size() != actual.size()) {
            fail("The expected List has not the same number of values like the actual list");
        }
        for (int count = 0; count < expected.size(); count++) {
            MultiValuedAttribute expectedAttribute = expected.get(count);
            MultiValuedAttribute actualAttribute = actual.get(count);
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
        singleGroupResult = oConnector.getGroup(SEARCHED_GROUP_UUID, accessToken);
    }

    private void givenAGroupUUID() {
        this.SEARCHED_GROUP_UUID = UUID.fromString(GROUP_UUID_STRING);
    }

    private void thenReturnedGroupHasUUID(UUID uuid) {
        Group result = oConnector.getGroup(uuid, accessToken);
        assertEquals(uuid.toString(), result.getId());
    }

    private void givenAllGroupsAreLookedUpSuccessfully() {
        stubFor(get(urlEqualTo(URL_BASE_GROUPS))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("query_all_groups.json")));
    }

    private void whenAllGroupsAreLookedUp() {
        groupQueryResult = oConnector.getAllGroups(accessToken);
    }

    private void thenReturnedListOfAllGroupsIsAsExpected() {
        assertEquals(NUMBER_OF_EXPECTED_GROUPS, groupQueryResult.getTotalResults());
        for (Group currentGroup : groupQueryResult.getResources()) {
            if (currentGroup.getId().equals(GROUP_UUID_STRING)) {
                assertEquals(1, currentGroup.getMembers().size());
                for (MultiValuedAttribute actValue : currentGroup.getMembers()) {
                    assertEquals(userUuidString, actValue.getValue().toString());
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
        oConnector = new OsiamConnector.Builder(endpoint)
                .setGrantType(GrantType.PASSWORD)
                .setClientId(VALID_CLIENT_ID)
                .setClientSecret(VALID_CLIENT_SECRET)
                .setUserName(VALID_USERNAME)
                .setPassword(VALID_PASSWORD)
                .setScope(Scope.GET)
                .build();
    }

    private void given_oauth_server_issues_access_token() {
        stubFor(post(urlEqualTo("/osiam-server/" + TOKEN_PATH)).
                willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBodyFile("valid_accesstoken.json")));
    }

    private void when_token_is_requested() {
        accessToken = oConnector.retrieveAccessToken();
    }

    private void then_access_token_is_valid() throws Exception {
        assertFalse(accessToken.isExpired());
    }
}
