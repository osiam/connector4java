package org.osiam.client.oauth;

import org.junit.Test;
import org.osiam.client.exception.UnauthorizedException;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class AuthServiceTest {

    private final static String ENDPOINT = "http://localhost:8080/osiam-server/";
    private final static String TOKEN_PATH = "/oauth/token";
    private final static String VALID_CLIENT_ID = "valid-client";
    private final static String VALID_CLIENT_SECRET = "valid_secret";
    private final static String VALID_USERNAME = "valid-username";
    private final static String VALID_PASSWORD = "valid-password";


    private AuthService service;

    @Test
    public void service_uses_correct_URI() throws URISyntaxException {
        given_a_correctly_configured_auth_service();
        System.out.println(service.getUri());
        assertEquals(new URI(ENDPOINT + TOKEN_PATH), service.getUri() );
    }



    public void given_a_correctly_configured_auth_service(){
        service = new AuthService.Builder(ENDPOINT)
                .withGrantType(GrantType.PASSWORD)
                .withClientId(VALID_CLIENT_ID)
                .withClientSecret(VALID_CLIENT_SECRET)
                .withUsername(VALID_USERNAME)
                .withPassword(VALID_PASSWORD)
                .build();
    }
}
