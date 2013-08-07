package org.osiam.client;
/*
 * for licensing see the file license.txt.
 */

import com.sun.jersey.api.client.WebResource;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.QueryResult;
import org.osiam.resources.scim.User;

import java.util.UUID;

/**
 * OsiamUserService provides all methods necessary to manipulate the User-Entities registered in the
 * given OSIAM installation. For the construction of an instance please use the included {@link OsiamUserService.Builder}
 */
public class OsiamUserService extends AbstractOsiamService<User> {

    /**
     * The private constructor for the OSIAMUserService. Please use the {@link OsiamUserService.Builder}
     * to construct one.
     *
     * @param userWebResource a valid WebResource to connect to a given OSIAM server
     */
    private OsiamUserService(WebResource userWebResource) {
        super(userWebResource);
    }

    /**
     * this method retrieves a single User with the given id. If no user with the given id can be found an
     * {@link NoResultException} is thrown.
     *
     * @param id          the uuid from the wanted user
     * @param accessToken the access token from OSIAM for the actual session
     * @return the user with the given id
     * @throws UnauthorizedException if the request could not be authorized. For example the access-token is not valid anymore.
     * @throws NoResultException     if no user with the given id can be found
     * @throws ConnectionInitializationException
     *                               if no connection to the given OSIAM services could be initialized
     */
    public User getUserByUUID(UUID id, AccessToken accessToken) {
        return getResourceByUUID(id, accessToken);
    }


    public QueryResult<User> getAllUsers(AccessToken accessToken) {
        return super.getAllResources(accessToken);
    }

    /**
     * with this method it is possible to search for the exisitngs Users by a given search string (where statement)
     * For more detailed information about the possible logical operators and usable fields please have a look into the wikie
     * @see <a href="https://github.com/osiam/connector4java/wiki/Working-with-user#search-for-user">https://github.com/osiam/connector4java/wiki/Working-with-user#search-for-user</a>
     * @param queryString a string containing the needed search where statement
     * @param accessToken the access token from OSIAM for the actual session
     * @return a QueryResult Containing a list of all found Users
     */
    public QueryResult<User> searchUsersByQueryString(String queryString, AccessToken accessToken) {
        return super.searchResourcesByQueryString(queryString, accessToken);
    }

    /**
     * The Builder class is used to construct instances of the {@link OsiamUserService}
     */
    public static class Builder extends AbstractOsiamService.Builder<User> {

        /**
         * Set up the Builder for the construction of  an {@link OsiamUserService} instance for the OAuth2 service at
         * the given endpoint
         *
         * @param endpoint The URL at which the OSIAM server lives.
         */
        public Builder(String endpoint) {
            super(endpoint);
        }

        /**
         * constructs a OsiamUserService with the given values
         *
         * @return a valid OsiamUserService
         */
        public OsiamUserService build() {
            return new OsiamUserService(super.getWebResource());
        }
    }
}
