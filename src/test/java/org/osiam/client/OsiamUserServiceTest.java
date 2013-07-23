package org.osiam.client;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Ignore("The Mocking went bust")
public class OsiamUserServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(); // No-args constructor defaults to port 8080

    final private static String userUuidString = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";

    private UUID searchedUUID;
    private AccessToken accessToken;
    private AccessTokenMockProvider tokenProvider;

    OsiamUserService service;

    @Before
    public void setUp() {
        service = ServiceBuilder.buildUserService(URI.create("http://localhost:8080/osiam-server/"), "irrelvevant", URI.create("http://localhost:5000"), "irrelevant");
        tokenProvider = new AccessTokenMockProvider("/valid_accesstoken.json");

    }

    @Test
    public void existing_user_is_returned() throws IOException {
        given_existing_user_UUID();
        given_valid_access_token();
        when_existing_uuid_is_looked_up();
        then_returned_user_has_uuid(searchedUUID);
    }

    @Test
    public void user_has_valid_values() throws Exception {
        given_existing_user_UUID();
        given_valid_access_token();
        when_existing_uuid_is_looked_up();
        then_returned_user_values(searchedUUID);
    }

    @Test(expected = NoResultException.class)
    public void user_does_not_exist() throws IOException {
        given_valid_access_token();
        given_non_existent_user_UUID();
        when_non_existent_uuid_is_looked_up();
        service.getUserByUUID(searchedUUID, accessToken);
    }

    @Test(expected = UnauthorizedException.class)
    public void invalid_access_token() throws Exception {
        given_invalid_access_token();
        given_non_existent_user_UUID();
        when_invalid_accesstoken_is_looked_up();
        service.getUserByUUID(searchedUUID, accessToken);
    }

    private void given_valid_access_token() throws IOException {
        this.accessToken = tokenProvider.given_a_valid_access_token();
    }

    private void given_invalid_access_token() throws Exception {
        this.accessToken = tokenProvider.given_an_expired_access_token();
    }

    private void given_existing_user_UUID() {
        this.searchedUUID = UUID.fromString(userUuidString);
    }

    private void given_non_existent_user_UUID() {
        this.searchedUUID = UUID.fromString(userUuidString);
    }

    private void when_non_existent_uuid_is_looked_up() {
        stubFor(when_uuid_is_looked_up(userUuidString, accessToken)
                .willReturn(aResponse()
                        .withStatus(404)));
    }

    private void when_invalid_accesstoken_is_looked_up() {
        stubFor(when_uuid_is_looked_up(userUuidString, accessToken)
                .willReturn(aResponse()
                        .withStatus(401)));
    }

    private void when_existing_uuid_is_looked_up() {
        stubFor(when_uuid_is_looked_up(userUuidString, accessToken)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile(userUuidString + ".json")));
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

    private void then_returned_user_values(UUID uuid) throws IOException {

        User expectedUser = get_expected_user();
        User actualUser = service.getUserByUUID(uuid, accessToken);
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

    private User get_expected_user() throws JsonParseException, JsonMappingException, IOException {
        Reader reader = null;
        StringBuilder jsonUser = null;
        User expectedUser = null;
        try {
            reader = new FileReader("src/test/resources/__files/" + userUuidString + ".json");
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
