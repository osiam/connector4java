package org.osiam.client;
/*
 * for licensing see the file license.txt.
 */

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.QueryResult;
import org.osiam.resources.scim.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OsiamUserServiceTest {

    private static final String URL_BASE = "/osiam-server//Users/";
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9090); // No-args constructor defaults to port 8080

    final static private String COUNTRY = "Germany";
    final static private String userUuidString = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    final static private String endpoint = "http://localhost:9090/osiam-server/";
    final static private String SIMPLE_QUERY_STRING = "displayName eq BarbaraJ.";
    private UUID searchedUUID;
    private AccessToken accessToken;
    private AccessTokenMockProvider tokenProvider;

    private User singleUserResult;
    private QueryResult<User> queryResult;

    OsiamUserService service;

    @Before
    public void setUp() throws Exception {
        service = new OsiamUserService.Builder(endpoint).build();
        tokenProvider = new AccessTokenMockProvider("/__files/valid_accesstoken.json");

        // use happy path for default
        givenAnUserUUID();
        givenAnAccessToken();
    }

    @Test
    public void service_returns_correct_uri() throws Exception {
        assertEquals(new URI(endpoint + "/Users/"), service.getUri());
    }

    @Test
    public void existing_user_is_returned() throws Exception {
        givenUUIDcanBeFound();
        whenUUIDisLookedUp();
        thenReturnedUserHasUUID(searchedUUID);
        thenMetaDataWasDeserializedCorrectly();
        thenAddressIsDeserializedCorrectly();
        thenPhoneNumbersAreDeserializedCorrectly();
        thenBasicValuesAreDeserializedCorrectly();
    }

    @Test
    public void user_has_valid_values() throws Exception {
        givenUUIDcanBeFound();
        whenUUIDisLookedUp();
        thenReturnedUserMatchesExpectations();
    }

    @Test(expected = NoResultException.class)
    public void user_does_not_exist() throws IOException {
        givenUUIDcanNotBeFound();
        whenUUIDisLookedUp();
        fail("Exception expected");
    }

    @Test(expected = UnauthorizedException.class)
    public void expired_access_token() throws Exception {
        givenExpiredAccessTokenIsUsedForLookup();
        whenUUIDisLookedUp();
        fail("Exception expected");
    }

    @Test(expected = UnauthorizedException.class)
    public void invalid_access_token() throws Exception {
        givenInvalidAccessTokenIsUsedForLookup();
        whenUUIDisLookedUp();
        fail("Exception expected");
    }

    @Test
    public void all_users_are_looked_up() {
        givenAllUsersAreLookedUpSuccessfully();
        whenAllUsersAreLookedUp();
        thenNumberOfReturnedUsersIs(1);
    }

    @Test
    public void search_for_single_user_is_successful() {
        givenASingleUserCanBeSearchedByQuery();
        whenSingleUserIsSearchedByQueryString(SIMPLE_QUERY_STRING);
        thenQueryWasValid();
        thenNumberOfReturnedUsersIs(1);
        thenReturnedListOfSearchedUsersIsAsExpected();
    }

    private void whenSingleUserIsSearchedByQueryString(String queryString) {
        queryResult = service.searchUsersByQueryString(queryString, accessToken);
    }

    private void givenAnAccessToken() throws IOException {
        this.accessToken = tokenProvider.valid_access_token();
    }

    private void givenAnUserUUID() {
        this.searchedUUID = UUID.fromString(userUuidString);
    }

    private void whenUUIDisLookedUp() {
        singleUserResult = service.getUserByUUID(searchedUUID, accessToken);
    }

    private void whenAllUsersAreLookedUp() {
        queryResult = service.getAllUsers(accessToken);
    }

    private void givenExpiredAccessTokenIsUsedForLookup() {
        stubFor(givenUUIDisLookedUp(userUuidString, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_UNAUTHORIZED)));
    }

    private void givenInvalidAccessTokenIsUsedForLookup() {
        stubFor(givenUUIDisLookedUp(userUuidString, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_UNAUTHORIZED)));
    }

    private void givenUUIDcanNotBeFound() {
        stubFor(givenUUIDisLookedUp(userUuidString, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_NOT_FOUND)));
    }

    private void givenUUIDcanBeFound() {
        stubFor(givenUUIDisLookedUp(userUuidString, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBodyFile("user_" + userUuidString + ".json")));
    }

    private MappingBuilder givenUUIDisLookedUp(String uuidString, AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE + uuidString))
                .withHeader("Content-Type", equalTo(APPLICATION_JSON))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }

    private void givenAllUsersAreLookedUpSuccessfully() {
        stubFor(get(urlEqualTo(URL_BASE))
                .withHeader("Content-Type", equalTo(APPLICATION_JSON))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBodyFile("query_all_users.json")));
    }

    private void givenASingleUserCanBeSearchedByQuery() {
        stubFor(get(urlEqualTo(URL_BASE + "?access_token=" + accessToken.getToken()
                + "&filter=displayName+eq+BarbaraJ."))
                .withHeader("Content-Type", equalTo(APPLICATION_JSON))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBodyFile("query_user_by_name.json")));
    }

    private void thenReturnedUserHasUUID(UUID uuid) {
        assertEquals(uuid.toString(), singleUserResult.getId());
    }

    private void thenQueryWasValid() {
        verify(getRequestedFor(urlEqualTo(URL_BASE + "?access_token=" + accessToken.getToken()
                + "&filter=displayName+eq+BarbaraJ."))
                .withHeader("Content-Type", equalTo(APPLICATION_JSON)));
    }


    private void thenReturnedListOfSearchedUsersIsAsExpected() {
        assertEquals(1, queryResult.getTotalResults());
        assertEquals("BarbaraJ.", queryResult.getResources().iterator().next().getDisplayName());
    }

    private void thenNumberOfReturnedUsersIs(int numberOfUsers) {
        assertEquals(numberOfUsers, queryResult.getTotalResults());
        assertEquals(numberOfUsers, queryResult.getResources().size());
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
        assertEquals("de", singleUserResult.getPreferredLanguage());
        assertEquals("http://babaraJ.com", singleUserResult.getProfileUrl());
        assertEquals("UTC", singleUserResult.getTimezone());
        assertEquals("Dr.", singleUserResult.getTitle());
        assertEquals("bjensen", singleUserResult.getUserName());
        assertEquals("user", singleUserResult.getUserType());
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
}
