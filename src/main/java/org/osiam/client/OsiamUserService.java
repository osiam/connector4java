package org.osiam.client;
/*
 * for licensing see the file license.txt.
 */

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryResult;
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
    public User getUserByUUID(UUID id, AccessToken accessToken) {
        return getResourceByUUID(id, accessToken);
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

        if (accessToken == null) { // NOSONAR - stmt in if is correct
            throw new IllegalArgumentException("The given accessToken can't be null.");
        }

        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            
            HttpGet realWebresource = createRealWebResource(accessToken);
            realWebresource.setURI(new URI(getUri() + "/me"));
            
            HttpResponse response = httpclient.execute(realWebresource);
            int httpStatus = response.getStatusLine().getStatusCode();

            // TODO: what errors do we expect?
            if (httpStatus != SC_OK) { // NOSONAR - stmt in if is correct
                switch (httpStatus) {
                    case SC_UNAUTHORIZED:
                        throw new UnauthorizedException("You are not authorized to access OSIAM. Please make sure your access token is valid");
                    case SC_NOT_FOUND:
                        throw new NoResultException("???");
                    default:
                        throw new ConnectionInitializationException("Unable to setup connection");
                }
            }

            InputStream content = response.getEntity().getContent();
            user = mapSingleResourceResponse(content);

            return user;
        } catch (IOException | URISyntaxException e) {
            throw new ConnectionInitializationException("Unable to setup connection", e);
        }
    }

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
