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

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.InvalidAttributeException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.client.user.BasicUser;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.UpdateUser;
import org.osiam.resources.scim.User;

import com.google.common.base.Strings;

/**
 * The OsiamUserService provides all methods necessary to manipulate the User-entries registered in the given OSIAM
 * installation. For the construction of an instance please use the included {@link OsiamUserService.Builder}
 */
class OsiamUserService extends AbstractOsiamService<User> { // NOSONAR - Builder constructs instances of
                                                                         // this class

    /**
     * The private constructor for the OsiamUserService. Please use the {@link OsiamUserService.Builder} to construct
     * one.
     *
     * @param builder
     *        a Builder to build the service from
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
     */
    public BasicUser getCurrentUserBasic(AccessToken accessToken) {
        checkAccessTokenIsNotNull(accessToken);

        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path("me").request(MediaType.APPLICATION_JSON)
                    .header("Authorization", BEARER + accessToken.getToken())
                    .get();

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        checkAndHandleResponse(content, status, accessToken, "get me", null);

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
     */
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

    /**
     * See {@link OsiamConnector.Builder}
     */
    public static class Builder extends AbstractOsiamService.Builder<User> {

        /**
         * Set up the Builder for the construction of an {@link OsiamUserService} instance for the OSIAM service at the
         * given endpoint
         *
         * @param endpoint
         *        The URL at which the OSIAM server lives.
         */
        public Builder(String endpoint) {
            super(endpoint);
        }

        /**
         * See {@link OsiamConnector.Builder#build()}
         */
        public OsiamUserService build() {
            return new OsiamUserService(this);
        }
    }
}
