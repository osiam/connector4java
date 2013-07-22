package org.osiam.client.oauth;

import org.junit.Test;
import org.osiam.client.exception.ConnectionInitializationException;

import static org.junit.Assert.fail;

public class OAuthClientRequestBuilderTest {

    private static final String IRRELEVANT = "irrelevant";

    @Test(expected = UnsupportedOperationException.class)
    public void wrong_grant_type_raises_exception(){
        new OAuthClientRequest.Builder(IRRELEVANT).withGrantType(GrantType.CLIENT_CREDENTIALS);
        fail();
    }

    @Test(expected = ConnectionInitializationException.class)
    public void missing_client_secret_raises_exception(){
        new OAuthClientRequest.Builder(IRRELEVANT).withClientSecret(IRRELEVANT).buildRequest();
        fail();
    }

    @Test(expected = ConnectionInitializationException.class)
    public void missing_client_ID_raises_exception(){
        new OAuthClientRequest.Builder(IRRELEVANT).withClientId(IRRELEVANT).buildRequest();
        fail();
    }

    @Test(expected = ConnectionInitializationException.class)
    public void when_grant_type_is_password_missing_username_raises_exception(){
        new OAuthClientRequest.Builder(IRRELEVANT).withClientId(IRRELEVANT)
                .withClientSecret(IRRELEVANT)
                .withGrantType(GrantType.PASSWORD).withUsername(IRRELEVANT).buildRequest();
        fail();
    }

    @Test(expected = ConnectionInitializationException.class)
    public void when_grant_type_is_password_missing_password_raises_exception(){
        new OAuthClientRequest.Builder(IRRELEVANT).withClientId(IRRELEVANT)
                .withClientSecret(IRRELEVANT)
                .withGrantType(GrantType.PASSWORD).withUsername(IRRELEVANT).buildRequest();
        fail();
    }

    @Test(expected = ConnectionInitializationException.class)
    public void when_grant_type_is_password_missing_credentials_raise_exception(){
        new OAuthClientRequest.Builder(IRRELEVANT)
                .withClientId(IRRELEVANT)
                .withClientSecret(IRRELEVANT)
                .withGrantType(GrantType.PASSWORD).buildRequest();
    }

}

