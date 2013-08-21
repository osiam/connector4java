package org.osiam.client.oauth;
/*
 * for licensing see the file license.txt.
 */

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

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
                {GrantType.PASSWORD, "password"}
        });
    }

    @Test
    public void grant_type_returns_matching_urlparam() {
        assertEquals(expected, grantType.getUrlParam());
    }
}
