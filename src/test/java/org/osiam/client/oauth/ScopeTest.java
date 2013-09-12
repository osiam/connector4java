package org.osiam.client.oauth;
/*
 * for licensing see the file license.txt.
 */

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ScopeTest {

    @Test
    public void scope_all_is_right_toString() {
        assertEquals("GET POST PUT PATCH DELETE", Scope.ALL.toString());
    }
}
