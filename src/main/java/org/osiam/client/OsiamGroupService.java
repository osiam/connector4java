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

import java.util.List;

import org.osiam.client.connector.OsiamConnector; // NOSONAR : needed for Javadoc
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.UpdateGroup;

/**
 * OsiamGroupService provides all methods necessary to manipulate the {@link Group} resources registered in the
 * given OSIAM installation. For the construction of an instance please use the {@link OsiamGroupService.Builder}
 */
public final class OsiamGroupService extends AbstractOsiamService<Group> { // NOSONAR - Builder constructs instances of this class

    /**
     * The private constructor for the OsiamGroupService. Please use the {@link OsiamGroupService.Builder}
     * to construct one.
     *
     * @param builder The Builder to build the OsiamGroupService from
     */
    private OsiamGroupService(Builder builder) {
        super(builder);
    }

    /**
     * See {@link OsiamConnector#getGroup(String, AccessToken)}
     */
    public Group getGroup(String id, AccessToken accessToken) {
        return getResource(id, accessToken);
    }

    /**
     * See {@link OsiamConnector#getAllGroups(AccessToken)}
     */
    public List<Group> getAllGroups(AccessToken accessToken) {
        return getAllResources(accessToken);
    }

    /**
     * See {@link OsiamConnector#searchGroups(String, AccessToken)}
     */
    public SCIMSearchResult<Group> searchGroups(String queryString, AccessToken accessToken) {
        return searchResources(queryString, accessToken);
    }

    /**
     * See {@link OsiamConnector#searchGroups(Query, AccessToken)}
     */
    public SCIMSearchResult<Group> searchGroups(Query query, AccessToken accessToken) {
        return searchResources(query, accessToken);
    }

    /**
     * See {@link OsiamConnector#deleteUser(String, AccessToken)}
     */
    public void deleteGroup(String id, AccessToken accessToken) {
        deleteResource(id, accessToken);
    }

    /**
     * See {@link OsiamConnector#createGroup(Group, AccessToken)}
     */
    public Group createGroup(Group group, AccessToken accessToken) {
        return createResource(group, accessToken);
    }

    /**
     * See {@link OsiamConnector#updateGroup(String, UpdateGroup, AccessToken)}
     */
    public Group updateGroup(String id, UpdateGroup updateGroup, AccessToken accessToken) {
        return updateResource(id, updateGroup.getScimConformUpdateGroup(), accessToken);
    }

    /**
     * See {@link OsiamConnector#updateGroup(String, Group, AccessToken)}
     */
    public Group updateGroup(String id, Group group, AccessToken accessToken) {
        return updateResource(id, group, accessToken);
    }

    /**
     * See {@link OsiamConnector#replaceGroup(String, Group, AccessToken)}
     */
    public Group replaceGroup(String id, Group group, AccessToken accessToken){
        return replaceResource(id, group, accessToken);
    }

    /**
     * See {@link OsiamConnector.Builder}
     */
    public static class Builder extends AbstractOsiamService.Builder<Group> {

        /**
         * Set up the Builder for the construction of  an {@link OsiamGroupService} instance for the OSIAM service at
         * the given endpoint
         *
         * @param endpoint The URL at which the OSIAM server lives.
         */
        public Builder(String endpoint) {
            super(endpoint);
        }

        /**
         * See {@link OsiamConnector.Builder#build()}
         */
        public OsiamGroupService build() {
            return new OsiamGroupService(this);
        }
    }
}
