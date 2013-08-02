package org.osiam.client;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.QueryResult;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.Meta;
import org.osiam.resources.scim.MultiValuedAttribute;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class OsiamGroupServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9090); // No-args constructor defaults to port 8080

    final private static String groupUuidString = "55bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    final private static String endpoint = "http://localhost:9090/osiam-server/";
    private UUID searchedUUID;
    private AccessToken accessToken;
    private AccessTokenMockProvider tokenProvider;

    OsiamGroupService service;

    @Before
    public void setUp() {
        service = new OsiamGroupService.Builder(endpoint).build();
        tokenProvider = new AccessTokenMockProvider("/__files/valid_accesstoken.json");

    }

    @Test
    public void service_returns_correct_uri() throws Exception {
        assertEquals(new URI(endpoint + "/Groups/"), service.getUri());
    }

    @Test
    public void existing_group_is_returned() throws IOException {
        given_an_existing_group_UUID();
        givenAValidAccessToken();
        when_existing_uuid_is_looked_up();
        then_returned_group_has_uuid(searchedUUID);
    }

    @Test
    public void group_has_valid_values() throws Exception {
        given_an_existing_group_UUID();
        givenAValidAccessToken();
        when_existing_uuid_is_looked_up();
        then_returned_group_matches_expectations();
    }

    @Test    
    @Ignore
    public void list_of_groups_is_returned() throws Exception {
        givenAValidAccessToken();
        whenAllGroupsAreLookedUp();
        QueryResult queryResult = service.getAllGroups(accessToken);
        assertEquals(new Integer(7), queryResult.getTotalResults());
    }

    @Test(expected = NoResultException.class)
    public void group_does_not_exist() throws IOException {
        givenAValidAccessToken();
        given_a_non_existent_group_UUID();
        when_non_existent_uuid_is_looked_up();
        service.getGroupByUUID(searchedUUID, accessToken);
        fail("Exception expected");
    }

    @Test(expected = UnauthorizedException.class)
    public void expired_access_token() throws Exception {
        given_an_expired_access_token();
        given_an_existing_group_UUID();
        when_expired_access_token_is_used_for_lookup();
        service.getGroupByUUID(searchedUUID, accessToken);
        fail("Exception expected");
    }

    @Test(expected = UnauthorizedException.class)
    public void invalid_access_token() throws Exception {
        given_an_invalid_access_token();
        given_an_existing_group_UUID();
        when_invalid_access_token_is_used_for_lookup();
        service.getGroupByUUID(searchedUUID, accessToken);
        fail("Exception expected");
    }

    private void givenAValidAccessToken() throws IOException {
        this.accessToken = tokenProvider.valid_access_token();
    }

    private void given_an_expired_access_token() throws Exception {
        this.accessToken = tokenProvider.expired_access_token();
    }

    private void given_an_invalid_access_token() throws Exception {
        this.accessToken = tokenProvider.invalid_access_token();
    }

    private void given_an_existing_group_UUID() {
        this.searchedUUID = UUID.fromString(groupUuidString);
    }

    private void given_a_non_existent_group_UUID() {
        this.searchedUUID = UUID.fromString(groupUuidString);
    }

    private void when_expired_access_token_is_used_for_lookup() {
        stubFor(when_uuid_is_looked_up(groupUuidString, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_UNAUTHORIZED)));
    }

    private void when_invalid_access_token_is_used_for_lookup() {
        stubFor(when_uuid_is_looked_up(groupUuidString, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_UNAUTHORIZED)));
    }

    private void when_non_existent_uuid_is_looked_up() {
        stubFor(when_uuid_is_looked_up(groupUuidString, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_NOT_FOUND)));
    }

    private void when_existing_uuid_is_looked_up() {
        stubFor(when_uuid_is_looked_up(groupUuidString, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("group_" + groupUuidString + ".json")));
    }

    private void whenAllGroupsAreLookedUp(){
        stubFor(whenGroupsAreLookedUp(accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("query_all_groups.json")));
    }

    private MappingBuilder whenGroupsAreLookedUp(AccessToken accessToken) {
        return get(urlEqualTo("/osiam-server//Groups/"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }

    private MappingBuilder when_uuid_is_looked_up(String uuidString, AccessToken accessToken) {
        return get(urlEqualTo("/osiam-server//Groups/" + uuidString))
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }

    private void then_returned_group_has_uuid(UUID uuid) {
        Group result = service.getGroupByUUID(uuid, accessToken);
        assertEquals(uuid.toString(), result.getId());
    }

    private void then_returned_group_matches_expectations() throws Exception {

        Group expectedGroup = get_expected_group();
        Group actualGroup = service.getGroupByUUID(searchedUUID, accessToken);
        assertEqualsMetaData(expectedGroup.getMeta(), actualGroup.getMeta());
        assertEquals(expectedGroup.getId(), actualGroup.getId());
        assertEquals(expectedGroup.getDisplayName(), actualGroup.getDisplayName());
        assertEquals(expectedGroup.getExternalId(), actualGroup.getExternalId());
        assertEqualsMembers(expectedGroup, actualGroup);
    }

    private void assertEqualsMetaData(Meta expected, Meta actual) {
        assertEquals(expected.getResourceType(), actual.getResourceType());
        assertEquals(expected.getCreated(), actual.getCreated());
        assertEquals(expected.getLastModified(), actual.getLastModified());
        assertEquals(expected.getLocation(), actual.getLocation());
        assertEquals(expected.getVersion(), actual.getVersion());
        assertEquals(expected.getAttributes(), actual.getAttributes());
    }

    private void assertEqualsMembers(Group expectedGroup, Group actualGroup) {
        for (MultiValuedAttribute actAttribute : actualGroup.getMembers()) {
            String uuid = actAttribute.getValue().toString();
            boolean found = false;
            for (MultiValuedAttribute expAttribute : expectedGroup.getMembers()) {
                if (expAttribute.getValue().toString().equals(uuid)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }

    private Group get_expected_group() throws Exception {
        Reader reader = null;
        StringBuilder jsonGroup = null;
        Group expectedGroup;
        try {
            reader = new FileReader("src/test/resources/__files/group_" + groupUuidString + ".json");
            jsonGroup = new StringBuilder();
            for (int c; (c = reader.read()) != -1; )
                jsonGroup.append((char) c);
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
            }
        }

        expectedGroup = new ObjectMapper().readValue(jsonGroup.toString(), Group.class);
        return expectedGroup;
    }
}
