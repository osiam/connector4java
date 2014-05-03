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

import java.io.IOException;
import java.util.List;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import org.osiam.client.connector.OsiamConnector; // NOSONAR : needed for Javadoc
import org.osiam.client.exception.ConflictException;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.ForbiddenException;
import org.osiam.client.exception.OsiamRequestException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.nquery.Query;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.user.BasicUser;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.UpdateUser;
import org.osiam.resources.scim.User;

import com.google.common.base.Strings;

/**
 * The OsiamUserService provides all methods necessary to manipulate the User-entries registered in the given OSIAM
 * installation. For the construction of an instance please use the included {@link OsiamUserService.Builder}
 */
public final class OsiamUserService extends AbstractOsiamService<User> { // NOSONAR - Builder constructs instances of
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

        if (status.getStatusCode() != Status.OK.getStatusCode()) {
            if (status.getStatusCode() == Status.UNAUTHORIZED.getStatusCode()) {
                String errorMessage = extractErrorMessageUnauthorized(content, status);
                throw new UnauthorizedException(errorMessage);
            } else if (status.getStatusCode() == Status.FORBIDDEN.getStatusCode()) {
                String errorMessage = extractErrorMessageForbidden(accessToken, "get");
                throw new ForbiddenException(errorMessage);
            } else if (status.getStatusCode() == Status.CONFLICT.getStatusCode()) {
                String errorMessage = extractErrorMessage(content, status, "Unable to retrieve the actual User.");
                throw new ConflictException(errorMessage);
            } else {
                String errorMessage = extractErrorMessageDefault(content, status);
                throw new OsiamRequestException(status.getStatusCode(), errorMessage);
            }
        }

        try {
            return mapper.readValue(content, BasicUser.class);
        } catch (IOException e) {
            // FIXME: replace with an other exception and add the content to it
            // IOException means mapping could not be done by jackson
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        } catch (ProcessingException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }
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
     * See {@link OsiamConnector#searchUsers(String, AccessToken)}
     * @deprecated Use {@link OsiamUserService#searchUsers(Query, AccessToken)} instead
     */
    @Deprecated
    public SCIMSearchResult<User> searchUsers(String queryString, AccessToken accessToken) {
        return super.searchResources(queryString, accessToken);
    }

    /**
     * See {@link OsiamConnector#searchUsers(org.osiam.client.query.Query, AccessToken)}
     * @deprecated Use {@link OsiamUserService#searchUsers(Query, AccessToken)} instead
     */
    @Deprecated
    public SCIMSearchResult<User> searchUsers(org.osiam.client.query.Query query, AccessToken accessToken) {
        return super.searchResources(query, accessToken);
    }

    /**
     * See {@link OsiamConnector#searchUsers(Query, AccessToken)}
     */
    public SCIMSearchResult<User> searchUsers(Query query, AccessToken accessToken) {
        // TODO implement
        return null;
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
     * See {@link OsiamConnector#replaceUser(User, AccessToken)}
     */
    public User replaceUser(String id, User user, AccessToken accessToken) {
        if (user == null) {
            throw new IllegalArgumentException("The given User can't be null.");
        }
        if (Strings.isNullOrEmpty(id)) {
            throw new IllegalArgumentException("The given User ID can't be null or empty.");
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
