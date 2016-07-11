/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.client;

import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.UpdateGroup;

import java.util.List;

/**
 * OsiamGroupService provides all methods necessary to manipulate the {@link Group} resources registered in the given
 * OSIAM installation.
 */
class OsiamGroupService extends AbstractOsiamService<Group> {

    private static final String LEGACY_SCHEMA = "urn:scim:schemas:core:2.0:Group";

    OsiamGroupService(String endpoint, int connectTimeout, int readTimeout, Version version) {
        super(endpoint, Group.class, connectTimeout, readTimeout, version);
    }

    /**
     * See {@link OsiamConnector#getGroup(String, AccessToken)}
     */
    Group getGroup(String id, AccessToken accessToken, String... attributes) {
        return getResource(id, accessToken, attributes);
    }

    /**
     * See {@link OsiamConnector#getAllGroups(AccessToken)}
     */
    List<Group> getAllGroups(AccessToken accessToken, String... attributes) {
        return getAllResources(accessToken, attributes);
    }

    /**
     * See {@link OsiamConnector#searchGroups(Query, AccessToken)}
     */
    SCIMSearchResult<Group> searchGroups(Query query, AccessToken accessToken) {
        return searchResources(query, accessToken);
    }

    /**
     * See {@link OsiamConnector#deleteUser(String, AccessToken)}
     */
    void deleteGroup(String id, AccessToken accessToken) {
        deleteResource(id, accessToken);
    }

    /**
     * See {@link OsiamConnector#createGroup(Group, AccessToken)}
     */
    Group createGroup(Group group, AccessToken accessToken) {
        return createResource(group, accessToken);
    }

    /**
     * See {@link OsiamConnector#updateGroup(String, UpdateGroup, AccessToken)}
     *
     * @deprecated Updating with PATCH has been removed in OSIAM 3.0. This method is going to go away with version 1.12 or 2.0.
     */
    @Deprecated
    Group updateGroup(String id, UpdateGroup updateGroup, AccessToken accessToken) {
        return updateResource(id, updateGroup.getScimConformUpdateGroup(), accessToken);
    }

    /**
     * See {@link OsiamConnector#updateGroup(String, UpdateGroup, AccessToken)}
     *
     * @deprecated Updating with PATCH has been removed in OSIAM 3.0. This method is going to go away with version 1.12 or 2.0.
     */
    @Deprecated
    Group updateGroup(String id, Group group, AccessToken accessToken) {
        return updateResource(id, group, accessToken);
    }

    /**
     * See {@link OsiamConnector#replaceGroup(String, Group, AccessToken)}
     */
    Group replaceGroup(String id, Group group, AccessToken accessToken) {
        return replaceResource(id, group, accessToken);
    }

    @Override
    protected String getSchema() {
        return Group.SCHEMA;
    }

    @Override
    protected String getLegacySchema() {
        return LEGACY_SCHEMA;
    }
}
