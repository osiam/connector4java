package org.osiam.client.connector;
/*
* for licensing see the file license.txt.
*/
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.osiam.client.OsiamGroupService;
import org.osiam.client.OsiamUserService;
import org.osiam.client.exception.ConflictException;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.ForbiddenException;
import org.osiam.client.exception.InvalidAttributeException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.AuthService;
import org.osiam.client.oauth.GrantType;
import org.osiam.client.oauth.Scope;
import org.osiam.client.oauth.AuthService.Builder;
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
    private String endpoint;
    private Scope scope;
    private Scope[] scopes;
    private String stringScope;
    private String clientRedirectUri;
    
	private AuthService authService;
	private OsiamUserService userService;
    private OsiamGroupService groupService;
	
    private OsiamConnector(Builder builder) {
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.grantType = builder.grantType;
        this.username = builder.username;
        this.password = builder.password;
        this.endpoint = builder.endpoint;
        this.scope = builder.scope;
        this.scopes = builder.scopes;
        this.stringScope = builder.stringScope;
        this.clientRedirectUri = builder.clientRedirectUri;
    }
    
    private AuthService authService(){
    	if(authService == null){    // NOSONAR - false-positive from clover; if-expression is correct
    		AuthService.Builder builder = new AuthService.Builder(endpoint);
    		
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
    
    private OsiamUserService userService(){
    	if(userService == null){     // NOSONAR - false-positive from clover; if-expression is correct
    		userService = new OsiamUserService.Builder(endpoint).build();
    	}
    	return userService;
    }

    private OsiamGroupService groupService(){
        if(groupService == null){    // NOSONAR - false-positive from clover; if-expression is correct
            groupService = new OsiamGroupService.Builder(endpoint).build();
        }
        return groupService;
    }

    /**
     * Retrieve a single User with the given id. If no user for the given id can be found a {@link org.osiam.client.exception.NoResultException}
     * is thrown.
     *
     * @param id          the id of the wanted user
     * @param accessToken the OSIAM access token from for the current session
     * @return the user with the given id
     * @throws org.osiam.client.exception.UnauthorizedException if the request could not be authorized.
     * @throws org.osiam.client.exception.NoResultException     if no user with the given id can be found
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               if no connection to the given OSIAM services could be initialized
     */
    public User getUser(String id, AccessToken accessToken) {
        return userService().getUser(id, accessToken);
    }

    /**
     * Retrieves all existing Users
     * @param accessToken
     * @return a list of all Users
     */
    public List<User> getAllUsers(AccessToken accessToken) {
        return userService().getAllUsers(accessToken);
    }

    /**
     * Search for the existing Users by a given search string. For more detailed information about the possible
     * logical operators and usable fields please have a look into the wiki.<p>
     * <b>Note:</b> The query string should be URL encoded!
     *
     * @param queryString The string with the query that should be passed to the OSIAM service
     * @param accessToken the OSIAM access token from for the current session
     * @return a QueryResult Containing a list of all found Users
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
     */
    public QueryResult<User> searchUsers(Query query, AccessToken accessToken) {
        return userService().searchUsers(query, accessToken);
    }

    /**
     * Retrieve the User with the who holds the given access token.
     * @param accessToken
     * @return the OSIAM access token from for the current session
     * @throws UnauthorizedException if the request could not be authorized.
     * @throws ConnectionInitializationException
     *                               if no connection to the given OSIAM services could be initialized
     */
    public User getMe(AccessToken accessToken) {
        return userService().getMe(accessToken);
    }

    /**
     * Retrieve a single Group with the given id. If no group with the given id can be found a
     * {@link NoResultException} is thrown.
     *
     * @param id          the id of the wanted group
     * @param accessToken the access token from OSIAM for the current session.
     * @return the group with the given id.
     * @throws UnauthorizedException if the request could not be authorized.
     * @throws NoResultException     if no group with the given id can be found.
     * @throws ConnectionInitializationException
     *                               if no connection to the given OSIAM service could be initialized
     */
    public Group getGroup(String id, AccessToken accessToken) {
        return groupService().getGroup(id, accessToken);
    }

    /**
     * Retrieve a list of the of all {@link Group} resources saved in the OSIAM service. If there are more than 100
     * only the first 100 are returned, The returned QueryResult contains Information about the total number of entries.
     *
     * @param accessToken the OSIAM access token for the current session
     * @return a list of all groups
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
     * @see <a href="https://github.com/osiam/connector4java/wiki/Working-with-groups#search-for-groups">https://github.com/osiam/connector4java/wiki/Working-with-groups#search-for-groups</a>
     */
    public QueryResult<Group> searchGroups(String queryString, AccessToken accessToken) {
        return groupService().searchGroups(queryString, accessToken);
    }

    /**
     * Search for existing groups by a given @{link Query}.
     *
     * @param query       containing the needed search where statement
     * @param accessToken the OSIAM access token from for the current session
     * @return a QueryResult containing a list of all found Groups
     */
    public QueryResult<Group> searchGroups(Query query, AccessToken accessToken) {
        return groupService().searchGroups(query, accessToken);
    }

    /**
     * Provide an {@link org.osiam.client.oauth.AccessToken} for the given parameters of this service.
     *
     * @return a valid AccessToken
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               If the Service is unable to connect to the configured OAuth2 service.
     * @throws org.osiam.client.exception.UnauthorizedException If the configured credentials for this service are not permitted
     *                               to retrieve an {@link org.osiam.client.oauth.AccessToken}
     */
    public AccessToken retrieveAccessToken() {
       return authService().retrieveAccessToken();
    }

    /**
     * provides the needed URI which is needed to reconect the User to the OSIAM server to login.
     * A detailed example how to use this methode, can be seen in our wiki in gitHub
     * @return
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
     * @param authCodeResponse response goven from the OSIAM server. 
     * For more information please look at the wiki at github
     * @return a valid AccessToken
     */
    public AccessToken retrieveAccessToken(HttpResponse authCodeResponse) {
    	return authService().retrieveAccessToken(authCodeResponse);
    }
    
    /**
     * Provide an {@link AccessToken} for the given parameters of this service and the given authCode.
     * @param authCode authentication code retrieved from the OSIAM Server by using the oauth2 login flow. 
     * For more information please look at the wiki at github
     * @return a valid AccessToken
     */
    public AccessToken retrieveAccessToken(String authCode) {
    	return authService().retrieveAccessToken(authCode);
    }
    
    /**
     * saves the given {@link User} to the OSIAM DB.
     * @param user user to be saved
     * @param accessToken the OSIAM access token from for the current session
     * @return the same user Object like the given but with filled metadata and a new valid id
     */
    public User createUser(User user , AccessToken accessToken) {
        return userService().createUser(user, accessToken);
    }

    /**
     * saves the given {@link Group} to the OSIAM DB.
     * @param group group to be saved
     * @param accessToken the OSIAM access token from for the current session
     * @return the same group Object like the given but with filled metadata and a new valid id
     */
    public Group createGroup(Group group , AccessToken accessToken) {
        return groupService().createGroup(group, accessToken);
    }
    
    /**
     * delete the given {@link User} at the OSIAM DB.
     * @param id id to be deleted
     * @param accessToken the OSIAM access token from for the current session
     */
    public void deleteGroup(String id, AccessToken accessToken) {
    	groupService().deleteGroup(id, accessToken);
    }
    /**
     * delete the given {@link Group} at the OSIAM DB.
     * @param id id to be deleted
     * @param accessToken the OSIAM access token from for the current session
     */
    public void deleteUser(String id, AccessToken accessToken) {
    	userService().deleteUser(id, accessToken);
    }

    /**
     * update the user of the given id with the values given in the User Object.
     * For more detailed information how to set new field. Update Fields or to delete Fields please look in the wiki
     * @param id
     * @param updateUser
     * @param accessToken
     * @return
     * @see <a href="https://github.com/osiam/connector4java/wiki/Working-with-user">https://github.com/osiam/connector4java/wiki/Working-with-user</a>
     */
    public User updateUser(String id, UpdateUser updateUser , AccessToken accessToken){
        return userService().updateUser(id, updateUser, accessToken);
    }

    /**
     * update the group of the given id with the values given in the Group Object.
     * For more detailed information how to set new field. Update Fields or to delete Fields please look in the wiki
     * @param id
     * @param updateGroup
     * @param accessToken
     * @return
     * @see <a href="https://github.com/osiam/connector4java/wiki/Working-with-groups">https://github.com/osiam/connector4java/wiki/Working-with-groups</a>
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
        private String endpoint;
        private Scope scope;
        private Scope[] scopes;
        private String stringScope;
        private String clientRedirectUri;
        
        /**
         * Set up the Builder for the construction of  an {@link OsiamConnector} instance for the OAuth2 service at
         * the given endpoint
         *
         * @param endpoint The URL at which the OAuth2 service lives.
         */
        public Builder(String endpoint) {
            this.endpoint = endpoint;
        }

        /**
         * Use the given {@link Scope} to for the request. 
         * @param scope the needed scope
         * @param scopes the needed scopes
         * @return
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
         * Add the given username to the OAuth2 request
         *
         * @param username The username
         * @return The builder itself
         */
        public Builder setUserName(String username) {
            this.username = username;
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
