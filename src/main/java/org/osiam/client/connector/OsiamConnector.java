package org.osiam.client.connector;
/*
* for licensing see the file license.txt.
*/
import java.net.URI;
import java.util.List;

import org.apache.http.HttpResponse;
import org.osiam.client.OsiamGroupService;
import org.osiam.client.OsiamUserService;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.ForbiddenException;
import org.osiam.client.exception.InvalidAttributeException;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.AuthService;
import org.osiam.client.oauth.GrantType;
import org.osiam.client.oauth.Scope;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryResult;
import org.osiam.client.update.UpdateGroup;
import org.osiam.client.update.UpdateUser;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.User;

/**
 * OsiamConnector provides access to the OAuth2 service used to authorize requests
 * and all methods necessary to manipulate the {@link Group} and {@link User} resources registered in the
 * given OSIAM installation. For the construction of an instance please use the {@link OsiamConnector.Builder}
 */
public final class OsiamConnector {// NOSONAR - Builder constructs instances of this class

    private String clientId;
    private String clientSecret;
    private GrantType grantType;
    private String username;
    private String password;
    private String authServiceEndpoint;
    private String resourceServiceEndpoint;
    private Scope scope;
    private Scope[] scopes;
    private String stringScope;
    private String clientRedirectUri;

	private AuthService authService;
	private OsiamUserService userService;
    private OsiamGroupService groupService;

    /**
     * The private constructor for the OsiamConnector. Please use the {@link OsiamConnector.Builder}
     * to construct one.
     *
     * @param builder a valid Builder that holds all needed variables
     */
    private OsiamConnector(Builder builder) {
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.grantType = builder.grantType;
        this.username = builder.username;
        this.password = builder.password;
        this.authServiceEndpoint = builder.authServiceEndpoint;
        this.resourceServiceEndpoint = builder.resourceServiceEndpoint;
        this.scope = builder.scope;
        this.scopes = builder.scopes;
        this.stringScope = builder.stringScope;
        this.clientRedirectUri = builder.clientRedirectUri;
    }

    /**
     *
     * @return a valid AuthService build out of the provided variables
     */
    private AuthService authService(){// NOSONAR - its ok if the Cyclomatic Complexity is > 10
    	if(authService == null){    // NOSONAR - false-positive from clover; if-expression is correct
    		AuthService.Builder builder = new AuthService.Builder(authServiceEndpoint);

    		if(clientId != null){   // NOSONAR - false-positive from clover; if-expression is correct
    			builder = builder.setClientId(clientId);
    		}
    		if(clientSecret != null){   // NOSONAR - false-positive from clover; if-expression is correct
    			builder = builder.setClientSecret(clientSecret);
    		}
    		if(grantType != null){    // NOSONAR - false-positive from clover; if-expression is correct
    			builder = builder.setGrantType(grantType);
    		}
    		if(password != null){    // NOSONAR - false-positive from clover; if-expression is correct
    			builder = builder.setPassword(password);
    		}
    		if(username != null){    // NOSONAR - false-positive from clover; if-expression is correct
    			builder = builder.setUsername(username);
    		}
    		if(scope != null && scopes != null){// NOSONAR - false-positive from clover; if-expression is correct
    			builder = builder.setScope(scope, scopes);
    		}else if (scope != null){// NOSONAR - false-positive from clover; if-expression is correct
    			builder = builder.setScope(scope);
    		}
    		if(stringScope != null){// NOSONAR - false-positive from clover; if-expression is correct
    			builder = builder.setScope(stringScope);
    		}
    		if(clientRedirectUri != null){// NOSONAR - false-positive from clover; if-expression is correct
    			builder = builder.setClientRedirectUri(clientRedirectUri);
    		}
    		authService = builder.build();
    	}
    	return authService;
    }

    /**
     *
     * @return a valid OsiamUserService build out of the provided variables
     */
    private OsiamUserService userService(){
    	if(userService == null){     // NOSONAR - false-positive from clover; if-expression is correct
    		userService = new OsiamUserService.Builder(resourceServiceEndpoint).build();
    	}
    	return userService;
    }

    /**
     *
     * @return a valid OsiamGroupService build out of the provided variables
     */
    private OsiamGroupService groupService(){
        if(groupService == null){    // NOSONAR - false-positive from clover; if-expression is correct
            groupService = new OsiamGroupService.Builder(resourceServiceEndpoint).build();
        }
        return groupService;
    }

    /**
     * Retrieve a single User with the given id. If no user for the given id can be found a {@link NoResultException}
     * is thrown.
     *
     * @param id          the id of the wanted user
     * @param accessToken the OSIAM access token from for the current session
     * @return the user with the given id
     * @throws org.osiam.client.exception.UnauthorizedException if the request could not be authorized.
     * @throws org.osiam.client.exception.NoResultException     if no user with the given id can be found
     * @throws org.osiam.client.exception.ForbiddenException    if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               if the connection to the given OSIAM service could not be initialized
     */
    public User getUser(String id, AccessToken accessToken) {
        return userService().getUser(id, accessToken);
    }

    /**
     * Retrieve a list of the of all {@link User} resources saved in the OSIAM service.
     * If you need to have all User but the number is very big, this method can be slow.
     * In this case you can also use Query.Builder with no filter to split the number of User returned
     *
     * @param accessToken A valid AccessToken
     * @return a list of all Users
     * @throws org.osiam.client.exception.UnauthorizedException if the request could not be authorized.
     * @throws org.osiam.client.exception.ForbiddenException    if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               if the connection to the given OSIAM service could not be initialized
     */
    public List<User> getAllUsers(AccessToken accessToken) {
        return userService().getAllUsers(accessToken);
    }

    /**
     * Search for the existing Users by a given search string. For more detailed information about the possible
     * logical operators and usable fields please have a look into the wiki.<p>
     * <b>Note:</b> The query string should be URL encoded!
     *
     * @param queryString The URL encoded string with the query that should be passed to the OSIAM service
     * @param accessToken the OSIAM access token from for the current session
     * @return a QueryResult Containing a list of all found Users
     * @throws org.osiam.client.exception.UnauthorizedException if the request could not be authorized.
     * @throws org.osiam.client.exception.ForbiddenException    if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               if the connection to the given OSIAM service could not be initialized
     * @see <a href="https://github.com/osiam/connector4java/wiki/Working-with-user#search-for-user">https://github.com/osiam/connector4java/wiki/Working-with-user#search-for-user</a>
     */
    public QueryResult<User> searchUsers(String queryString, AccessToken accessToken) {
        return userService().searchUsers(queryString, accessToken);
    }

    /**
     * Search for existing Users by the given {@link Query}.
     *
     * @param query       containing the query to execute.
     * @param accessToken the OSIAM access token from for the current session
     * @return a QueryResult Containing a list of all found Users
     * @throws org.osiam.client.exception.UnauthorizedException if the request could not be authorized.
     * @throws org.osiam.client.exception.ForbiddenException    if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               if the connection to the given OSIAM service could not be initialized
     */
    public QueryResult<User> searchUsers(Query query, AccessToken accessToken) {
        return userService().searchUsers(query, accessToken);
    }

    /**
     * Retrieve the User who holds the given access token.
     * Not to be used for the grant Client-Credentials
     * @param accessToken the OSIAM access token from for the current session
     * @return the actual logged in user
     * @throws UnauthorizedException if the request could not be authorized.
     * @throws org.osiam.client.exception.ForbiddenException    if the scope doesn't allow this request
     * @throws ConnectionInitializationException
     *                               if no connection to the given OSIAM services could be initialized
     */
    public User getMe(AccessToken accessToken) {
        return userService().getMe(accessToken);
    }

    public User getMeBasic(AccessToken accessToken) {
        return userService().getMeBasic(accessToken);
    }

    /**
     * Retrieve a single Group with the given id. If no group with the given id can be found a
     * {@link NoResultException} is thrown.
     *
     * @param id          the id of the wanted group
     * @param accessToken the access token from OSIAM for the current session.
     * @return the group with the given id.
     * @throws org.osiam.client.exception.UnauthorizedException if the request could not be authorized.
     * @throws org.osiam.client.exception.NoResultException     if no user with the given id can be found
     * @throws org.osiam.client.exception.ForbiddenException    if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               if the connection to the given OSIAM service could not be initialized
     */
    public Group getGroup(String id, AccessToken accessToken) {
        return groupService().getGroup(id, accessToken);
    }

    /**
     * Retrieve a list of the of all {@link Group} resources saved in the OSIAM service.
     * If you need to have all Group but the number is very big, this method can be slow.
     * In this case you can also use Query.Builder with no filter to split the number of Groups returned
     *
     * @param accessToken the OSIAM access token for the current session
     * @return a list of all groups
     * @throws org.osiam.client.exception.UnauthorizedException if the request could not be authorized.
     * @throws org.osiam.client.exception.ForbiddenException    if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               if the connection to the given OSIAM service could not be initialized
     */
    public List<Group> getAllGroups(AccessToken accessToken) {
        return groupService().getAllGroups(accessToken);
    }

    /**
     * Search for existing groups by a given search string. For more detailed information about the possible logical
     * operators and usable fields please have a look into the wiki.
     *
     * @param queryString a string containing the needed search where statement
     * @param accessToken the OSIAM access token from for the current session
     * @return a QueryResult containing a list of all found Groups
     * @throws org.osiam.client.exception.UnauthorizedException if the request could not be authorized.
     * @throws org.osiam.client.exception.ForbiddenException    if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               if the connection to the given OSIAM service could not be initialized
     * @see <a href="https://github.com/osiam/connector4java/wiki/Working-with-groups#search-for-groups">https://github.com/osiam/connector4java/wiki/Working-with-groups#search-for-groups</a>
     */
    public QueryResult<Group> searchGroups(String queryString, AccessToken accessToken) {
        return groupService().searchGroups(queryString, accessToken);
    }

    /**
     * Search for existing groups by a given @{link Query}. For more detailed information about the possible logical
     * operators and usable fields please have a look into the wiki.
     *
     * @param query       containing the needed search where statement
     * @param accessToken the OSIAM access token from for the current session
     * @return a QueryResult containing a list of all found Groups
     * @throws org.osiam.client.exception.UnauthorizedException if the request could not be authorized.
     * @throws org.osiam.client.exception.ForbiddenException    if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               if the connection to the given OSIAM service could not be initialized
     * @see <a href="https://github.com/osiam/connector4java/wiki/Working-with-groups#search-for-groups">https://github.com/osiam/connector4java/wiki/Working-with-groups#search-for-groups</a>
     */
    public QueryResult<Group> searchGroups(Query query, AccessToken accessToken) {
        return groupService().searchGroups(query, accessToken);
    }

    /**
     * Provide an {@link AccessToken} for the given parameters of this service.
     *
     * @return a valid AccessToken
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               If the Service is unable to connect to the configured OAuth2 service.
     * @throws org.osiam.client.exception.UnauthorizedException If the configured credentials for this service are not permitted
     *                               to retrieve an {@link AccessToken}
     */
    public AccessToken retrieveAccessToken() {
       return authService().retrieveAccessToken();
    }

    /**
     * provides the needed URI which is needed to reconnect the User to the OSIAM server to login.
     * A detailed example how to use this method, can be seen in our wiki in gitHub
     * @return the needed redirect Uri
     * @see <a href="https://github.com/osiam/connector4java/wiki/Login-and-getting-an-access-token#grant-authorization-code">https://github.com/osiam/connector4java/wiki/Login-and-getting-an-access-token#grant-authorization-code</a>
     */
    public URI getRedirectLoginUri() {
    	return authService().getRedirectLoginUri();
    }

    /**
     * Provide an {@link AccessToken} for the given parameters of this service and the given {@link HttpResponse}.
     * If the User acepted your request for the needed data you will get an access token.
     * If the User denied your request a {@link ForbiddenException} will be thrown.
     * If the {@linkplain HttpResponse} does not contain a value named "code" or "error" a
     * {@linkplain InvalidAttributeException} will be thrown
     * @param authCodeResponse response given from the OSIAM server.
     * For more information please look at the wiki at github
     * @return a valid AccessToken
     * @throws org.osiam.client.exception.ForbiddenException in case the User had denied you the wanted data
     * @throws org.osiam.client.exception.InvalidAttributeException in case not authCode and no error message could be found in the response
     * @throws org.osiam.client.exception.ConflictException in case the given authCode could not be exchanged against a access token
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               If the Service is unable to connect to the configured OAuth2 service.
     * @see <a href="https://github.com/osiam/connector4java/wiki/Login-and-getting-an-access-token#grant-authorization-code">https://github.com/osiam/connector4java/wiki/Login-and-getting-an-access-token#grant-authorization-code</a>
     */
    public AccessToken retrieveAccessToken(HttpResponse authCodeResponse) {
    	return authService().retrieveAccessToken(authCodeResponse);
    }

    /**
     * Provide an {@link AccessToken} for the given parameters of this service and the given authCode.
     * @param authCode authentication code retrieved from the OSIAM Server by using the oauth2 login flow.
     * For more information please look at the wiki at github
     * @return a valid AccessToken
     * @throws org.osiam.client.exception.ConflictException in case the given authCode could not be exchanged against a access token
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               If the Service is unable to connect to the configured OAuth2 service.
     * @see <a href="https://github.com/osiam/connector4java/wiki/Login-and-getting-an-access-token#grant-authorization-code">https://github.com/osiam/connector4java/wiki/Login-and-getting-an-access-token#grant-authorization-code</a>
     */
    public AccessToken retrieveAccessToken(String authCode) {
    	return authService().retrieveAccessToken(authCode);
    }

    /**
     * saves the given {@link User} to the OSIAM DB.
     * @param user user to be saved
     * @param accessToken the OSIAM access token from for the current session
     * @return the same user Object like the given but with filled metadata and a new valid id
     * @throws org.osiam.client.exception.UnauthorizedException if the request could not be authorized.
     * @throws org.osiam.client.exception.ConflictException               if the User could not be created
     * @throws org.osiam.client.exception.ForbiddenException    if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               if the connection to the given OSIAM service could not be initialized
     */
    public User createUser(User user, AccessToken accessToken) {
        return userService().createUser(user, accessToken);
    }

    /**
     * saves the given {@link Group} to the OSIAM DB.
     * @param group group to be saved
     * @param accessToken the OSIAM access token from for the current session
     * @return the same group Object like the given but with filled metadata and a new valid id
     * @throws org.osiam.client.exception.UnauthorizedException if the request could not be authorized.
     * @throws org.osiam.client.exception.ConflictException     if the Group could not be created
     * @throws org.osiam.client.exception.ForbiddenException    if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               if the connection to the given OSIAM service could not be initialized
     */
    public Group createGroup(Group group, AccessToken accessToken) {
        return groupService().createGroup(group, accessToken);
    }

    /**
     * delete the given {@link Group} at the OSIAM DB.
     * @param id id of the Group to be deleted
     * @param accessToken the OSIAM access token from for the current session
     * @throws org.osiam.client.exception.UnauthorizedException if the request could not be authorized.
     * @throws org.osiam.client.exception.NoResultException     if no group with the given id can be found
     * @throws org.osiam.client.exception.ConflictException     if the Group could not be deleted
     * @throws org.osiam.client.exception.ForbiddenException    if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               if the connection to the given OSIAM service could not be initialized
     */
    public void deleteGroup(String id, AccessToken accessToken) {
    	groupService().deleteGroup(id, accessToken);
    }

    /**
     * delete the given {@link User} at the OSIAM DB.
     * @param id id of the User to be delete
     * @param accessToken the OSIAM access token from for the current session
     * @throws org.osiam.client.exception.UnauthorizedException if the request could not be authorized.
     * @throws org.osiam.client.exception.NoResultException     if no user with the given id can be found
     * @throws org.osiam.client.exception.ConflictException               if the User could not be deleted
     * @throws org.osiam.client.exception.ForbiddenException    if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               if the connection to the given OSIAM service could not be initialized
     */
    public void deleteUser(String id, AccessToken accessToken) {
    	userService().deleteUser(id, accessToken);
    }

    /**
     * update the user of the given id with the values given in the User Object.
     * For more detailed information how to set new field, update Fields or to delete Fields please look in the wiki
     * @param id if of the User to be updated
     * @param updateUser all Fields that need to be updated
     * @param accessToken the OSIAM access token from for the current session
     * @return the updated User Object with all new Fields
     * @see <a href="https://github.com/osiam/connector4java/wiki/Working-with-user">https://github.com/osiam/connector4java/wiki/Working-with-user</a>
     * @throws org.osiam.client.exception.UnauthorizedException if the request could not be authorized.
     * @throws org.osiam.client.exception.ConflictException     if the User could not be updated
     * @throws org.osiam.client.exception.NotFoundException     if no group with the given id can be found
     * @throws org.osiam.client.exception.ForbiddenException    if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               if the connection to the given OSIAM service could not be initialized
     */
    public User updateUser(String id, UpdateUser updateUser , AccessToken accessToken){
        return userService().updateUser(id, updateUser, accessToken);
    }

    /**
     * update the group of the given id with the values given in the Group Object.
     * For more detailed information how to set new field. Update Fields or to delete Fields please look in the wiki
     * @param id id of the Group to be updated
     * @param updateGroup all Fields that need to be updated
     * @param accessToken the OSIAM access token from for the current session
     * @return the updated group Object
     * @see <a href="https://github.com/osiam/connector4java/wiki/Working-with-groups">https://github.com/osiam/connector4java/wiki/Working-with-groups</a>
     * @throws org.osiam.client.exception.UnauthorizedException if the request could not be authorized.
     * @throws org.osiam.client.exception.ConflictException     if the Group could not be updated
     * @throws org.osiam.client.exception.NotFoundException     if no group with the given id can be found
     * @throws org.osiam.client.exception.ForbiddenException    if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               if the connection to the given OSIAM service could not be initialized
     */
    public Group updateGroup(String id, UpdateGroup updateGroup , AccessToken accessToken){
        return groupService().updateGroup(id, updateGroup, accessToken);
    }

    /**
     * The Builder class is used to construct instances of the {@link OsiamConnector}.
     */
    public static class Builder  {

        private String clientId;
        private String clientSecret;
        private GrantType grantType;
        private String username;
        private String password;
        private String authServiceEndpoint;
        private String resourceServiceEndpoint;
        private Scope scope;
        private Scope[] scopes;
        private String stringScope;
        private String clientRedirectUri;

        /**
         * Temporary default constructor
         */
        public Builder(){

        }

        /**
         * Set up the Builder for the construction of an {@link OsiamConnector} instance for the OAuth2-AuthService
         * service at the given authServiceEndpoint
         *
         * @param authServiceEndpoint The URL at which the OAuth2 service lives.
         */
        public Builder(String authServiceEndpoint) {
            this.authServiceEndpoint = authServiceEndpoint;
        }

        /**
         * use the given endpoint for communication with the OAuth2-Service for authentication
         * @param endpoint The AuthService endpoint to use for communication
         * @return The builder itself
         */
        public Builder setAuthServiceEndpoint(String endpoint){
            this.authServiceEndpoint = endpoint;
            return this;
        }

        /**
         * use the given endpoint for communication with the SCIM2 resource server.
         * @param endpoint The resource service endpoint to use
         * @return The builder itself
         */
        public Builder setResourceEndpoint(String endpoint){
            this.resourceServiceEndpoint = endpoint;
            return this;
        }

        /**
         * Use the given {@link Scope} to for the request.
         * @param scope the needed scope
         * @param scopes the needed scopes
         * @return The builder itself
         */
        public Builder setScope(Scope scope, Scope... scopes){
        	this.scope = scope;
        	this.scopes = scopes;
        	return this;
        }

        /**
         * The needed access token scopes as String like 'GET PATCH'
         * @param scope the needed scope
         * @return The builder itself
         */
        public Builder setScope(String scope){
        	this.stringScope = scope;
        	return this;
        }

        /**
         * Use the given {@link org.osiam.client.oauth.GrantType} to for the request. At this point only the grant type 'password' is supported.
         *
         * @param grantType of the requested AuthCode
         * @return The builder itself
         * @throws UnsupportedOperationException At build time if the GrantType is anything else than GrantType.PASSWORD
         */
        public Builder setGrantType(GrantType grantType) {
            this.grantType = grantType;
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
         * Add the given userName to the OAuth2 request
         *
         * @param userName The userName
         * @return The builder itself
         */
        public Builder setUserName(String userName) {
            this.username = userName;
            return this;
        }

        /**
         * Add the given password to the OAuth2 request
         *
         * @param password The password
         * @return The builder itself
         */
        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        /**
         * Add a Client redirect URI to the OAuth2 request
         *
         * @param clientRedirectUri the clientRedirectUri which is known to the OSIAM server
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
         * @throws org.osiam.client.exception.ConnectionInitializationException
         *          If either the provided client credentials (clientId/clientSecret)
         *          or, if the requested grant type is 'password', the user credentials (userName/password) are incomplete.
         */
        public OsiamConnector build() {
            return new OsiamConnector(this);
        }

    }
}
