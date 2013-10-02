package org.osiam.client;
/*
 * for licensing see the file license.txt.
 */

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.apache.http.HttpStatus.SC_CONFLICT;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
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
     * The private constructor for the OsiamUserService. Please use the {@link OsiamUserService.Builder}
     * to construct one.
     *
     * @param userWebResource a valid WebResource to connect to a given OSIAM server
     */
    private OsiamUserService(Builder builder) {
        super(builder);
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
        return getResource(id, accessToken);
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
    public User getMeBasic(AccessToken accessToken) {
    	final User user;
        if (accessToken == null) { // NOSONAR - false-positive from clover; if-expression is correct
            throw new IllegalArgumentException("The given accessToken can't be null.");
        }

        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            
            HttpGet realWebresource = createRealWebResource(accessToken);
            realWebresource.setURI(new URI(getMeWebResource().getURI().toString()));
            
            HttpResponse response = httpclient.execute(realWebresource);
            int httpStatus = response.getStatusLine().getStatusCode();

            if (httpStatus != SC_OK) { // NOSONAR - false-positive from clover; if-expression is correct
                String errorMessage;
                switch (httpStatus) {
                    case SC_UNAUTHORIZED:
                        errorMessage = getErrorMessage(response, "You are not authorized to access OSIAM. Please make sure your access token is valid");
                        throw new UnauthorizedException(errorMessage);
                    case SC_FORBIDDEN:
                    	errorMessage = "Insufficient scope (" + accessToken.getScope() + ") to retrieve the actual User.";
                        throw new ForbiddenException(errorMessage);
                    case SC_CONFLICT:
                        errorMessage = getErrorMessage(response, "Unable to retrieve the actual User.");
                        throw new ConflictException(errorMessage);
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
    * Retrieve the User who holds the given access token.
    * Not to be used for the grant Client-Credentials
    * @param accessToken the OSIAM access token from for the current session
    * @return the actual logged in user
    * @throws UnauthorizedException if the request could not be authorized.
    * @throws org.osiam.client.exception.ForbiddenException if the scope doesn't allow this request
    * @throws ConnectionInitializationException
    * if no connection to the given OSIAM services could be initialized
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
                        case SC_FORBIDDEN:
                         errorMessage = "Insufficient scope (" + accessToken.getScope() + ") to retrieve the actual User.";
                            throw new ForbiddenException(errorMessage);
                        case SC_CONFLICT:
                            errorMessage = getErrorMessage(response, "Unable to retrieve the actual User.");
                            throw new ConflictException(errorMessage);
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
     * Retrieve a list of the of all {@link User} resources saved in the OSIAM service. 
     * If you need to have all User but the number is very big, this method can be slow.
     * In this case you can also use Query.Builder with no filter to split the number of User returned
     *
     * @param accessToken
     * @return a list of all Users
     * @throws org.osiam.client.exception.UnauthorizedException if the request could not be authorized.
     * @throws org.osiam.client.exception.ForbiddenException    if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               if the connection to the given OSIAM service could not be initialized
     */
    public List<User> getAllUsers(AccessToken accessToken) {
        return super.getAllResources(accessToken);
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
        return super.searchResources(queryString, accessToken);
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
        return super.searchResources(query, accessToken);
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
        deleteResource(id, accessToken);
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
        return createResource(user, accessToken);
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
            return new OsiamUserService(this);
        }
    }
}
