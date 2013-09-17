package org.osiam.client.oauth;
/*
 * for licensing see the file license.txt.
 */

import static org.junit.Assert.fail;

import org.junit.Test;

public class AuthServiceBuilderTest {

    private static final String IRRELEVANT = "irrelevant";

    @Test(expected = IllegalArgumentException.class)
    public void missing_client_secret_raises_exception() {
        new AuthService.Builder(IRRELEVANT).setClientId(IRRELEVANT).build();
        fail("We expected an exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void missing_client_ID_raises_exception() {
        new AuthService.Builder(IRRELEVANT).setClientSecret(IRRELEVANT).build();
        fail("We expected an exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_grant_type_is_password_missing_username_raises_exception() {
        new AuthService.Builder(IRRELEVANT)
                .setClientId(IRRELEVANT)
                .setClientSecret(IRRELEVANT)
                .setGrantType(GrantType.PASSWORD)
                .setPassword(IRRELEVANT).build();
        fail("We expected an exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_grant_type_is_password_missing_password_raises_exception() {
        new AuthService.Builder(IRRELEVANT)
                .setClientId(IRRELEVANT)
                .setClientSecret(IRRELEVANT)
                .setGrantType(GrantType.PASSWORD)
                .setUsername(IRRELEVANT).build();
        fail("We expected an exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_grant_type_is_password_missing_credentials_raise_exception() {
        new AuthService.Builder(IRRELEVANT)
                .setClientId(IRRELEVANT)
                .setClientSecret(IRRELEVANT)
                .setGrantType(GrantType.PASSWORD).build();
        fail("We expected an exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_grant_type_is_missing_raises_exception() {
        new AuthService.Builder(IRRELEVANT)
                .setClientId(IRRELEVANT)
                .setClientSecret(IRRELEVANT)
                .setUsername(IRRELEVANT)
                .setPassword(IRRELEVANT).build();
        fail("We expected an exception");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void when_grant_type_is_client_cridential_password_is_set_raise_exception() {
        new AuthService.Builder(IRRELEVANT)
                .setClientId(IRRELEVANT)
                .setClientSecret(IRRELEVANT)
                .setGrantType(GrantType.CLIENT_CREDENTIALS)
                .setPassword(IRRELEVANT)
                .build();
        fail("We expected an exception");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void when_grant_type_is_client_cridential_username_is_set_raise_exception() {
        new AuthService.Builder(IRRELEVANT)
                .setClientId(IRRELEVANT)
                .setClientSecret(IRRELEVANT)
                .setGrantType(GrantType.CLIENT_CREDENTIALS)
                .setUsername(IRRELEVANT)
                .build();
        fail("We expected an exception");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void when_grant_type_is_access_token_username_is_set_raise_exception() {
        new AuthService.Builder(IRRELEVANT)
                .setClientId(IRRELEVANT)
                .setClientSecret(IRRELEVANT)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setUsername(IRRELEVANT)
                .setClientRedirectUri(IRRELEVANT)
                .build();
        fail("We expected an exception");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void when_grant_type_is_access_token_password_is_set_raise_exception() {
        new AuthService.Builder(IRRELEVANT)
                .setClientId(IRRELEVANT)
                .setClientSecret(IRRELEVANT)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setPassword(IRRELEVANT)
                .setClientRedirectUri(IRRELEVANT)
                .build();
        fail("We expected an exception");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void when_grant_type_is_access_token_redirect_uri_is_not_setraise_exception() {
        new AuthService.Builder(IRRELEVANT)
                .setClientId(IRRELEVANT)
                .setClientSecret(IRRELEVANT)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .build();
        fail("We expected an exception");
    }
}