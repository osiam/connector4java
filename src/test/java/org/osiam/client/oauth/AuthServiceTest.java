package org.osiam.client.oauth;
/*
 * for licensing see the file license.txt.
 */

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.UnauthorizedException;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

public class AuthServiceTest {
    
    private final static String ENDPOINT = "http://localhost:9090/osiam-server/";
    private static final String WRONG_ENDPOINT = "http://localhost:9090/wrong-osiam-server/";
    private final static String TOKEN_PATH = "/oauth/token";
    private final static String VALID_CLIENT_ID = "valid-client";
    private final static String VALID_CLIENT_SECRET = "valid_secret";
    private static final String INVALID_CLIENT_ID = "invalid-client";
    private static final String INVALID_CLIENT_SECRET = "invalid-secret";
    private final static String VALID_USERNAME = "valid-username";
    private final static String VALID_PASSWORD = "valid-password";
    private final static String ACCESS_TOKEN_STRING = "c5d116cb-2758-4e7c-9aca-4a115bc4f19e";

    @Rule
    public WireMockClassRule wireMockRule = new WireMockClassRule(9090);

    private AuthService service;
    private AccessToken accessToken;

    @Test
    public void service_returns_valid_access_token() throws Exception {
        given_a_correctly_configured_auth_service();
        given_oauth_server_issues_access_token();

        when_token_is_requested();
        
        then_access_token_is_valid();
    }

    @Test
    public void service_returns_expected_access_token() {
        given_a_correctly_configured_auth_service();
        given_oauth_server_issues_access_token();

        when_token_is_requested();
        
        then_access_token_is_expected_one();
    }
    
    @Test(expected=ConnectionInitializationException.class)
    public void service_fails_on_bad_request() {
        given_a_correctly_configured_auth_service();
        given_oauth_server_cannot_issues_access_token_because_of_bad_request();
        
        when_token_is_requested();
        
        then_exception_has_to_be_thrown();
    }
    
    @Test(expected=UnauthorizedException.class)
    public void service_fails_on_unauthorized() {
        given_a_wrong_configured_auth_service_with_invalid_client_credentials();
        given_oauth_server_cannot_issues_access_token_because_of_wrong_auth();
        
        when_token_is_requested();
        
        then_exception_has_to_be_thrown();
    }
    
    @Test(expected=ConnectionInitializationException.class)
    public void service_fails_on_not_found() {
        given_a_wrong_configured_auth_service_with_wrong_endpoint();
        given_oauth_server_issues_access_token();
        
        when_token_is_requested();
        
        then_exception_has_to_be_thrown();
    }
    
    @Test(expected=ConnectionInitializationException.class)
    public void service_fails_on_other_error() {
        given_a_correctly_configured_auth_service();
        given_oauth_server_has_error();
        
        when_token_is_requested();
        
        then_exception_has_to_be_thrown();
    }

    private void given_a_wrong_configured_auth_service_with_wrong_endpoint() {
        service = new AuthService.Builder(WRONG_ENDPOINT)
                .grantType(GrantType.PASSWORD)
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .username(VALID_USERNAME)
                .password(VALID_PASSWORD)
                .build();
    }
    
    private void given_a_wrong_configured_auth_service_with_invalid_client_credentials() {
        service = new AuthService.Builder(ENDPOINT)
                .grantType(GrantType.PASSWORD)
                .clientId(INVALID_CLIENT_ID)
                .clientSecret(INVALID_CLIENT_SECRET)
                .username(VALID_USERNAME)
                .password(VALID_PASSWORD)
                .build();
    }
    
    private void given_a_correctly_configured_auth_service() {
        service = new AuthService.Builder(ENDPOINT)
                .grantType(GrantType.PASSWORD)
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .username(VALID_USERNAME)
                .password(VALID_PASSWORD)
                .build();
    }

    private void given_oauth_server_issues_access_token() {
        stubFor(post(urlEqualTo("/osiam-server/" + TOKEN_PATH)).
                willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBodyFile("valid_accesstoken.json")));
    }
    
    private void given_oauth_server_has_error() {
        stubFor(post(urlEqualTo("/osiam-server/" + TOKEN_PATH)).
                willReturn(aResponse()
                        .withStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR)));
    }
        
    private void given_oauth_server_cannot_issues_access_token_because_of_bad_request() {
        stubFor(post(urlEqualTo("/osiam-server/" + TOKEN_PATH))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_BAD_REQUEST)));
    }

    private void given_oauth_server_cannot_issues_access_token_because_of_wrong_auth() {
        stubFor(post(urlEqualTo("/osiam-server/" + TOKEN_PATH))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_UNAUTHORIZED)));
    }
    
    private void when_token_is_requested() {
        accessToken = service.retrieveAccessToken();
    }

    private void then_exception_has_to_be_thrown() {
        fail("Exception expected");
    }

    private void then_access_token_is_expected_one() {
        assertEquals(ACCESS_TOKEN_STRING, accessToken.getToken());
    }

    private void then_access_token_is_valid() throws Exception {
        assertFalse(accessToken.isExpired());
    }
}
