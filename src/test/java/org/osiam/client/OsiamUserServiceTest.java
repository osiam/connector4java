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
import org.osiam.resources.scim.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OsiamUserServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9090); // No-args constructor defaults to port 8080

    final private static String userUuidString = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    final private static String endpoint = "http://localhost:9090/osiam-server/";
    private UUID searchedUUID;
    private AccessToken accessToken;
    private AccessTokenMockProvider tokenProvider;

    OsiamUserService service;

    @Before
    public void setUp() {
        service = new OsiamUserService.Builder(endpoint).build();
        tokenProvider = new AccessTokenMockProvider("/__files/valid_accesstoken.json");

    }

    @Test
    public void service_returns_correct_uri() throws Exception {
        assertEquals(new URI(endpoint + "/Users/"), service.getUri());
    }

    @Test
    public void existing_user_is_returned() throws IOException {
        given_an_existing_user_UUID();
        given_a_valid_access_token();
        when_existing_uuid_is_looked_up();
        then_returned_user_has_uuid(searchedUUID);
    }

    @Test
    public void user_has_valid_values() throws Exception {
        given_an_existing_user_UUID();
        given_a_valid_access_token();
        when_existing_uuid_is_looked_up();
        then_returned_user_matches_expectations();
    }

    @Test(expected = NoResultException.class)
    public void user_does_not_exist() throws IOException {
        given_a_valid_access_token();
        given_a_non_existent_user_UUID();
        when_non_existent_uuid_is_looked_up();
        service.getUserByUUID(searchedUUID, accessToken);
        fail("Exception expected");
    }

    @Test(expected = UnauthorizedException.class)
    public void expired_access_token() throws Exception {
        given_an_expired_access_token();
        given_an_existing_user_UUID();
        when_expired_access_token_is_used_for_lookup();
        service.getUserByUUID(searchedUUID, accessToken);
        fail("Exception expected");
    }

    @Test(expected = UnauthorizedException.class)
    public void invalid_access_token() throws Exception {
        given_an_invalid_access_token();
        given_an_existing_user_UUID();
        when_invalid_access_token_is_used_for_lookup();
        service.getUserByUUID(searchedUUID, accessToken);
        fail("Exception expected");
    }

    private void given_a_valid_access_token() throws IOException {
        this.accessToken = tokenProvider.valid_access_token();
    }

    private void given_an_expired_access_token() throws Exception {
        this.accessToken = tokenProvider.expired_access_token();
    }

    private void given_an_invalid_access_token() throws Exception {
        this.accessToken = tokenProvider.invalid_access_token();
    }

    private void given_an_existing_user_UUID() {
        this.searchedUUID = UUID.fromString(userUuidString);
    }

    private void given_a_non_existent_user_UUID() {
        this.searchedUUID = UUID.fromString(userUuidString);
    }

    private void when_expired_access_token_is_used_for_lookup() {
        stubFor(when_uuid_is_looked_up(userUuidString, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_UNAUTHORIZED)));
    }

    private void when_invalid_access_token_is_used_for_lookup() {
        stubFor(when_uuid_is_looked_up(userUuidString, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_UNAUTHORIZED)));
    }

    private void when_non_existent_uuid_is_looked_up() {
        stubFor(when_uuid_is_looked_up(userUuidString, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_NOT_FOUND)));
    }

    private void when_existing_uuid_is_looked_up() {
        stubFor(when_uuid_is_looked_up(userUuidString, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("user_" + userUuidString + ".json")));
    }

    private MappingBuilder when_uuid_is_looked_up(String uuidString, AccessToken accessToken) {
        return get(urlEqualTo("/osiam-server//Users/" + uuidString))
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }

    private void then_returned_user_has_uuid(UUID uuid) {
        User result = service.getUserByUUID(uuid, accessToken);
        assertEquals(uuid.toString(), result.getId());
    }

    private void then_returned_user_matches_expectations() throws Exception {

        User expectedUser = get_expected_user();
        User actualUser = service.getUserByUUID(searchedUUID, accessToken);
        assertEqualsMetaData(expectedUser.getMeta(), actualUser.getMeta());
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEqualsAdressList(expectedUser.getAddresses(), actualUser.getAddresses());
        assertEquals(expectedUser.getDisplayName(), actualUser.getDisplayName());
        assertEqualsMultiValueList(expectedUser.getEmails(), actualUser.getEmails());
        assertEquals(expectedUser.getExternalId(), actualUser.getExternalId());
        assertEquals(expectedUser.getLocale(), actualUser.getLocale());
        assertEqualsName(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getNickName(), actualUser.getNickName());
        assertEquals(expectedUser.getPassword(), actualUser.getPassword());
        assertEqualsMultiValueList(expectedUser.getPhoneNumbers(), actualUser.getPhoneNumbers());
        assertEquals(expectedUser.getPhotos(), actualUser.getPhotos());
        assertEquals(expectedUser.getPreferredLanguage(), actualUser.getPreferredLanguage());
        assertEquals(expectedUser.getProfileUrl(), actualUser.getProfileUrl());
        assertEquals(expectedUser.getTimezone(), actualUser.getTimezone());
        assertEquals(expectedUser.getTitle(), actualUser.getTitle());
        assertEquals(expectedUser.getUserName(), actualUser.getUserName());
        assertEquals(expectedUser.getUserType(), actualUser.getUserType());
        assertEquals(expectedUser.isActive(), actualUser.isActive());
    }

    private void assertEqualsMetaData(Meta expected, Meta actual) {
        assertEquals(expected.getResourceType(), actual.getResourceType());
        assertEquals(expected.getCreated(), actual.getCreated());
        assertEquals(expected.getLastModified(), actual.getLastModified());
        assertEquals(expected.getLocation(), actual.getLocation());
        assertEquals(expected.getVersion(), actual.getVersion());
        assertEquals(expected.getAttributes(), actual.getAttributes());
    }

    private void assertEqualsAdressList(List<Address> expected, List<Address> actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected.size() != actual.size()) {
            fail("The expected List has not the same number of values like the actual list");
        }
        for (int count = 0; count < expected.size(); count++) {
            Address expectedAttribute = expected.get(count);
            Address actualAttribute = actual.get(count);
            assertEquals(expectedAttribute.getCountry(), actualAttribute.getCountry());
            assertEquals(expectedAttribute.getLocality(), actualAttribute.getLocality());
            assertEquals(expectedAttribute.getPostalCode(), actualAttribute.getPostalCode());
            assertEquals(expectedAttribute.getRegion(), actualAttribute.getRegion());
            assertEquals(expectedAttribute.getStreetAddress(), actualAttribute.getStreetAddress());
        }
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
