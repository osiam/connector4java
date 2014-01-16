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

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.osiam.client.connector.OsiamConnector;
import org.osiam.client.exception.ConflictException;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.ForbiddenException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.client.update.UpdateUser;
import org.osiam.client.user.BasicUser;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.User;

import com.fasterxml.jackson.databind.ObjectMapper;

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
        if (accessToken == null) {
            throw new IllegalArgumentException("The given accessToken can't be null.");
        }

        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();

            URI uri = new URI(getMeWebResource().getURI().toString());

            HttpGet realWebResource = new HttpGet(uri);
            realWebResource.addHeader(AUTHORIZATION, BEARER + accessToken.getToken());

            HttpResponse response = httpclient.execute(realWebResource);
            int httpStatus = response.getStatusLine().getStatusCode();

            if (httpStatus != SC_OK) {
                String errorMessage;
                switch (httpStatus) {
                case SC_UNAUTHORIZED:
                    errorMessage = getErrorMessage(response,
                            "You are not authorized to access OSIAM. Please make sure your access token is valid");
                    throw new UnauthorizedException(errorMessage);
                case SC_FORBIDDEN:
                    errorMessage = "Insufficient scope (" + accessToken.getScope() + ") to retrieve the actual User.";
                    throw new ForbiddenException(errorMessage);
                case SC_CONFLICT:
                    errorMessage = getErrorMessage(response, "Unable to retrieve the actual User.");
                    throw new ConflictException(errorMessage);
                default:
                    errorMessage = getErrorMessage(response,
                            String.format("Unable to setup connection (HTTP Status Code: %d)", httpStatus));
                    throw new ConnectionInitializationException(errorMessage);
                }
            }

            InputStream content = response.getEntity().getContent();
            BasicUser basicUser = new ObjectMapper().readValue(content, BasicUser.class);

            return basicUser;
        } catch (IOException | URISyntaxException e) {
            throw new ConnectionInitializationException("Unable to setup connection", e);
        }
    }

    /**
     * See {@link OsiamConnector#getCurrentUser(AccessToken)}
     */
    public User getCurrentUser(AccessToken accessToken) {
        BasicUser basicUser = getCurrentUserBasic(accessToken);
        return getResource(basicUser.getId(), accessToken);
    }

    protected HttpGet getMeWebResource() {
        HttpGet webResource;
        try {
            webResource = new HttpGet(new URI(getEndpoint() + "/me"));
            webResource.addHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());
        } catch (URISyntaxException e) {
            throw new ConnectionInitializationException("Unable to setup connection " + getEndpoint() +
                    "is not a valid URI.", e);
        }
        return webResource;
    }

    /**
     * See {@link OsiamConnector#getAllUsers(AccessToken)}
     */
    public List<User> getAllUsers(AccessToken accessToken) {
        return super.getAllResources(accessToken);
    }

    /**
     * See {@link OsiamConnector#searchUsers(String, AccessToken)}
     */
    public SCIMSearchResult<User> searchUsers(String queryString, AccessToken accessToken) {
        return super.searchResources(queryString, accessToken);
    }

    /**
     * See {@link OsiamConnector#searchUsers(Query, AccessToken)}
     */
    public SCIMSearchResult<User> searchUsers(Query query, AccessToken accessToken) {
        return super.searchResources(query, accessToken);
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
     * See {@link OsiamConnector#updateUser(String, User, AccessToken)}
     */
    public User updateUser(String uuid, User user, AccessToken accessToken) {
        if (user == null) {
            throw new IllegalArgumentException("The given User can't be null.");
        }
        if (uuid == null || uuid.isEmpty()) {
            throw new IllegalArgumentException("The given User ID can't be null or empty.");
        }
        return updateResource(uuid, user, accessToken);

    }

    /**
     * See {@link OsiamConnector#replaceUser(User, AccessToken)}
     */
    public User replaceUser(User user, AccessToken accessToken) {
        if (user == null) {
            throw new IllegalArgumentException("The given User can't be null.");
        }
        if (user.getId() == null || user.getId().isEmpty()) {
            throw new IllegalArgumentException("The given User ID can't be null or empty.");
        }
        return replaceResource(user.getId(), user, accessToken);
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
