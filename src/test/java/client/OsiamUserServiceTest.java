package client;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osiam.resources.scim.User;

import java.net.URI;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

public class OsiamUserServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(); // No-args constructor defaults to port 8080

    final private static String uuidString = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";

    private UUID existingUserUUID;
    private String access_token;

    OsiamUserService service;

    @Before
    public void setUp() {
        service = ServiceBuilder.buildUserService(URI.create("http://localhost:8080/osiam-server/"), "irrelvevant", URI.create("http://localhost:5000"), "irrelevant");
    }

    @Test
    public void existing_user_is_returned() {
        given_existing_user_UUID();
        given_valid_access_token();
        when_existing_uuid_is_looked_up();
        then_returned_user_has_uuid(existingUserUUID);
    }

    private void given_valid_access_token() {
        access_token = "valid access token";
    }

    private void given_existing_user_UUID() {
        this.existingUserUUID = UUID.fromString(uuidString);
    }

    private void when_existing_uuid_is_looked_up() {
        stubFor(when_uuid_is_looked_up(uuidString, access_token)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile(uuidString + ".json")));
    }

    private MappingBuilder when_uuid_is_looked_up(String uuidString, String access_token) {
        return get(urlEqualTo("/osiam-server//User/" + uuidString))
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Authorization", equalTo("Bearer " + access_token));
    }

    private void then_returned_user_has_uuid(UUID uuid) {
        User result = service.getUserByUUID(uuid, access_token);
        assertEquals(uuid.toString(), result.getId());
    }
}
