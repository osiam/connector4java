package org.osiam.client;
/*
 * for licensing see the file license.txt.
 */

import org.apache.http.client.methods.HttpGet;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryResult;
import org.osiam.resources.scim.Group;

import java.util.UUID;

/**
 * OsiamGroupService provides all methods necessary to manipulate the {@link Group} resources registered in the
 * given OSIAM installation. For the construction of an instance please use the {@link OsiamGroupService.Builder}
 */
public class OsiamGroupService extends AbstractOsiamService<Group> {


    /**
     * The private constructor for the OsiamGroupService. Please use the {@link OsiamGroupService.Builder}
     * to construct one.
     *
     * @param groupWebResource a valid WebResource to connect to a given OSIAM server
     */
    private OsiamGroupService(HttpGet groupWebResource) {
        super(groupWebResource);
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
    public Group getGroupByUUID(UUID id, AccessToken accessToken) {
        return getResourceByUUID(id, accessToken);
    }

    /**
     * Retrieve a list of the of all {@link Group} resources saved in the OSIAM service. If there are more than 100
     * only the first 100 are returned, The returned QueryResult contains Information about the total number of entries.
     *
     * @param accessToken the OSIAM access token for the current session
     * @return a QueryResult Containing a list of all groups
     */
    public QueryResult<Group> getAllGroups(AccessToken accessToken) {
        return getAllResources(accessToken);
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
        return searchResources(queryString, accessToken);
    }

    /**
     * Search for existing groups by a given @{link Query}.
     *
     * @param query       containing the needed search where statement
     * @param accessToken the OSIAM access token from for the current session
     * @return a QueryResult containing a list of all found Groups
     */
    public QueryResult<Group> searchGroups(Query query, AccessToken accessToken) {
        return searchResources(query, accessToken);
    }

    /**
     * The Builder is used to construct instances of the {@link OsiamGroupService}
     */
    public static class Builder extends AbstractOsiamService.Builder<Group> {

        /**
         * Set up the Builder for the construction of  an {@link OsiamGroupService} instance for the OSIAM service at
         * the given endpoint
         *
         * @param endpoint The URL at which the OSIAM server lives.
         */
        public Builder(String endpoint) {
            super(endpoint);
        }

        /**
         * constructs a OsiamGroupService with the given values
         *
         * @return a valid OsiamGroupService
         */
        public OsiamGroupService build() {
            return new OsiamGroupService(super.getWebResource());
        }
    }
}
