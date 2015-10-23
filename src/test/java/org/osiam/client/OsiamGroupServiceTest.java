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
import org.osiam.resources.scim.Group;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.fail;

public class OsiamGroupServiceTest {

    private static final String GROUP_ID_STRING = "55bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private static final String ENDPOINT = "http://localhost:9090/osiam";

    private String searchedId;
    private AccessToken accessToken;
    private OsiamGroupService service;

    @Before
    public void setUp() throws IOException {
        service = new OsiamGroupService.Builder(ENDPOINT).build();
        searchedId = GROUP_ID_STRING;
        accessToken = new AccessToken.Builder("c5d116cb-2758-4e7c-9aca-4a115bc4f19e")
                .setExpiresAt(new Date(System.currentTimeMillis() * 2))
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void id_is_null_by_getting_single_user_raises_exception() throws Exception {
        service.getGroup(null, accessToken);

        fail("Exception expected");
    }

    @Test(expected = NullPointerException.class)
    public void accessToken_is_null_by_getting_single_group_raises_exception() throws Exception {
        service.getGroup(searchedId, null);

        fail("Exception expected");
    }

    @Test(expected = NullPointerException.class)
    public void accessToken_is_null_by_getting_all_group_raises_exception() throws Exception {
        service.getAllGroups(null);

        fail("Exception expected");
    }

    @Test(expected = NullPointerException.class)
    public void create_null_group_raises_exception() {
        service.createGroup(null, accessToken);

        fail("Exception excpected");
    }

    @Test(expected = NullPointerException.class)
    public void create_group_with_null_accestoken_raises_exception() {
        Group newGroup = new Group.Builder().build();

        service.createGroup(newGroup, null);

        fail("Exception excpected");
    }

    @Test(expected = IllegalArgumentException.class)
    public void delete_null_group_raises_exception() {
        service.deleteGroup(null, accessToken);

        fail("Exception excpected");
    }

    @Test(expected = NullPointerException.class)
    public void delete_group_with_null_accestoken_raises_exception() {
        service.deleteGroup("irrelevant", null);

        fail("Exception excpected");
    }
}
