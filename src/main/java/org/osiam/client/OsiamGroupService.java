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
 * OSIAM installation. For the construction of an instance please use the {@link OsiamGroupService.Builder}
 */
class OsiamGroupService extends AbstractOsiamService<Group> { // NOSONAR - Builder constructs instances of
    // this class

    private static final String LEGACY_SCHEMA = "urn:scim:schemas:core:2.0:Group";

    /**
     * The private constructor for the OsiamGroupService. Please use the {@link OsiamGroupService.Builder} to construct
     * one.
     *
     * @param builder The Builder to build the OsiamGroupService from
     */
    private OsiamGroupService(Builder builder) {
        super(builder);
    }

    /**
     * See {@link OsiamConnector#getGroup(String, AccessToken)}
     */
    Group getGroup(String id, AccessToken accessToken) {
        return getResource(id, accessToken);
    }

    /**
     * See {@link OsiamConnector#getAllGroups(AccessToken)}
     */
    List<Group> getAllGroups(AccessToken accessToken) {
        return getAllResources(accessToken);
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
     * @deprecated Updating with PATCH has been removed in OSIAM 3.0. This method is going to go away with version 1.12 or 2.0.
     */
    @Deprecated
    Group updateGroup(String id, UpdateGroup updateGroup, AccessToken accessToken) {
        return updateResource(id, updateGroup.getScimConformUpdateGroup(), accessToken);
    }

    /**
     * See {@link OsiamConnector#updateGroup(String, UpdateGroup, AccessToken)}
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

    /**
     * See {@link OsiamConnector.Builder}
     */
    public static class Builder extends AbstractOsiamService.Builder<Group> {

        /**
         * Set up the Builder for the construction of an {@link OsiamGroupService} instance for the OSIAM service at the
         * given endpoint
         *
         * @param endpoint The URL at which OSIAM lives.
         */
        public Builder(String endpoint) {
            super(endpoint);
        }

        /**
         * Set the connect timeout per connector, in milliseconds.
         * <p/>
         * <p>
         * A value of zero (0) is equivalent to an interval of infinity. Default: 2500
         * </p>
         *
         * @param connectTimeout the connect timeout per connector, in milliseconds.
         * @return The builder itself
         */
        public Builder withConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        /**
         * Set the read timeout per connector, in milliseconds.
         * <p/>
         * <p>
         * A value of zero (0) is equivalent to an interval of infinity. Default: 5000
         * </p>
         *
         * @param readTimeout the read timeout per connector, in milliseconds.
         * @return The builder itself
         */
        public Builder withReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        /**
         * Configures the group service to use legacy schemas, i.e. schemas that were defined
         * before SCIM 2 draft 09.
         *
         * <p/>This enables compatibility with OSIAM releases up to version 2.3
         * (resource-server 2.2). This behavior is not enabled by default. Set it to `true` if you
         * connect to an OSIAM version <= 2.3 and, please, update to 2.5 or later immediately.
         *
         * @param legacySchemas should legacy schemas be used
         * @return The builder itself
         */
        public Builder withLegacySchemas(boolean legacySchemas) {
            this.legacySchemas = legacySchemas;
            return this;
        }

        /**
         * See {@link OsiamConnector.Builder#build()}
         */
        public OsiamGroupService build() {
            return new OsiamGroupService(this);
        }
    }
}
