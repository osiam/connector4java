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

import com.google.common.base.Strings;
import org.glassfish.jersey.client.ClientProperties;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.InvalidAttributeException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.client.user.BasicUser;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.UpdateUser;
import org.osiam.resources.scim.User;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import java.util.List;

/**
 * The OsiamUserService provides all methods necessary to manipulate the User-entries registered in the given OSIAM
 * installation. For the construction of an instance please use the included {@link OsiamUserService.Builder}
 */
class OsiamUserService extends AbstractOsiamService<User> {

    static final String LEGACY_SCHEMA = "urn:scim:schemas:core:2.0:User";

    /**
     * The private constructor for the OsiamUserService. Please use the {@link OsiamUserService.Builder} to construct
     * one.
     *
     * @param builder a Builder to build the service from
     */
    private OsiamUserService(Builder builder) {
        super(builder);
    }

    /**
     * See {@link OsiamConnector#getUser(String, AccessToken)}
     */
    public User getUser(String id, AccessToken accessToken) {
        return getResource(id, accessToken);
    }

    /**
     * See {@link OsiamConnector#getCurrentUserBasic(AccessToken)}
     *
     * @deprecated Please use getMe(accessToken)
     */
    public BasicUser getCurrentUserBasic(AccessToken accessToken) {
        String content = getMeResource(accessToken);

        return mapToType(content, BasicUser.class);
    }

    /**
     * See {@link OsiamConnector#getCurrentUser(AccessToken)}
     */
    public User getCurrentUser(AccessToken accessToken) {
        BasicUser basicUser = getCurrentUserBasic(accessToken);
        return getResource(basicUser.getId(), accessToken);
    }

    /**
     * See {@link OsiamConnector#getMe(AccessToken)}
     */
    public User getMe(AccessToken accessToken) {
        String content = getMeResource(accessToken);

        return mapToType(content, User.class);
    }

    /**
     * See {@link OsiamConnector#getAllUsers(AccessToken)}
     */
    public List<User> getAllUsers(AccessToken accessToken) {
        return super.getAllResources(accessToken);
    }

    /**
     * See {@link OsiamConnector#searchUsers(Query, AccessToken)}
     */
    public SCIMSearchResult<User> searchUsers(Query query, AccessToken accessToken) {
        return searchResources(query, accessToken);
    }

    /**
     * See {@link OsiamConnector#deleteUser(String, AccessToken)}
     */
    public void deleteUser(String id, AccessToken accessToken) {
        deleteResource(id, accessToken);
    }

    /**
     * See {@link OsiamConnector#createUser(User, AccessToken)}
     */
    public User createUser(User user, AccessToken accessToken) {
        return createResource(user, accessToken);
    }

    /**
     * See {@link OsiamConnector#updateUser(String, UpdateUser, AccessToken)}
     * @deprecated Updating with PATCH has been removed in OSIAM 3.0. This method is going to go away with version 1.12 or 2.0.
     */
    @Deprecated
    public User updateUser(String id, UpdateUser updateUser, AccessToken accessToken) {
        if (updateUser == null) {
            throw new IllegalArgumentException("The given updateUser can't be null.");
        }
        return updateResource(id, updateUser.getScimConformUpdateUser(), accessToken);
    }

    /**
     * See {@link OsiamConnector#replaceUser(String, User, AccessToken)}
     */
    public User replaceUser(String id, User user, AccessToken accessToken) {
        if (user == null) {
            throw new InvalidAttributeException("The given User can't be null.");
        }
        if (Strings.isNullOrEmpty(id)) {
            throw new InvalidAttributeException("The given User ID can't be null or empty.");
        }
        return replaceResource(id, user, accessToken);
    }

    @Override
    protected String getSchema() {
        return User.SCHEMA;
    }

    @Override
    protected String getLegacySchema() {
        return LEGACY_SCHEMA;
    }

    private String getMeResource(AccessToken accessToken) {
        checkAccessTokenIsNotNull(accessToken);

        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path("Me").request(MediaType.APPLICATION_JSON)
                    .header("Authorization", BEARER + accessToken.getToken())
                    .property(ClientProperties.CONNECT_TIMEOUT, getConnectTimeout())
                    .property(ClientProperties.READ_TIMEOUT, getReadTimeout())
                    .get();

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        checkAndHandleResponse(content, status, accessToken);
        return content;
    }

    /**
     * See {@link OsiamConnector.Builder}
     */
    public static class Builder extends AbstractOsiamService.Builder<User> {

        /**
         * Set up the Builder for the construction of an {@link OsiamUserService} instance for the OSIAM service at the
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
         * Configures the user service to use legacy schemas, i.e. schemas that were defined before
         * SCIM 2 draft 09.
         * <p>
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
        public OsiamUserService build() {
            return new OsiamUserService(this);
        }
    }
}
