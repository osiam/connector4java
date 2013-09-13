package org.osiam.client;
/*
 * for licensing see the file license.txt.
 */

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.osiam.client.exception.ConflictException;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.ForbiddenException;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryResult;
import org.osiam.client.update.UpdateUser;
import org.osiam.resources.scim.User;

/**
 * The OsiamUserService provides all methods necessary to manipulate the User-entries registered in the
 * given OSIAM installation. For the construction of an instance please use the included {@link OsiamUserService.Builder}
 */
public final class OsiamUserService extends AbstractOsiamService<User> { // NOSONAR - Builder constructs instances of this class

    /**
     * The private constructor for the OSIAMUserService. Please use the {@link OsiamUserService.Builder}
     * to construct one.
     *
     * @param userWebResource a valid WebResource to connect to a given OSIAM server
     */
    private OsiamUserService(HttpGet userWebResource) {
        super(userWebResource);
    }

    /**
     * Retrieve a single User with the given id. If no user for the given id can be found a {@link NoResultException}
     * is thrown.
     *
     * @param id          the uuid of the wanted user
     * @param accessToken the OSIAM access token from for the current session
     * @return the user with the given id
     * @throws UnauthorizedException if the request could not be authorized.
     * @throws NoResultException     if no user with the given id can be found
     * @throws ConnectionInitializationException
     *                               if no connection to the given OSIAM services could be initialized
     */
    public User getUser(String id, AccessToken accessToken) {
        return getResource(id, accessToken);
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
	final User user;

        if (accessToken == null) { // NOSONAR - false-positive from clover; if-expression is correct
            throw new IllegalArgumentException("The given accessToken can't be null.");
        }

        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            
            HttpGet realWebresource = createRealWebResource(accessToken);
            realWebresource.setURI(new URI(getUri() + "/me"));
            
            HttpResponse response = httpclient.execute(realWebresource);
            int httpStatus = response.getStatusLine().getStatusCode();

            if (httpStatus != SC_OK) { // NOSONAR - false-positive from clover; if-expression is correct
                String errorMessage;
                switch (httpStatus) {
                    case SC_UNAUTHORIZED:
                        errorMessage = getErrorMessage(response, "You are not authorized to access OSIAM. Please make sure your access token is valid");
                        throw new UnauthorizedException(errorMessage);
                    case SC_NOT_FOUND:
                        errorMessage = getErrorMessage(response, "No User could be found, who holds the supplied access token");
                        throw new NoResultException(errorMessage);
                    case  SC_CONFLICT:
                        errorMessage = getErrorMessage(response, "Unable to retrieve the actual User.");
                        throw new ConflictException(errorMessage);
                    case SC_FORBIDDEN:
                    	errorMessage = "Insufficient scope (" + accessToken.getScope() + ") to retrieve the actual User.";
                        throw new ForbiddenException(errorMessage);
                    default:
                        errorMessage = getErrorMessage(response, String.format("Unable to setup connection (HTTP Status Code: %d)", httpStatus));
                        throw new ConnectionInitializationException(errorMessage);
                }
            }

            InputStream content = response.getEntity().getContent();
            user = mapSingleResourceResponse(content);

            return user;
        } catch (IOException | URISyntaxException e) {
            throw new ConnectionInitializationException("Unable to setup connection", e);
        }
    }

    /**
     * Retrieves all existing Users
     * @param accessToken
     * @return a QueryResult Containing a list of all Users
     */
    public QueryResult<User> getAllUsers(AccessToken accessToken) {
        return super.getAllResources(accessToken);
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
        return super.searchResources(queryString, accessToken);
    }

    /**
     * Search for existing Users by the given {@link Query}.
     *
     * @param query       containing the query to execute.
     * @param accessToken the OSIAM access token from for the current session
     * @return a QueryResult Containing a list of all found Users
     */
    public QueryResult<User> searchUsers(Query query, AccessToken accessToken) {
        return super.searchResources(query, accessToken);
    }

    /**
     * delete the given {@link User} at the OSIAM DB.
     * @param uuid id to be delete
     * @param accessToken the OSIAM access token from for the current session
     */
    public void deleteUser(String id, AccessToken accessToken) {
        deleteResource(id, accessToken);
    }

    /**
     * saves the given {@link User} to the OSIAM DB.
     * @param user user to be saved
     * @param accessToken the OSIAM access token from for the current session
     * @return the same user Object like the given but with filled metadata and a new valid uuid
     */
    public User createUser(User user , AccessToken accessToken) {
        return createResource(user , accessToken);
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
        if (updateUser == null) { // NOSONAR - false-positive from clover; if-expression is correct
            throw new IllegalArgumentException("The given updateUser can't be null.");
        }
        return updateResource(id, updateUser.getScimConformUpdateUser(), accessToken);
    }
    /**
     * The Builder class is used to construct instances of the {@link OsiamUserService}
     */
    public static class Builder extends AbstractOsiamService.Builder<User> {

        /**
         * Set up the Builder for the construction of  an {@link OsiamUserService} instance for the OSIAM service at
         * the given endpoint
         *
         * @param endpoint The URL at which the OSIAM server lives.
         */
        public Builder(String endpoint) {
            super(endpoint);
        }

        /**
         * constructs an OsiamUserService with the given values
         *
         * @return a valid OsiamUserService
         */
        public OsiamUserService build() {
            return new OsiamUserService(super.getWebResource());
        }
    }
}
