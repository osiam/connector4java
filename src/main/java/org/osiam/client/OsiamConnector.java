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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.RequestEntityProcessing;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.osiam.client.exception.*;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Client;
import org.osiam.client.oauth.Scope;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryBuilder;
import org.osiam.client.user.BasicUser;
import org.osiam.resources.scim.*;

import javax.ws.rs.client.ClientBuilder;
import java.net.URI;
import java.util.List;

/**
 * OsiamConnector provides access to the OAuth2 service used to authorize requests and all methods necessary to
 * manipulate the {@link Group} and {@link User} resources registered in the given OSIAM installation. For the
 * construction of an instance please use the {@link OsiamConnector.Builder}
 */
public class OsiamConnector {

    public static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int DEFAULT_CONNECT_TIMEOUT = 2500;
    private static final int DEFAULT_READ_TIMEOUT = 5000;
    private static final boolean DEFAULT_LEGACY_SCHEMAS = false;
    private static final int DEFAULT_MAX_CONNECTIONS = 40;
    private static final PoolingHttpClientConnectionManager connectionManager =
            new PoolingHttpClientConnectionManager();

    private static final javax.ws.rs.client.Client client = ClientBuilder.newClient(new ClientConfig()
            .connectorProvider(new ApacheConnectorProvider())
            .property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.BUFFERED)
            .property(ApacheClientProperties.CONNECTION_MANAGER, connectionManager)
            .register(HttpAuthenticationFeature.basicBuilder().build())
            .property(ClientProperties.CONNECT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT)
            .property(ClientProperties.READ_TIMEOUT, DEFAULT_READ_TIMEOUT));

    static {
        setMaxConnections(DEFAULT_MAX_CONNECTIONS);
    }

    private AuthService authService;
    private OsiamUserService userService;
    private OsiamGroupService groupService;

    /**
     * The private constructor for the OsiamConnector. Please use the {@link OsiamConnector.Builder}
     * to construct one.
     *
     * @param builder a valid {@link Builder} that holds all needed variables
     */
    private OsiamConnector(Builder builder) {
        String authEndpoint;
        String resourceEndpoint;
        Version version;
        if (!Strings.isNullOrEmpty(builder.endpoint)) {
            authEndpoint = builder.endpoint;
            resourceEndpoint = builder.endpoint;
            version = Version.OSIAM_3;
        } else {
            authEndpoint = builder.getAuthServiceEndpoint();
            resourceEndpoint = builder.getResourceServiceEndpoint();
            if (builder.legacySchemas) {
                version = Version.OSIAM_2_LEGACY_SCHEMAS;
            } else {
                version = Version.OSIAM_2;
            }
        }

        if (!Strings.isNullOrEmpty(authEndpoint)) {
            authService = new AuthService(authEndpoint, builder.clientId, builder.clientSecret,
                    builder.clientRedirectUri, builder.connectTimeout, builder.readTimeout);
        }

        if (!Strings.isNullOrEmpty(resourceEndpoint)) {
            userService = new OsiamUserService(resourceEndpoint, builder.connectTimeout, builder.readTimeout, version);
            groupService = new OsiamGroupService(resourceEndpoint, builder.connectTimeout, builder.readTimeout, version);
        }
    }

    static javax.ws.rs.client.Client getClient() {
        return client;
    }

    /**
     * Set the connect timeout interval, in milliseconds.
     * <p>
     * A value of zero (0) is equivalent to an interval of infinity. The default value is 2500. This property will be
     * set application global, so you can only define this timeout for all {@link org.osiam.client.OsiamConnector}
     * instances at the same time.
     * <p>
     *
     * @param connectTimeout the connect timeout interval, in milliseconds
     */
    public static void setConnectTimeout(int connectTimeout) {
        client.property(ClientProperties.CONNECT_TIMEOUT, connectTimeout);
    }

    /**
     * Set the read timeout interval, in milliseconds.
     * <p>
     * A value of zero (0) is equivalent to an interval of infinity. The default value is 5000. This property will be
     * set application global, so you can only define this timeout for all {@link org.osiam.client.OsiamConnector}
     * instances at the same time.
     * <p>
     *
     * @param readTimeout the read timeout interval, in milliseconds
     */
    public static void setReadTimeout(int readTimeout) {
        client.property(ClientProperties.READ_TIMEOUT, readTimeout);
    }

    /**
     * Sets the maximum number of connections that the underlying HTTP connection pool will
     * use to connect to OSIAM.
     * <p>
     * The default value is 40. This property will be set application global, so you can only
     * define it for all {@link org.osiam.client.OsiamConnector} instances at the same time.
     * <p>
     * See {@link OsiamConnector#setMaxConnectionsPerRoute(int)} if you use OSIAM 2.x and
     * installed auth-server and resource-server under different domains.
     *
     * @param maxConnections The maximum number of HTTP connections
     */
    public static void setMaxConnections(int maxConnections) {
        connectionManager.setMaxTotal(maxConnections);
        connectionManager.setDefaultMaxPerRoute(maxConnections);
    }

    /**
     * Sets the maximum number of connections that the underlying HTTP connection pool will
     * allocate for single route.
     * <p>
     * This setting should only be used, if you use OSIAM 2.x and installed auth-server and
     * resource-server under different domains. In this case you have 2 distinct routes to OSIAM.
     * <p>
     * A single route means a single FQDN, hostname or IP address. In the context of OSIAM 2.x
     * this means the OSIAM server or the auth- or resource-server if they will be accessed under a
     * different hostname. Remember to also set the number of maximum connections via
     * {@link OsiamConnector#setMaxConnections(int)} based on the value set here, e.g. if you have 2
     * separate endpoints, in sense of the hostname, for auth- and resource-server and set this
     * value to {@code 20} you should set the maximum number of connections to {@code 40}. Remember
     * to set maximum connections first and maximum connections per route afterwards, because
     * {@link OsiamConnector#setMaxConnections(int)} also sets the maximum conenctions per route to
     * the given value.
     * <p>
     * The default value is 40. This property will be set application global, so
     * you can only define this timeout for all {@link org.osiam.client.OsiamConnector} instances at
     * the same time.
     *
     * @param maxConnectionsPerRoute The maximum number of HTTP connections per route
     */
    public static void setMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
        connectionManager.setDefaultMaxPerRoute(maxConnectionsPerRoute);
    }

    private AuthService getAuthService() {
        if (authService == null) {
            throw new IllegalStateException("OSIAM's endpoint(s) are not properly configured.");
        }
        return authService;
    }

    private OsiamUserService getUserService() {
        if (userService == null) {
            throw new IllegalStateException("OSIAM's endpoint(s) are not properly configured.");
        }
        return userService;
    }

    private OsiamGroupService getGroupService() {
        if (groupService == null) {
            throw new IllegalStateException("OSIAM's endpoint(s) are not properly configured.");
        }
        return groupService;
    }

    /**
     * Retrieve a single User with the given id. If no user for the given id can be found a {@link NoResultException} is
     * thrown.
     *
     * @param id          the id of the wanted user
     * @param accessToken the OSIAM access token from for the current session
     * @param attributes  the attributes that should be returned in the response. If none are given, all are returned
     * @return the user with the given id
     * @throws UnauthorizedException             if the request could not be authorized.
     * @throws NoResultException                 if no user with the given id can be found
     * @throws ForbiddenException                if the scope doesn't allow this request
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     */
    public User getUser(String id, AccessToken accessToken, String... attributes) {
        return getUserService().getUser(id, accessToken, attributes);
    }

    /**
     * Retrieve a list of the of all {@link User} resources saved in the OSIAM service. If you need to have all User but
     * the number is very large, this method can be slow. In this case you can also use Query.Builder with no filter to
     * split the number of User returned
     *
     * @param accessToken A valid AccessToken
     * @param attributes  the list of attributes that should be returned in the response
     * @return a list of all Users
     * @throws UnauthorizedException             if the request could not be authorized.
     * @throws ForbiddenException                if the scope doesn't allow this request
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     */
    public List<User> getAllUsers(AccessToken accessToken, String... attributes) {
        return getUserService().getAllUsers(accessToken, attributes);
    }

    /**
     * Search for existing Users by the given {@link org.osiam.client.query.Query Query}.
     *
     * @param query       containing the query to execute.
     * @param accessToken the OSIAM access token from for the current session
     * @return a SCIMSearchResult Containing a list of all found Users
     * @throws UnauthorizedException             if the request could not be authorized.
     * @throws ForbiddenException                if the scope doesn't allow this request
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     */
    public SCIMSearchResult<User> searchUsers(Query query, AccessToken accessToken) {
        return getUserService().searchResources(query, accessToken);
    }

    /**
     * Retrieves the User holding the given access token. Not to be used for the grant Client-Credentials. If the
     * version of OSIAM to be queried is lower than 3.0 and only the basic Data like the userName, Name, primary
     * e-mail address is needed, consider using the method getCurrentUserBasic(...) since it is more performant than
     * this one. Be aware, however, that that method is deprecated and going to go away with version 2.0 of the
     * connector. This method is not compatible with OSIAM 3.x.
     *
     * @param accessToken the OSIAM access token from for the current session
     * @return the actual logged in user
     * @throws UnauthorizedException             if the request could not be authorized.
     * @throws ForbiddenException                if the scope doesn't allow this request
     * @throws ConnectionInitializationException if no connection to the given OSIAM services could be initialized
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     * @deprecated Use {@link #getMe(AccessToken, String...)}. This method is going to go away with version 1.12 or 2.0.
     */
    @Deprecated
    public User getCurrentUser(AccessToken accessToken) {
        return getUserService().getCurrentUser(accessToken);
    }

    /**
     * Retrieves the User holding the given access token.
     *
     * @param accessToken the OSIAM access token from for the current session
     * @param attributes  the attributes that should be returned in the response. If none are given, all are returned
     * @return the actual logged in user
     * @throws UnauthorizedException             if the request could not be authorized.
     * @throws ForbiddenException                if the scope doesn't allow this request
     * @throws ConnectionInitializationException if no connection to the given OSIAM services could be initialized
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     */
    public User getMe(AccessToken accessToken, String... attributes) {
        return getUserService().getMe(accessToken, attributes);
    }

    /**
     * Retrieves the basic User data as BasicUser Object from the User who holds the given access token. Not to be used
     * for the grant Client-Credentials If only the basic Data like the userName, Name, primary email address is needed
     * use this method since it is more performant as the getCurrentUser(...) method. This method is not compatible with
     * OSIAM 3.x.
     *
     * @param accessToken the OSIAM access token from for the current session
     * @return the actual logged in user
     * @throws UnauthorizedException             if the request could not be authorized.
     * @throws ForbiddenException                if the scope doesn't allow this request
     * @throws ConnectionInitializationException if no connection to the given OSIAM services could be initialized
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     * @deprecated The BasicUser class has been deprecated. Use {@link #getMe(AccessToken)} with OSIAM 3.x. This method
     * is going to go away with version 1.12 or 2.0.
     */
    @Deprecated
    public BasicUser getCurrentUserBasic(AccessToken accessToken) {
        return getUserService().getCurrentUserBasic(accessToken);
    }

    /**
     * Retrieve a single Group with the given id. If no group with the given id can be found a {@link NoResultException}
     * is thrown.
     *
     * @param id          the id of the wanted group
     * @param accessToken the access token from OSIAM for the current session.
     * @param attributes  the attributes that should be returned in the response. If none are given, all are returned
     * @return the group with the given id.
     * @throws UnauthorizedException             if the request could not be authorized.
     * @throws NoResultException                 if no user with the given id can be found
     * @throws ForbiddenException                if the scope doesn't allow this request
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     */
    public Group getGroup(String id, AccessToken accessToken, String... attributes) {
        return getGroupService().getGroup(id, accessToken, attributes);
    }

    /**
     * Retrieve a list of the of all {@link Group} resources saved in the OSIAM service. If you need to have all Group
     * but the number is very large, this method can be slow. In this case you can also use Query.Builder with no filter
     * to split the number of Groups returned
     *
     * @param accessToken the OSIAM access token for the current session
     * @param attributes  the list of attributes that should be returned in the response
     * @return a list of all groups
     * @throws UnauthorizedException             if the request could not be authorized.
     * @throws ForbiddenException                if the scope doesn't allow this request
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     */
    public List<Group> getAllGroups(AccessToken accessToken, String... attributes) {
        return getGroupService().getAllGroups(accessToken);
    }

    /**
     * Search for existing groups by a given {@link org.osiam.client.query.Query Query}. For more detailed information
     * about the possible logical operators and usable fields please have a look into the documentation.
     *
     * @param query       containing the needed search where statement
     * @param accessToken the OSIAM access token from for the current session
     * @return a SCIMSearchResult containing a list of all found Groups
     * @throws UnauthorizedException             if the request could not be authorized.
     * @throws ForbiddenException                if the scope doesn't allow this request
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     * @see <a href="https://github.com/osiam/connector4java/docs/working-with-groups
     * .md#search-for-groups#search-for-groups">Search for groups</a>
     */
    public SCIMSearchResult<Group> searchGroups(Query query, AccessToken accessToken) {
        return getGroupService().searchGroups(query, accessToken);
    }

    /**
     * Provides a new and refreshed access token by getting the refresh token from the given access token.
     *
     * @param accessToken the access token to be refreshed
     * @param scopes      an optional parameter if the scope of the token should be changed. Otherwise the scopes of the
     *                    old token are used.
     * @return the new access token with the refreshed lifetime
     * @throws IllegalArgumentException          in case the accessToken has an empty refresh token
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws UnauthorizedException             if the request could not be authorized.
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     */
    public AccessToken refreshAccessToken(AccessToken accessToken, Scope... scopes) {
        return getAuthService().refreshAccessToken(accessToken, scopes);
    }

    /**
     * provides the needed URI which is needed to reconnect the User to OSIAM to login. A detailed example
     * how to use this method, can be seen in the documentation
     *
     * @param scopes the wanted scopes for the user who want's to log in with the oauth workflow
     * @return the needed redirect Uri
     * @throws IllegalStateException if OSIAM's endpoint(s) are not properly configured
     * @see <a href="https://github.com/osiam/connector4java/docs/login-and-getting-an-access-token
     * .md#grant-authorization-code">Grant authorization code</a>
     */
    public URI getAuthorizationUri(Scope... scopes) {
        return getAuthService().getAuthorizationUri(scopes);
    }

    /**
     * Provide an {@link AccessToken} for the {@link org.osiam.client.oauth.GrantType} CLIENT_CREDENTIALS.
     *
     * @param scopes the wanted Scopes of the {@link AccessToken}
     * @return an valid {@link AccessToken}
     * @throws IllegalStateException if OSIAM's endpoint(s) are not properly configured
     */
    public AccessToken retrieveAccessToken(Scope... scopes) {
        return getAuthService().retrieveAccessToken(scopes);
    }

    /**
     * Provide an {@link AccessToken} for the {@link org.osiam.client.oauth.GrantType}
     * RESOURCE_OWNER_PASSWORD_CREDENTIALS.
     *
     * @param userName the userName of the actual User
     * @param password the password of the actual User
     * @param scopes   the wanted Scopes of the {@link AccessToken}
     * @return an valid {@link AccessToken}
     * @throws IllegalStateException if OSIAM's endpoint(s) are not properly configured
     */
    public AccessToken retrieveAccessToken(String userName, String password, Scope... scopes) {
        return getAuthService().retrieveAccessToken(userName, password, scopes);
    }

    /**
     * Provide an {@link AccessToken} for the {@link org.osiam.client.oauth.GrantType} AUTHORIZATION_CODE (oauth2
     * login).
     *
     * @param authCode authentication code retrieved from OSIAM by using the oauth2 login flow. For more
     *                 information please check the documentation
     * @return a valid AccessToken
     * @throws ConflictException                 in case the given authCode could not be exchanged against a access
     *                                           token
     * @throws ConnectionInitializationException If the Service is unable to connect to the configured OAuth2 service.
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     * @see <a href="https://github.com/osiam/connector4java/docs/login-and-getting-an-access-token
     * .md#grant-authorization-code">Grant authorization code</a>
     */
    public AccessToken retrieveAccessToken(String authCode) {
        return getAuthService().retrieveAccessToken(authCode);
    }

    /**
     * saves the given {@link User} to the OSIAM DB.
     *
     * @param user        user to be saved
     * @param accessToken the OSIAM access token from for the current session
     * @return the same user Object like the given but with filled metadata and a new valid id
     * @throws UnauthorizedException             if the request could not be authorized.
     * @throws ConflictException                 if the User could not be created
     * @throws ForbiddenException                if the scope doesn't allow this request
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     */
    public User createUser(User user, AccessToken accessToken) {
        return getUserService().createUser(user, accessToken);
    }

    /**
     * saves the given {@link Group} to the OSIAM DB.
     *
     * @param group       group to be saved
     * @param accessToken the OSIAM access token from for the current session
     * @return the same group Object like the given but with filled metadata and a new valid id
     * @throws UnauthorizedException             if the request could not be authorized.
     * @throws ConflictException                 if the Group could not be created
     * @throws ForbiddenException                if the scope doesn't allow this request
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     */
    public Group createGroup(Group group, AccessToken accessToken) {
        return getGroupService().createGroup(group, accessToken);
    }

    /**
     * delete the given {@link Group} at the OSIAM DB.
     *
     * @param id          id of the Group to be deleted
     * @param accessToken the OSIAM access token from for the current session
     * @throws UnauthorizedException             if the request could not be authorized.
     * @throws NoResultException                 if no group with the given id can be found
     * @throws ConflictException                 if the Group could not be deleted
     * @throws ForbiddenException                if the scope doesn't allow this request
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     */
    public void deleteGroup(String id, AccessToken accessToken) {
        getGroupService().deleteGroup(id, accessToken);
    }

    /**
     * delete the given {@link User} at the OSIAM DB.
     *
     * @param id          id of the User to be delete
     * @param accessToken the OSIAM access token from for the current session
     * @throws UnauthorizedException             if the request could not be authorized.
     * @throws NoResultException                 if no user with the given id can be found
     * @throws ConflictException                 if the User could not be deleted
     * @throws ForbiddenException                if the scope doesn't allow this request
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     */
    public void deleteUser(String id, AccessToken accessToken) {
        getUserService().deleteUser(id, accessToken);
    }

    /**
     * update the user of the given id with the values given in the User Object. For more detailed information how to
     * set new field, update Fields or to delete Fields please look in the documentation. This method is not compatible
     * with OSIAM 3.x.
     *
     * @param id          if of the User to be updated
     * @param updateUser  all Fields that need to be updated
     * @param accessToken the OSIAM access token from for the current session
     * @return the updated User Object with all new Fields
     * @throws UnauthorizedException             if the request could not be authorized.
     * @throws ConflictException                 if the User could not be updated
     * @throws NoResultException                 if no user with the given id can be found
     * @throws ForbiddenException                if the scope doesn't allow this request
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     * @see <a href="https://github.com/osiam/connector4java/docs/working-with-user.md">Working with user</a>
     * @deprecated Updating with PATCH has been removed in OSIAM 3.0. This method is going to go away with version 1.12 or 2.0.
     */
    @Deprecated
    public User updateUser(String id, UpdateUser updateUser, AccessToken accessToken) {
        return getUserService().updateUser(id, updateUser, accessToken);
    }

    /**
     * replaces the {@link User} with the given id with the given {@link User}
     *
     * @param id          The id of the User to be replaced
     * @param user        The {@link User} who will repleace the old {@link User}
     * @param accessToken the OSIAM access token from for the current session
     * @return the replaced User
     * @throws InvalidAttributeException         in case the id or the User is null or empty
     * @throws UnauthorizedException             if the request could not be authorized.
     * @throws ConflictException                 if the User could not be replaced
     * @throws NoResultException                 if no user with the given id can be found
     * @throws ForbiddenException                if the scope doesn't allow this request
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     */
    public User replaceUser(String id, User user, AccessToken accessToken) {
        return getUserService().replaceUser(id, user, accessToken);
    }

    /**
     * update the group of the given id with the values given in the Group Object. For more detailed information how to
     * set new field. Update Fields or to delete Fields please look in the documentation
     *
     * @param id          id of the Group to be updated
     * @param updateGroup all Fields that need to be updated
     * @param accessToken the OSIAM access token from for the current session
     * @return the updated group Object
     * @throws UnauthorizedException             if the request could not be authorized.
     * @throws ConflictException                 if the Group could not be updated
     * @throws NoResultException                 if no group with the given id can be found
     * @throws ForbiddenException                if the scope doesn't allow this request
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     * @see <a href="https://github.com/osiam/connector4java/docs/working-with-groups
     * .md#search-for-groups">Working with groups</a>
     * @deprecated Updating with PATCH has been removed in OSIAM 3.0. This method is going to go away with version 1.12 or 2.0.
     */
    @Deprecated
    public Group updateGroup(String id, UpdateGroup updateGroup, AccessToken accessToken) {
        return getGroupService().updateGroup(id, updateGroup, accessToken);
    }

    /**
     * replaces the {@link Group} with the given id with the given {@link Group}
     *
     * @param id          The id of the Group to be replaced
     * @param group       The {@link Group} who will repleace the old {@link Group}
     * @param accessToken the OSIAM access token from for the current session
     * @return the replaced User
     * @throws InvalidAttributeException         in case the id or the Group is null or empty
     * @throws UnauthorizedException             if the request could not be authorized.
     * @throws ConflictException                 if the Group could not be replaced
     * @throws NoResultException                 if no Group with the given id can be found
     * @throws ForbiddenException                if the scope doesn't allow this request
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     */
    public Group replaceGroup(String id, Group group, AccessToken accessToken) {
        return getGroupService().replaceGroup(id, group, accessToken);
    }

    /**
     * validates if the given token in the AccessToken is valid and not expired.
     *
     * @param tokenToValidate The AccessToken to be validated
     * @return The validated AccessToken if the AccessToken is valid
     * @throws UnauthorizedException             if the tokenToValidate is not valid
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     */
    public AccessToken validateAccessToken(AccessToken tokenToValidate) {
        return getAuthService().validateAccessToken(tokenToValidate);
    }

    /**
     * Revokes the given access token if it is valid.
     *
     * @param tokenToRevoke the {@link AccessToken} to be revoked
     * @throws IllegalStateException if OSIAM's endpoint(s) are not properly configured
     */
    public void revokeAccessToken(AccessToken tokenToRevoke) {
        getAuthService().revokeAccessToken(tokenToRevoke);
    }

    /**
     * Revokes all access tokens of the user with the given ID.
     *
     * @param id          the user ID
     * @param accessToken the access token used to access the service
     * @throws IllegalStateException if OSIAM's endpoint(s) are not properly configured
     */
    public void revokeAllAccessTokens(String id, AccessToken accessToken) {
        getAuthService().revokeAllAccessTokens(id, accessToken);
    }

    /**
     * Get client by the given client id.
     *
     * @param clientId    the client id
     * @param accessToken the access token used to access the service
     * @return The found client
     * @throws UnauthorizedException             if the accessToken is not valid
     * @throws ForbiddenException                if access to this resource is not allowed
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws ClientNotFoundException           if no client with the given id can be found
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     */
    public Client getClient(String clientId, AccessToken accessToken) {
        return getAuthService().getClient(clientId, accessToken);
    }

    /**
     * Get all clients.
     *
     * @param accessToken the access token used to access the service
     * @return The found clients
     * @throws UnauthorizedException             if the accessToken is not valid
     * @throws ForbiddenException                if access to this resource is not allowed
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     */
    public List<Client> getClients(AccessToken accessToken) {
        return getAuthService().getClients(accessToken);
    }

    /**
     * Create a client.
     *
     * @param client      the client to create
     * @param accessToken the access token used to access the service
     * @return The created client
     * @throws UnauthorizedException             if the accessToken is not valid
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws ClientAlreadyExistsException      if the client with the clientId already exists
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     */
    public Client createClient(Client client, AccessToken accessToken) {
        return getAuthService().createClient(client, accessToken);
    }

    /**
     * Get OSIAM OAuth client by the given ID.
     *
     * @param clientId    the id of the client which should be deleted
     * @param accessToken the access token used to access the service
     * @throws UnauthorizedException             if the accessToken is not valid
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws ConflictException                 if the client with the clientId already exists
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     */
    public void deleteClient(String clientId, AccessToken accessToken) {
        getAuthService().deleteClient(clientId, accessToken);
    }

    /**
     * Get OSIAM OAuth client by the given ID.
     *
     * @param clientId    the id of the client which should be updated
     * @param client      the client
     * @param accessToken the access token used to access the service
     * @return The updated client
     * @throws UnauthorizedException             if the accessToken is not valid
     * @throws ConnectionInitializationException if the connection to the given OSIAM service could not be initialized
     * @throws ConflictException                 if the client with the clientId already exists
     * @throws IllegalStateException             if OSIAM's endpoint(s) are not properly configured
     */
    public Client updateClient(String clientId, Client client, AccessToken accessToken) {
        return getAuthService().updateClient(clientId, client, accessToken);
    }

    /**
     * Creates a new {@link QueryBuilder}.
     */
    public QueryBuilder createQueryBuilder() {
        return new QueryBuilder();
    }

    /**
     * Creates a new {@link QueryBuilder} and copies the values of the given {@link Query}.
     */
    public QueryBuilder createQueryBuilder(Query original) {
        return new QueryBuilder(original);
    }

    /**
     * The Builder class is used to construct instances of the {@link OsiamConnector}.
     */
    public static class Builder {

        private String endpoint;
        private String combinedEndpoint;
        private String authServiceEndpoint;
        private String resourceServiceEndpoint;
        private String clientId;
        private String clientSecret;
        private String clientRedirectUri;
        private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        private int readTimeout = DEFAULT_READ_TIMEOUT;
        private boolean legacySchemas = DEFAULT_LEGACY_SCHEMAS;

        /**
         * Use the given endpoint for communication with OSIAM.
         * <p>
         * <p/>Use this method with OSIAM 3.x.
         * <p>
         * <p/>For OSIAM 2.x: see {@link Builder#setEndpoint(String)},
         * {@link Builder#setAuthServerEndpoint(String)}, or
         * {@link Builder#setResourceServerEndpoint(String)}.
         *
         * @param endpoint The endpoint to use for communication
         * @return The builder itself
         */
        public Builder withEndpoint(String endpoint) {
            this.endpoint = endpoint;
            combinedEndpoint = null;
            authServiceEndpoint = null;
            resourceServiceEndpoint = null;
            return this;
        }

        /**
         * Use the given combined endpoint for communication with the OAuth2-Service for
         * authentication and the SCIM2 resource server.
         * <p>
         * <p/>Use this method with OSIAM 2.x.
         * <p>
         * <p/>The schema will be <endpoint>/osiam-auth-server and <endpoint>/osiam-resource-server.
         * This method can be used if the authentication and the resource server are at the same
         * location and have the standard names.
         * <p>
         * <p/>For OSIAM 3.x: see {@link Builder#withEndpoint(String)}.
         *
         * @param endpoint The endpoint to use for communication
         * @return The builder itself
         */
        public Builder setEndpoint(String endpoint) {
            combinedEndpoint = endpoint;
            this.endpoint = null;
            authServiceEndpoint = null;
            resourceServiceEndpoint = null;
            return this;
        }

        /**
         * Use the given endpoint for communication with the OAuth2-Service for
         * authentication
         * <p>
         * <p/>Use this method with OSIAM 2.x.
         * <p>
         * <p/>For OSIAM 3.x: see {@link Builder#withEndpoint(String)}.
         *
         * @param endpoint The AuthService endpoint to use for communication
         * @return The builder itself
         */
        public Builder setAuthServerEndpoint(String endpoint) {
            authServiceEndpoint = endpoint;
            this.endpoint = null;
            combinedEndpoint = null;
            return this;
        }

        /**
         * Use the given endpoint for communication with the SCIM2 resource server.
         * <p>
         * <p/>Use this method with OSIAM 2.x.
         * <p>
         * <p/>For OSIAM 3.x: see {@link Builder#withEndpoint(String)}.
         *
         * @param endpoint The resource service endpoint to use
         * @return The builder itself
         */
        public Builder setResourceServerEndpoint(String endpoint) {
            resourceServiceEndpoint = endpoint;
            this.endpoint = null;
            combinedEndpoint = null;
            return this;
        }


        /**
         * Add a ClientId to the OAuth2 request
         *
         * @param clientId The client-Id
         * @return The builder itself
         */
        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         * Add a clientSecret to the OAuth2 request
         *
         * @param clientSecret The client secret
         * @return The builder itself
         */
        public Builder setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        /**
         * Add a Client redirect URI to the OAuth2 request
         *
         * @param clientRedirectUri the clientRedirectUri which is known to OSIAM
         * @return The builder itself
         */
        public Builder setClientRedirectUri(String clientRedirectUri) {
            this.clientRedirectUri = clientRedirectUri;
            return this;
        }

        /**
         * Set the connect timeout per connector, in milliseconds.
         * <p>
         * <p>
         * A value of zero (0) is equivalent to an interval of infinity. The default value is 2500. This connect timeout
         * is set per connector and overrides the global set
         * {@link org.osiam.client.OsiamConnector#setConnectTimeout(int)}
         * <p>
         *
         * @param connectTimeout the connect timeout per request, in milliseconds
         * @return The builder itself
         */
        public Builder withConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        /**
         * Set the read timeout per connector, in milliseconds.
         * <p>
         * <p>
         * A value of zero (0) is equivalent to an interval of infinity. The default value is 5000. This read timeout is
         * set per connector and overrides the global set {@link org.osiam.client.OsiamConnector#setConnectTimeout(int)}
         * <p>
         *
         * @param readTimeout the read timeout per request, in milliseconds
         * @return The builder itself
         */
        public Builder withReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        /**
         * Configures the connector to use legacy schemas, i.e. schemas that were defined before
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
         * Construct the {@link OsiamConnector} with the parameters passed to this builder.
         *
         * @return An OsiamConnector configured accordingly.
         * @throws ConnectionInitializationException If either the provided client credentials
         *                                           (clientId/clientSecret) or, if the requested grant type is
         *                                           'password', the user credentials (userName/password) are
         *                                           incomplete.
         */
        public OsiamConnector build() {
            return new OsiamConnector(this);
        }

        private String getAuthServiceEndpoint() {
            if (!Strings.isNullOrEmpty(authServiceEndpoint)) {
                return authServiceEndpoint;
            }
            if (!Strings.isNullOrEmpty(combinedEndpoint)) {
                return appendSlash(combinedEndpoint) + "osiam-auth-server";
            }
            return null;
        }

        private String getResourceServiceEndpoint() {
            if (!Strings.isNullOrEmpty(resourceServiceEndpoint)) {
                return resourceServiceEndpoint;
            }
            if (!Strings.isNullOrEmpty(combinedEndpoint)) {
                return appendSlash(combinedEndpoint) + "osiam-resource-server";
            }
            return null;
        }

        private String appendSlash(String endpoint) {
            return !endpoint.endsWith("/") ? endpoint + "/" : endpoint;
        }
    }
}
