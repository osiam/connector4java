package org.osiam.client.connector;
/*
* for licensing see the file license.txt.
*/
import org.osiam.client.OsiamGroupService;
import org.osiam.client.OsiamUserService;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.AuthService;
import org.osiam.client.oauth.GrantType;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryResult;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.User;

import java.util.UUID;
/**
 * OsiamConnector provides access to the OAuth2 service used to authorize requests
 * and all methods necessary to manipulate the {@link Group} and {@link User} resources registered in the
 * given OSIAM installation. For the construction of an instance please use the {@link OsiamConnector.Builder}
 */
public class OsiamConnector {

    private String clientId;
    private String clientSecret;
    private GrantType grantType;
    private String username;
    private String password;
    private String endpoint;
    
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
    }
    
    private AuthService authService(){
    	if(authService == null){
    		AuthService.Builder builder = new AuthService.Builder(endpoint);
    		
    		if(clientId != null){
    			builder = builder.clientId(clientId);
    		}
    		if(clientSecret != null){
    			builder = builder.clientSecret(clientSecret);
    		}
    		if(grantType != null){
    			builder = builder.grantType(grantType);
    		}
    		if(password != null){
    			builder = builder.password(password);
    		}
    		if(username != null){
    			builder = builder.username(username);
    		}
    		authService = builder.build();
    	}
    	return authService;
    }
    
    private OsiamUserService userService(){
    	if(userService == null){
    		userService = new OsiamUserService.Builder(endpoint).build();
    	}
    	return userService;
    }

    private OsiamGroupService groupService(){
        if(groupService == null){
            groupService = new OsiamGroupService.Builder(endpoint).build();
        }
        return groupService;
    }

    /**
     * Retrieve a single User with the given id. If no user for the given id can be found a {@link org.osiam.client.exception.NoResultException}
     * is thrown.
     *
     * @param id          the uuid of the wanted user
     * @param accessToken the OSIAM access token from for the current session
     * @return the user with the given id
     * @throws org.osiam.client.exception.UnauthorizedException if the request could not be authorized.
     * @throws org.osiam.client.exception.NoResultException     if no user with the given id can be found
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               if no connection to the given OSIAM services could be initialized
     */
    public User getUser(UUID id, AccessToken accessToken) {
        return userService().getUser(id, accessToken);
    }

    /**
     * Retrieves all existing Users
     * @param accessToken
     * @return a QueryResult Containing a list of all Users
     */
    public QueryResult<User> getAllUsers(AccessToken accessToken) {
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
     * @param id          the uuid of the wanted group
     * @param accessToken the access token from OSIAM for the current session.
     * @return the group with the given id.
     * @throws UnauthorizedException if the request could not be authorized.
     * @throws NoResultException     if no group with the given id can be found.
     * @throws ConnectionInitializationException
     *                               if no connection to the given OSIAM service could be initialized
     */
    public Group getGroup(UUID id, AccessToken accessToken) {
        return groupService().getGroup(id, accessToken);
    }

    /**
     * Retrieve a list of the of all {@link Group} resources saved in the OSIAM service. If there are more than 100
     * only the first 100 are returned, The returned QueryResult contains Information about the total number of entries.
     *
     * @param accessToken the OSIAM access token for the current session
     * @return a QueryResult Containing a list of all groups
     */
    public QueryResult<Group> getAllGroups(AccessToken accessToken) {
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
     * saves the given {@link User} to the OSIAM DB.
     * @param user user to be saved
     * @param accessToken the OSIAM access token from for the current session
     * @return the same user Object like the given but with filled metadata and a new valid uuid
     */
    public User createUser(User user , AccessToken accessToken) {
        return userService().createUser(user, accessToken);
    }

    /**
     * saves the given {@link Group} to the OSIAM DB.
     * @param group group to be saved
     * @param accessToken the OSIAM access token from for the current session
     * @return the same group Object like the given but with filled metadata and a new valid uuid
     */
    public Group createGroup(Group group , AccessToken accessToken) {
        return groupService().createGroup(group, accessToken);
    }
    
    /**
     * delete the given {@link User} at the OSIAM DB.
     * @param uuid id to be delete
     * @param accessToken the OSIAM access token from for the current session
     */
    public void deleteGroup(UUID id, AccessToken accessToken) {
    	groupService().deleteGroup(id, accessToken);
    }
    /**
     * delete the given {@link Group} at the OSIAM DB.
     * @param uuid id to be delete
     * @param accessToken the OSIAM access token from for the current session
     */
    public void deleteUser(UUID id, AccessToken accessToken) {
    	userService().deleteUser(id, accessToken);
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
         * Use the given {@link org.osiam.client.oauth.GrantType} to for the request. At this point only the grant type 'password' is supported.
         *
         * @param grantType of the requested AuthCode
         * @return The builder itself
         * @throws UnsupportedOperationException At build time if the GrantType is anything else than GrantType.PASSWORD
         */
        public Builder grantType(GrantType grantType) {
            this.grantType = grantType;
            return this;
        }

        /**
         * Add a ClientId to the OAuth2 request
         *
         * @param clientId The client-Id
         * @return The builder itself
         */
        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         * Add a clientSecret to the OAuth2 request
         *
         * @param clientSecret The client secret
         * @return The builder itself
         */
        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        /**
         * Add the given username to the OAuth2 request
         *
         * @param username The username
         * @return The builder itself
         */
        public Builder username(String username) {
            this.username = username;
            return this;
        }

        /**
         * Add the given password to the OAuth2 request
         *
         * @param password The password
         * @return The builder itself
         */
        public Builder password(String password) {
            this.password = password;
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
