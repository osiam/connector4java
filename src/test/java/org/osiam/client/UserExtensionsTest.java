package org.osiam.client;

import java.io.IOException;

import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osiam.client.oauth.AccessToken;
import org.osiam.resources.scim.User;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.apache.http.HttpStatus.SC_OK;

public class UserExtensionsTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9090);

    private static final String URL_BASE = "/osiam-auth-server//Users";
    final static private String ENDPOINT = "http://localhost:9090/osiam-auth-server/";
    private final static String USER_ID_WITHOUT_EXTENSION = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private final static String USER_ID_WITH_EXTENSION = "a4bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private AccessToken accessToken;
    private AccessTokenMockProvider tokenProvider;
    private OsiamUserService service;
    private User singleUserResult;
    private String userIdToRetrieve;

    @Before
    public void setUp() throws Exception {
        service = new OsiamUserService.Builder(ENDPOINT).build();
        tokenProvider = new AccessTokenMockProvider("/__files/valid_accesstoken.json");

        givenAnAccessToken();
    }

    @Test
    public void the_extension_map_is_empty_if_the_user_has_no_extensions() {
        givenAnUserIdWithoutExtension();
        givenUserIdIsLookedUp();
        
        whenSingleUserisRetrieved();
        
        Assert.assertEquals(0, singleUserResult.getAllExtensions().size());
    }

    private void givenAnUserIdWithoutExtension() {
        userIdToRetrieve = USER_ID_WITHOUT_EXTENSION;
    }
    
    private void givenAnUserIdWithExtension() {
        userIdToRetrieve = USER_ID_WITH_EXTENSION;
    }

    @Test
    public void the_schema_set_only_contains_the_core_schema_if_the_user_has_no_extensions() {
    }

    @Test
    public void if_an_extension_is_defined_its_urn_is_listed_in_the_schema_set() {
    }

    @Test
    public void if_an_extension_is_defined_it_shows_up_in_the_extensions_map() {
    }

    @Test
    public void updates_to_fields_are_correctly_serialized() {
    }

    private void whenSingleUserisRetrieved() {
        singleUserResult = service.getUser(userIdToRetrieve, accessToken);
    }

    private void givenAnAccessToken() throws IOException {
        this.accessToken = tokenProvider.valid_access_token();
    }

    private void givenUserIdIsLookedUp() {
        stubFor(givenIDisLookedUp(userIdToRetrieve, accessToken).willReturn(
                aResponse().withStatus(SC_OK).withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("user_" + userIdToRetrieve + ".json")));
    }

    private MappingBuilder givenIDisLookedUp(String id, AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE + "/" + id)).withHeader("Content-Type",
                equalTo(ContentType.APPLICATION_JSON.getMimeType())).withHeader("Authorization",
                equalTo("Bearer " + accessToken.getToken()));
    }
}
