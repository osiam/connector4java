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

import java.net.URI;
import java.util.List;

import org.osiam.client.exception.ConflictException; // NOSONAR : needed for Javadoc
import org.osiam.client.exception.ConnectionInitializationException; // NOSONAR : needed for Javadoc
import org.osiam.client.exception.ForbiddenException; // NOSONAR : needed for Javadoc
import org.osiam.client.exception.InvalidAttributeException;
import org.osiam.client.exception.NoResultException; // NOSONAR : needed for Javadoc
import org.osiam.client.exception.UnauthorizedException; // NOSONAR : needed for Javadoc
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryBuilder;
import org.osiam.client.user.BasicUser;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.UpdateGroup;
import org.osiam.resources.scim.UpdateUser;
import org.osiam.resources.scim.User;

/**
 * OsiamConnector provides access to the OAuth2 service used to authorize requests and all methods necessary to
 * manipulate the {@link Group} and {@link User} resources registered in the given OSIAM installation. For the
 * construction of an instance please use the {@link OsiamConnector.Builder}
 */
public class OsiamConnector {// NOSONAR - Builder constructs instances of this class

    private String clientId;
    private String clientSecret;
    private String genEndpoint;
    private String authServiceEndpoint;
    private String resourceServiceEndpoint;
    private String clientRedirectUri;

    private AuthService authService;
    private OsiamUserService userService;
    private OsiamGroupService groupService;

    /**
     * The private constructor for the OsiamConnector. Please use the {@link OsiamConnector.Builder} to construct one.
     *
     * @param builder
     *            a valid Builder that holds all needed variables
     */
    private OsiamConnector(Builder builder) {
        clientId = builder.clientId;
        clientSecret = builder.clientSecret;
        authServiceEndpoint = builder.authServiceEndpoint;
        resourceServiceEndpoint = builder.resourceServiceEndpoint;
        genEndpoint = builder.genEndpoint;
        clientRedirectUri = builder.clientRedirectUri;
    }

    /**
     *
     * @return a valid AuthService build out of the provided variables
     */
    private AuthService authService() {// NOSONAR - its ok if the Cyclomatic Complexity is > 10
        if (authService == null) {
            AuthService.Builder builder = new AuthService.Builder(getAuthServiceEndPoint());

            if (clientId != null) {
                builder = builder.setClientId(clientId);
            }
            if (clientSecret != null) {
                builder = builder.setClientSecret(clientSecret);
            }
            if (clientRedirectUri != null) {
                builder = builder.setClientRedirectUri(clientRedirectUri);
            }
            authService = builder.build();
        }
        return authService;
    }

    private String getAuthServiceEndPoint() {
        if (!(authServiceEndpoint == null || authServiceEndpoint.isEmpty())) {
            return authServiceEndpoint;
        }
        if (!(genEndpoint == null || genEndpoint.isEmpty())) {
            StringBuilder endpoint = new StringBuilder(genEndpoint);
            if (!genEndpoint.endsWith("/")) {
                endpoint.append("/");
            }
            endpoint.append("osiam-auth-server/");
            return endpoint.toString();
        }
        throw new InvalidAttributeException("No endpoint to the OSIAM server has been set");
    }

    private String getResourceServiceEndPoint() {
        if (!(resourceServiceEndpoint == null || resourceServiceEndpoint.isEmpty())) {
            return resourceServiceEndpoint;
        }
        if (!(genEndpoint == null || genEndpoint.isEmpty())) {
            StringBuilder endpoint = new StringBuilder(genEndpoint);
            if (!genEndpoint.endsWith("/")) {
                endpoint.append("/");
            }
            endpoint.append("osiam-resource-server");
            return endpoint.toString();
        }
        throw new InvalidAttributeException("No endpoint to the OSIAM server has been set");
    }

    /**
     *
     * @return a valid OsiamUserService build out of the provided variables
     */
    private OsiamUserService userService() {
        if (userService == null) {
            userService = new OsiamUserService.Builder(getResourceServiceEndPoint()).build();
        }
        return userService;
    }

    /**
     *
     * @return a valid OsiamGroupService build out of the provided variables
     */
    private OsiamGroupService groupService() {
        if (groupService == null) {
            groupService = new OsiamGroupService.Builder(getResourceServiceEndPoint()).build();
        }
        return groupService;
    }

    /**
     * Retrieve a single User with the given id. If no user for the given id can be found a {@link NoResultException} is
     * thrown.
     *
     * @param id
     *            the id of the wanted user
     * @param accessToken
     *            the OSIAM access token from for the current session
     * @return the user with the given id
     * @throws UnauthorizedException
     *             if the request could not be authorized.
     * @throws NoResultException
     *             if no user with the given id can be found
     * @throws ForbiddenException
     *             if the scope doesn't allow this request
     * @throws ConnectionInitializationException
     *             if the connection to the given OSIAM service could not be initialized
     */
    public User getUser(String id, AccessToken accessToken) {
        return userService().getUser(id, accessToken);
    }

    /**
     * Retrieve a list of the of all {@link User} resources saved in the OSIAM service. If you need to have all User but
     * the number is very big, this method can be slow. In this case you can also use Query.Builder with no filter to
     * split the number of User returned
     *
     * @param accessToken
     *            A valid AccessToken
     * @return a list of all Users
     * @throws UnauthorizedException
     *             if the request could not be authorized.
     * @throws ForbiddenException
     *             if the scope doesn't allow this request
     * @throws ConnectionInitializationException
     *             if the connection to the given OSIAM service could not be initialized
     */
    public List<User> getAllUsers(AccessToken accessToken) {
        return userService().getAllUsers(accessToken);
    }

    /**
     * Search for existing Users by the given {@link org.osiam.client.query.Query Query}.
     *
     * @param query
     *        containing the query to execute.
     * @param accessToken
     *        the OSIAM access token from for the current session
     * @return a SCIMSearchResult Containing a list of all found Users
     * @throws UnauthorizedException
     *         if the request could not be authorized.
     * @throws ForbiddenException
     *         if the scope doesn't allow this request
     * @throws ConnectionInitializationException
     *         if the connection to the given OSIAM service could not be initialized
     */
    public SCIMSearchResult<User> searchUsers(Query query, AccessToken accessToken) {
        return userService().searchResources(query, accessToken);
    }

    /**
     * Retrieves the User who holds the given access token. Not to be used for the grant Client-Credentials If only the
     * basic Data like the userName, Name, primary email address is needed use the methode getCurrentUserBasic(...)
     * since it is more performant as this one
     *
     * @param accessToken
     *            the OSIAM access token from for the current session
     * @return the actual logged in user
     * @throws UnauthorizedException
     *             if the request could not be authorized.
     * @throws ForbiddenException
     *             if the scope doesn't allow this request
     * @throws ConnectionInitializationException
     *             if no connection to the given OSIAM services could be initialized
     */
    public User getCurrentUser(AccessToken accessToken) {
        return userService().getCurrentUser(accessToken);
    }

    /**
     * Retrieves the basic User data as BasicUser Object from the User who holds the given access token. Not to be used
     * for the grant Client-Credentials If only the basic Data like the userName, Name, primary email address is needed
     * use this methode since it is more performant as the getCurrentUser(...) method
     *
     * @param accessToken
     *            the OSIAM access token from for the current session
     * @return the actual logged in user
     * @throws UnauthorizedException
     *             if the request could not be authorized.
     * @throws ForbiddenException
     *             if the scope doesn't allow this request
     * @throws ConnectionInitializationException
     *             if no connection to the given OSIAM services could be initialized
     */
    public BasicUser getCurrentUserBasic(AccessToken accessToken) {
        return userService().getCurrentUserBasic(accessToken);
    }

    /**
     * Retrieve a single Group with the given id. If no group with the given id can be found a {@link NoResultException}
     * is thrown.
     *
     * @param id
     *            the id of the wanted group
     * @param accessToken
     *            the access token from OSIAM for the current session.
     * @return the group with the given id.
     * @throws UnauthorizedException
     *             if the request could not be authorized.
     * @throws NoResultException
     *             if no user with the given id can be found
     * @throws ForbiddenException
     *             if the scope doesn't allow this request
     * @throws ConnectionInitializationException
     *             if the connection to the given OSIAM service could not be initialized
     */
    public Group getGroup(String id, AccessToken accessToken) {
        return groupService().getGroup(id, accessToken);
    }

    /**
     * Retrieve a list of the of all {@link Group} resources saved in the OSIAM service. If you need to have all Group
     * but the number is very big, this method can be slow. In this case you can also use Query.Builder with no filter
     * to split the number of Groups returned
     *
     * @param accessToken
     *            the OSIAM access token for the current session
     * @return a list of all groups
     * @throws UnauthorizedException
     *             if the request could not be authorized.
     * @throws ForbiddenException
     *             if the scope doesn't allow this request
     * @throws ConnectionInitializationException
     *             if the connection to the given OSIAM service could not be initialized
     */
    public List<Group> getAllGroups(AccessToken accessToken) {
        return groupService().getAllGroups(accessToken);
    }

    /**
     * Search for existing groups by a given {@link org.osiam.client.query.Query Query}. For more detailed information
     * about the possible logical operators and usable fields please have a look into the wiki.
     *
     * @param query
     *        containing the needed search where statement
     * @param accessToken
     *        the OSIAM access token from for the current session
     * @return a SCIMSearchResult containing a list of all found Groups
     * @throws UnauthorizedException
     *         if the request could not be authorized.
     * @throws ForbiddenException
     *         if the scope doesn't allow this request
     * @throws ConnectionInitializationException
     *         if the connection to the given OSIAM service could not be initialized
     * @see <a
     *      href="https://github.com/osiam/connector4java/wiki/Working-with-groups#search-for-groups">https://github.com/osiam/connector4java/wiki/Working-with-groups#search-for-groups</a>
     */
    public SCIMSearchResult<Group> searchGroups(Query query, AccessToken accessToken) {
        return groupService().searchGroups(query, accessToken);
    }

    /**
     * Provides a new and refreshed access token by getting the refresh token from the given access token.
     *
     * @param accessToken
     *            the access token to be refreshed
     * @param scopes
     *            an optional parameter if the scope of the token should be changed. Otherwise the scopes of the old
     *            token are used.
     * @return the new access token with the refreshed lifetime
     * @throws IllegalArgumentException
     *         in case the accessToken has an empty refresh token
     * @throws ConnectionInitializationException
     *         if the connection to the given OSIAM service could not be initialized
     * @throws UnauthorizedException
     *         if the request could not be authorized.
     */
    public AccessToken refreshAccessToken(AccessToken accessToken, Scope... scopes) {
        return authService.refreshAccessToken(accessToken, scopes);
    }

    /**
     * provides the needed URI which is needed to reconnect the User to the OSIAM server to login. A detailed example
     * how to use this method, can be seen in our wiki in gitHub
     * 
     * @param scopes
     *        the wanted scopes for the user who want's to log in with the oauth workflow
     * @return the needed redirect Uri
     * @see <a
     *      href="https://github.com/osiam/connector4java/wiki/Login-and-getting-an-access-token#grant-authorization-code">https://github.com/osiam/connector4java/wiki/Login-and-getting-an-access-token#grant-authorization-code</a>
     */
    public URI getRedirectLoginUri(Scope... scopes) {
        return authService().getRedirectLoginUri(scopes);
    }

    /**
     * Provide an {@link AccessToken} for the {@link GrantType} CLIENT_CREDENTIALS.
     * 
     * @param scopes
     *        the wanted Scopes of the {@link AccessToken}
     * @return an valid {@link AccessToken}
     */
    public AccessToken retrieveAccessToken(Scope... scopes) {
        return authService().retrieveAccessToken(scopes);
    }

    /**
     * Provide an {@link AccessToken} for the {@link GrantType} RESOURCE_OWNER_PASSWORD_CREDENTIALS.
     * 
     * @param userName
     *        the userName of the actual User
     * @param password
     *        the password of the actual User
     * @param scopes
     *        the wanted Scopes of the {@link AccessToken}
     * @return an valid {@link AccessToken}
     */
    public AccessToken retrieveAccessToken(String userName, String password, Scope... scopes) {
        return authService().retrieveAccessToken(userName, password, scopes);
    }

    /**
     * Provide an {@link AccessToken} for the {@link GrantType} AUTHORIZATION_CODE (oauth2 login).
     * 
     * @param authCode
     *            authentication code retrieved from the OSIAM Server by using the oauth2 login flow. For more
     *            information please look at the wiki at github
     * @return a valid AccessToken
     * @throws ConflictException
     *             in case the given authCode could not be exchanged against a access token
     * @throws ConnectionInitializationException
     *             If the Service is unable to connect to the configured OAuth2 service.
     * @see <a
     *      href="https://github.com/osiam/connector4java/wiki/Login-and-getting-an-access-token#grant-authorization-code">https://github.com/osiam/connector4java/wiki/Login-and-getting-an-access-token#grant-authorization-code</a>
     */
    public AccessToken retrieveAccessToken(String authCode) {
        return authService().retrieveAccessToken(authCode);
    }

    /**
     * saves the given {@link User} to the OSIAM DB.
     *
     * @param user
     *            user to be saved
     * @param accessToken
     *            the OSIAM access token from for the current session
     * @return the same user Object like the given but with filled metadata and a new valid id
     * @throws UnauthorizedException
     *             if the request could not be authorized.
     * @throws ConflictException
     *             if the User could not be created
     * @throws ForbiddenException
     *             if the scope doesn't allow this request
     * @throws ConnectionInitializationException
     *             if the connection to the given OSIAM service could not be initialized
     */
    public User createUser(User user, AccessToken accessToken) {
        return userService().createUser(user, accessToken);
    }

    /**
     * saves the given {@link Group} to the OSIAM DB.
     *
     * @param group
     *            group to be saved
     * @param accessToken
     *            the OSIAM access token from for the current session
     * @return the same group Object like the given but with filled metadata and a new valid id
     * @throws UnauthorizedException
     *             if the request could not be authorized.
     * @throws ConflictException
     *             if the Group could not be created
     * @throws ForbiddenException
     *             if the scope doesn't allow this request
     * @throws ConnectionInitializationException
     *             if the connection to the given OSIAM service could not be initialized
     */
    public Group createGroup(Group group, AccessToken accessToken) {
        return groupService().createGroup(group, accessToken);
    }

    /**
     * delete the given {@link Group} at the OSIAM DB.
     *
     * @param id
     *            id of the Group to be deleted
     * @param accessToken
     *            the OSIAM access token from for the current session
     * @throws UnauthorizedException
     *             if the request could not be authorized.
     * @throws NoResultException
     *             if no group with the given id can be found
     * @throws ConflictException
     *             if the Group could not be deleted
     * @throws ForbiddenException
     *             if the scope doesn't allow this request
     * @throws ConnectionInitializationException
     *             if the connection to the given OSIAM service could not be initialized
     */
    public void deleteGroup(String id, AccessToken accessToken) {
        groupService().deleteGroup(id, accessToken);
    }

    /**
     * delete the given {@link User} at the OSIAM DB.
     *
     * @param id
     *            id of the User to be delete
     * @param accessToken
     *            the OSIAM access token from for the current session
     * @throws UnauthorizedException
     *             if the request could not be authorized.
     * @throws NoResultException
     *             if no user with the given id can be found
     * @throws ConflictException
     *             if the User could not be deleted
     * @throws ForbiddenException
     *             if the scope doesn't allow this request
     * @throws ConnectionInitializationException
     *             if the connection to the given OSIAM service could not be initialized
     */
    public void deleteUser(String id, AccessToken accessToken) {
        userService().deleteUser(id, accessToken);
    }

    /**
     * update the user of the given id with the values given in the User Object. For more detailed information how to
     * set new field, update Fields or to delete Fields please look in the wiki
     *
     * @param id
     *            if of the User to be updated
     * @param updateUser
     *            all Fields that need to be updated
     * @param accessToken
     *            the OSIAM access token from for the current session
     * @return the updated User Object with all new Fields
     * @see <a
     *      href="https://github.com/osiam/connector4java/wiki/Working-with-user">https://github.com/osiam/connector4java/wiki/Working-with-user</a>
     * @throws UnauthorizedException
     *             if the request could not be authorized.
     * @throws ConflictException
     *             if the User could not be updated
     * @throws NoResultException
     *             if no user with the given id can be found
     * @throws ForbiddenException
     *             if the scope doesn't allow this request
     * @throws ConnectionInitializationException
     *             if the connection to the given OSIAM service could not be initialized
     */
    public User updateUser(String id, UpdateUser updateUser, AccessToken accessToken) {
        return userService().updateUser(id, updateUser, accessToken);
    }

    /**
     * replaces the {@link User} with the given id with the given {@link User}
     * 
     * @param id
     *        The id of the User to be replaced
     * @param user
     *        The {@link User} who will repleace the old {@link User}
     * @param accessToken
     *        the OSIAM access token from for the current session
     * @return the replaced User
     * @throws InvalidAttributeException
     *         in case the id or the User is null or empty
     * @throws UnauthorizedException
     *         if the request could not be authorized.
     * @throws ConflictException
     *         if the User could not be replaced
     * @throws NoResultException
     *         if no user with the given id can be found
     * @throws ForbiddenException
     *         if the scope doesn't allow this request
     * @throws ConnectionInitializationException
     *         if the connection to the given OSIAM service could not be initialized
     */
    public User replaceUser(String id, User user, AccessToken accessToken) {
        return userService().replaceUser(id, user, accessToken);
    }

    /**
     * update the group of the given id with the values given in the Group Object. For more detailed information how to
     * set new field. Update Fields or to delete Fields please look in the wiki
     *
     * @param id
     *            id of the Group to be updated
     * @param updateGroup
     *            all Fields that need to be updated
     * @param accessToken
     *            the OSIAM access token from for the current session
     * @return the updated group Object
     * @see <a
     *      href="https://github.com/osiam/connector4java/wiki/Working-with-groups">https://github.com/osiam/connector4java/wiki/Working-with-groups</a>
     * @throws UnauthorizedException
     *             if the request could not be authorized.
     * @throws ConflictException
     *             if the Group could not be updated
     * @throws NoResultException
     *             if no group with the given id can be found
     * @throws ForbiddenException
     *             if the scope doesn't allow this request
     * @throws ConnectionInitializationException
     *             if the connection to the given OSIAM service could not be initialized
     */
    public Group updateGroup(String id, UpdateGroup updateGroup, AccessToken accessToken) {
        return groupService().updateGroup(id, updateGroup, accessToken);
    }

    /**
     * replaces the {@link Group} with the given id with the given {@link Group}
     * 
     * @param id
     *        The id of the Group to be replaced
     * @param user
     *        The {@link Group} who will repleace the old {@link Group}
     * @param accessToken
     *        the OSIAM access token from for the current session
     * @return the replaced User
     * @throws InvalidAttributeException
     *         in case the id or the Group is null or empty
     * @throws UnauthorizedException
     *         if the request could not be authorized.
     * @throws ConflictException
     *         if the Group could not be replaced
     * @throws NoResultException
     *         if no Group with the given id can be found
     * @throws ForbiddenException
     *         if the scope doesn't allow this request
     * @throws ConnectionInitializationException
     *         if the connection to the given OSIAM service could not be initialized
     */
    public Group replaceGroup(String id, Group group, AccessToken accessToken) {
        return groupService().updateGroup(id, group, accessToken);
    }

    /**
     * validates if the given token in the AccessToken is valid and not expired.
     *
     * @param tokenToValidate
     *            The AccessToken to be validated
     * @return The validated AccessToken if the AccessToken is valid
     * @throws UnauthorizedException
     *             if the request could not be authorized.
     * @throws AccessTokenValidationException
     *             if the tokenToValidate is not valid
     * @throws ConnectionInitializationException
     *             if the connection to the given OSIAM service could not be initialized
     */
    public AccessToken validateAccessToken(AccessToken tokenToValidate) {
        return authService().validateAccessToken(tokenToValidate);
    }

    /**
     * Creates a new {@link QueryBuilder}.
     */
    public QueryBuilder createQueryBuilder() {
        return new QueryBuilder();
    }

    /**
     * Creates a new {@link QueryBuilder} and copies the values of the given
     * {@link Query}.
     */
    public QueryBuilder createQueryBuilder(Query original) {
        return new QueryBuilder(original);
    }

    /**
     * The Builder class is used to construct instances of the {@link OsiamConnector}.
     */
    public static class Builder {

        private String clientId;
        private String clientSecret;
        private String genEndpoint;
        private String authServiceEndpoint;
        private String resourceServiceEndpoint;
        private String clientRedirectUri;

        /**
         * use the given basic endpoint for communication with the OAuth2-Service for authentication and the SCIM2
         * resource server. The schema will be <endpoint>/osiam-auth-server and <endpoint>/osiam-resource-server. This
         * method can be used if the authentification and the resource server are at the same location and have the
         * standard names.
         *
         * @param endpoint
         *            The endpoint to use for communication
         * @return The builder itself
         */
        public Builder setEndpoint(String endpoint) {
            genEndpoint = endpoint;
            return this;
        }

        /**
         * use the given endpoint for communication with the OAuth2-Service for authentication
         *
         * @param endpoint
         *            The AuthService endpoint to use for communication
         * @return The builder itself
         */
        public Builder setAuthServerEndpoint(String endpoint) {
            authServiceEndpoint = endpoint;
            return this;
        }

        /**
         * use the given endpoint for communication with the SCIM2 resource server.
         *
         * @param endpoint
         *            The resource service endpoint to use
         * @return The builder itself
         */
        public Builder setResourceServerEndpoint(String endpoint) {
            resourceServiceEndpoint = endpoint;
            return this;
        }

        /**
         * Add a ClientId to the OAuth2 request
         *
         * @param clientId
         *            The client-Id
         * @return The builder itself
         */
        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         * Add a clientSecret to the OAuth2 request
         *
         * @param clientSecret
         *            The client secret
         * @return The builder itself
         */
        public Builder setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        /**
         * Add a Client redirect URI to the OAuth2 request
         *
         * @param clientRedirectUri
         *            the clientRedirectUri which is known to the OSIAM server
         * @return The builder itself
         */
        public Builder setClientRedirectUri(String clientRedirectUri) {
            this.clientRedirectUri = clientRedirectUri;
            return this;
        }

        /**
         * Construct the {@link OsiamConnector} with the parameters passed to this builder.
         *
         * @return An OsiamConnector configured accordingly.
         * @throws ConnectionInitializationException
         *             If either the provided client credentials (clientId/clientSecret) or, if the requested grant type
         *             is 'password', the user credentials (userName/password) are incomplete.
         */
        public OsiamConnector build() {
            return new OsiamConnector(this);
        }

    }
}
