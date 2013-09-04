package org.osiam.client;
/*
 * for licensing see the file license.txt.
 */


import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import org.apache.http.entity.ContentType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.resources.scim.User;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class OsiamUserEditTest {

    private static final String URL_BASE = "/osiam-server//Users";
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9090); // No-args constructor defaults to port 8080

    final static private String userUuidString = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    final static private String INVALID_USER_UUID_STRING = "55bbe688-4b1e-4e4e-80e7-e5ba5c4d";
    final static private String endpoint = "http://localhost:9090/osiam-server/";
    private UUID updateUUID;
    private AccessToken accessToken;
    private AccessTokenMockProvider tokenProvider;

    private User singleUserResult;
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
        assertEquals(new URI(endpoint + "/Users"), service.getUri());
    }

    
    @Test(expected = IllegalArgumentException.class)
    public void uuid_is_null_by_updating_single_user_raises_exception() throws Exception {
        givenUUIDisEmpty();
        updateUUID = null;
        updateSingleUser();
        fail("Exception expected");
    }

    @Test(expected = IllegalArgumentException.class)
    public void accessToken_is_null_by_getting_single_user_raises_exception() throws Exception {
        givenUUIDisEmpty();
        accessToken = null;
        updateSingleUser();
        fail("Exception expected");
    }

    @Test(expected = NoResultException.class)
    public void user_does_not_exist() throws IOException {
        givenUUIDcanNotBeFound();
        updateSingleUser();
        fail("Exception expected");
    }

    @Test(expected = UnauthorizedException.class)
    public void expired_access_token() throws Exception {
        givenExpiredAccessTokenIsUsedForUpdate();
        updateSingleUser();
        fail("Exception expected");
    }

    @Test(expected = UnauthorizedException.class)
    public void invalid_access_token() throws Exception {
        givenInvalidAccessTokenIsUsedForUpdate();
        updateSingleUser();
        fail("Exception expected");
    }

    @Test(expected = NoResultException.class)
    public void invalid_UUID_update() throws IOException {
        givenUUIDisInvalid();
        updateSingleUser();
        fail("Exception expected");
    }
    
    private void givenAnAccessToken() throws IOException {
        this.accessToken = tokenProvider.valid_access_token();
    }

    private void givenAnUserUUID() {
        this.updateUUID = UUID.fromString(userUuidString);
    }
    
    private void updateSingleUser() {
    	User.Builder userBuilder = new User.Builder("testuser");
        singleUserResult = service.getUser(updateUUID, accessToken);
        
        userBuilder.setNickName("irgendwas");
        userBuilder.build(); 
    }
    
    private void givenUUIDisEmpty() {
        stubFor(givenUUIDisLookedUp("", accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("query_all_users.json")));
    }    

    private MappingBuilder givenUUIDisLookedUp(String uuidString, AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE + "/" + uuidString))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }
    
    private void givenUUIDcanNotBeFound() {
        stubFor(givenUUIDisLookedUp(userUuidString, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_NOT_FOUND)));
    }
    
    private void givenExpiredAccessTokenIsUsedForUpdate() {
        stubFor(givenUUIDisLookedUp(userUuidString, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_UNAUTHORIZED)));
    }
    
    private void givenInvalidAccessTokenIsUsedForUpdate() {
        stubFor(givenUUIDisLookedUp(userUuidString, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_UNAUTHORIZED)));
    }
    
    private void givenUUIDisInvalid() {
        stubFor(givenUUIDisLookedUp(INVALID_USER_UUID_STRING, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_NOT_FOUND)));
    }
    
    


}
