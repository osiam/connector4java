package org.osiam.client;
/*
 * for licensing see the file license.txt.
 */


import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;

import org.apache.http.entity.ContentType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.update.UpdateUser;
import org.osiam.resources.scim.User;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class OsiamUserEditTest {

    private static final String URL_BASE = "/osiam-server//Users";
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9090); // No-args constructor defaults to port 8080

    final static private String userIdString = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    
    final static private String endpoint = "http://localhost:9090/osiam-server/";
    private String updateID;
    private UpdateUser UPDATE_USER;
    private AccessToken accessToken;
    private AccessTokenMockProvider tokenProvider;

    private User singleUserResult;
    OsiamUserService service;

    @Before
    public void setUp() throws Exception {
        service = new OsiamUserService.Builder(endpoint).build();
        tokenProvider = new AccessTokenMockProvider("/__files/valid_accesstoken.json");
        
        // use happy path for default
        givenAnUserID();
        givenAnAccessToken();
        givenAnUpdateUser();
    }

    @Test
    public void service_returns_correct_uri() throws Exception {
        assertEquals(new URI(endpoint + "/Users"),service.getUri());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void id_is_null_by_updating_single_user_raises_exception() throws Exception {
    	givenIDisEmpty();
        updateID = null;
        updateSingleUser();
        fail("Exception expected");
    }
 
    @Test(expected = IllegalArgumentException.class)
    public void null_access_token__by_updating_user_rases_exception() throws Exception {
        givenIDisEmpty();
        accessToken = null;
        updateSingleUser();
        fail("Exception expected");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void null_UpdateUser_rases_exception() throws Exception {
        givenIDisEmpty();
        UPDATE_USER = null;
        updateSingleUser();
        fail("Exception expected");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void user_is_null_by_getting_single_user_raises_exception() throws Exception {
        givenIDisEmpty();

        updateSingleUserWithEmptyResource();
        fail("Exception expected");
    }

    private void givenAnAccessToken() throws IOException {
        this.accessToken = tokenProvider.valid_access_token();
    }

    private void givenAnUserID() {
        this.updateID = userIdString;
    }
    
    private void givenAnUpdateUser() {
        this.UPDATE_USER = new UpdateUser.Builder().build();
    }

    private void updateSingleUser() {
        service.updateUser(updateID, UPDATE_USER, accessToken);
    }
    
    private void updateSingleUserWithEmptyResource() {
    	singleUserResult = null;
    	service.updateResource(updateID, singleUserResult, accessToken);
    }
    
    private void givenIDisEmpty() {
        stubFor(givenIDisLookedUp("", accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("query_all_users.json")));
    }    

    private MappingBuilder givenIDisLookedUp(String id, AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE + "/" + id))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }

}
