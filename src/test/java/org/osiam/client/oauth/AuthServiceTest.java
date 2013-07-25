package org.osiam.client.oauth;

import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;

import java.net.URI;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

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
        verify_access_token_is_requested_correctly();

    }

    private void given_oauth_server_issues_access_token() {
        stubFor(post(urlEqualTo("/osiam-server/" + TOKEN_PATH)).
                willReturn(aResponse().withStatus(HttpStatus.SC_OK)
                        .withBodyFile("valid_accesstoken.json")));
        accessToken = service.retrieveAccessToken();
    }

    private void verify_access_token_is_requested_correctly() throws Exception {

        verify(postRequestedFor(urlEqualTo("/osiam-server/" + TOKEN_PATH))
                .withHeader("Authorization", equalTo("Bearer " + encodeClientCredentials(VALID_CLIENT_ID, VALID_CLIENT_SECRET)))
        );

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

    private String encodeClientCredentials(String clientId, String clientSecret) throws Exception {
        String clientCredentials = clientId + ":" + clientSecret;
        String encoded = new String(Base64.encodeBase64(clientCredentials.getBytes(CHARSET)), CHARSET);
        return encoded;
    }
}
