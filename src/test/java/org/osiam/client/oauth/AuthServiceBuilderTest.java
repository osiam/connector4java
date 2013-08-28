package org.osiam.client.oauth;
/*
 * for licensing see the file license.txt.
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URI;

import org.junit.Test;
import org.osiam.client.exception.ConnectionInitializationException;

public class AuthServiceBuilderTest {

    private static final String IRRELEVANT = "irrelevant";

    @Test(expected = ConnectionInitializationException.class)
    public void missing_client_secret_raises_exception() {
        new AuthService.Builder(IRRELEVANT).clientId(IRRELEVANT).build();
        fail("We expected an exception");
    }

    @Test(expected = ConnectionInitializationException.class)
    public void missing_client_ID_raises_exception() {
        new AuthService.Builder(IRRELEVANT).clientSecret(IRRELEVANT).build();
        fail("We expected an exception");
    }

    @Test(expected = ConnectionInitializationException.class)
    public void when_grant_type_is_password_missing_username_raises_exception() {
        new AuthService.Builder(IRRELEVANT)
                .clientId(IRRELEVANT)
                .clientSecret(IRRELEVANT)
                .grantType(GrantType.PASSWORD)
                .password(IRRELEVANT).build();
        fail("We expected an exception");
    }

    @Test(expected = ConnectionInitializationException.class)
    public void when_grant_type_is_password_missing_password_raises_exception() {
        new AuthService.Builder(IRRELEVANT)
                .clientId(IRRELEVANT)
                .clientSecret(IRRELEVANT)
                .grantType(GrantType.PASSWORD)
                .username(IRRELEVANT).build();
        fail("We expected an exception");
    }

    @Test(expected = ConnectionInitializationException.class)
    public void when_grant_type_is_password_missing_credentials_raise_exception() {
        new AuthService.Builder(IRRELEVANT)
                .clientId(IRRELEVANT)
                .clientSecret(IRRELEVANT)
                .grantType(GrantType.PASSWORD).build();
        fail("We expected an exception");
    }

    @Test(expected = ConnectionInitializationException.class)
    public void when_grant_type_is_missing_raises_exception() {
        new AuthService.Builder(IRRELEVANT)
                .clientId(IRRELEVANT)
                .clientSecret(IRRELEVANT)
                .username(IRRELEVANT)
                .password(IRRELEVANT).build();
        fail("We expected an exception");
    }
}