package org.osiam.client.oauth;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class GrantTypeTest {

    private GrantType grantType;
    private String expected;

    public GrantTypeTest(GrantType grantType, String expected) {
        this.grantType = grantType;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> generateTestData() {
        return Arrays.asList(new Object[][]{
                {GrantType.AUTHORIZATION_CODE, "authorization_code"},
                {GrantType.CLIENT_CREDENTIALS, "client_credentials"},
                {GrantType.PASSWORD, "password"},
                {GrantType.REFRESH_TOKEN, "refresh-token"}
        });
    }

    @Test
    public void grant_type_returns_matching_urlparam() {
        assertEquals(expected, grantType.getUrlParam());
    }
}
