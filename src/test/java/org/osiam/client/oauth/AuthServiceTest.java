package org.osiam.client.oauth;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;

import java.net.URI;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class AuthServiceTest {

    @Rule
    public WireMockClassRule wireMockRule = new WireMockClassRule(9090);

    private final static String CHARSET = "UTF-8";
    private final static String ENDPOINT = "http://localhost:9090/osiam-server/";
    private final static String TOKEN_PATH = "/oauth/token";
    private final static String VALID_CLIENT_ID = "valid-client";
    private final static String VALID_CLIENT_SECRET = "valid_secret";
    private final static String VALID_USERNAME = "valid-username";
    private final static String VALID_PASSWORD = "valid-password";
    private final static String VALID_SCOPE = "GET POST PUT PATCH DELETE";
    private final static String ACCESS_TOKEN_STRING = "c5d116cb-2758-4e7c-9aca-4a115bc4f19e";

    private AuthService service;
    private AccessToken accessToken;

    @Test
    public void service_uses_correct_URI() throws Exception {
        given_a_correctly_configured_auth_service();

        assertEquals(new URI(ENDPOINT + TOKEN_PATH), service.getUri());
    }

    @Test
    public void service_returns_valid_access_token() throws Exception {
        given_a_correctly_configured_auth_service();
        given_oauth_server_issues_access_token();
        verify_access_token_is_valid();

    }

    @Test
    public void service_returns_expected_access_token(){
        given_a_correctly_configured_auth_service();
        given_oauth_server_issues_access_token();

    }

    private void given_oauth_server_issues_access_token() {
        stubFor(post(urlEqualTo("/osiam-server/" + TOKEN_PATH)).
                willReturn(aResponse().withStatus(HttpStatus.SC_OK)
                        .withBodyFile("valid_accesstoken.json")));
    }


    private void given_a_correctly_configured_auth_service() {
        service = new AuthService.Builder(ENDPOINT)
                .withGrantType(GrantType.PASSWORD)
                .withClientId(VALID_CLIENT_ID)
                .withClientSecret(VALID_CLIENT_SECRET)
                .withUsername(VALID_USERNAME)
                .withPassword(VALID_PASSWORD)
                .build();
    }

    private void verify_access_token_is_expected(){
        accessToken = service.retrieveAccessToken();
        assertEquals(ACCESS_TOKEN_STRING, accessToken.getToken());
    }
    private void verify_access_token_is_valid() throws Exception {
        accessToken = service.retrieveAccessToken();
        assertFalse(accessToken.isExpired());
    }

    private String encodeClientCredentials(String clientId, String clientSecret) throws Exception {
        String clientCredentials = clientId + ":" + clientSecret;
        String encoded = new String(Base64.encodeBase64(clientCredentials.getBytes(CHARSET)), CHARSET);
        return encoded;
    }
}
