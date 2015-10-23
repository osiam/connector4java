/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.client;

import org.junit.Before;
import org.junit.Test;
import org.osiam.client.oauth.AccessToken;
import org.osiam.resources.scim.User;

import java.util.Date;

import static org.junit.Assert.fail;

public class OsiamUserServiceTest {

    private static final String USER_ID = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private static final String endpoint = "http://localhost:9090/osiam";

    private String searchedID;
    private AccessToken accessToken;

    private OsiamUserService service;

    @Before
    public void setUp() throws Exception {
        service = new OsiamUserService.Builder(endpoint).build();
        searchedID = USER_ID;
        accessToken = new AccessToken.Builder("c5d116cb-2758-4e7c-9aca-4a115bc4f19e")
                .setExpiresAt(new Date(System.currentTimeMillis() * 2))
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void id_is_null_by_getting_single_user_raises_exception() throws Exception {
        service.getUser(null, accessToken);

        fail("Exception expected");
    }

    @Test(expected = NullPointerException.class)
    public void access_token_is_null_by_getting_single_user_raises_exception() throws Exception {
        service.getUser(searchedID, null);

        fail("Exception expected");
    }

    @Test(expected = NullPointerException.class)
    public void access_token_is_null_by_getting_all_group_raises_exception() throws Exception {
        service.getAllUsers(null);

        fail("Exception expected");
    }

    @Test(expected = NullPointerException.class)
    public void create_null_user_raises_exception() {
        service.createUser(null, accessToken);

        fail("Exception excpected");
    }

    @Test(expected = NullPointerException.class)
    public void create_user_with_null_access_token_raises_exception() {
        User newUser = new User.Builder("irrelevant").build();

        service.createUser(newUser, null);

        fail("Exception excpected");
    }

    @Test(expected = IllegalArgumentException.class)
    public void delete_null_user_raises_exception() {
        service.deleteUser(null, accessToken);

        fail("Exception excpected");
    }

    @Test(expected = NullPointerException.class)
    public void delete_user_with_null_access_token_raises_exception() {
        service.deleteUser("irrelevant", null);

        fail("Exception excpected");
    }
}
