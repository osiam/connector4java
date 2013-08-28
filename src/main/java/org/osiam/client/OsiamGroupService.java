package org.osiam.client;
/*
 * for licensing see the file license.txt.
 */

import java.util.UUID;

import org.apache.http.client.methods.HttpGet;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryResult;
import org.osiam.resources.scim.Group;

/**
 * OsiamGroupService provides all methods necessary to manipulate the {@link Group} resources registered in the
 * given OSIAM installation. For the construction of an instance please use the {@link OsiamGroupService.Builder}
 */
public final class OsiamGroupService extends AbstractOsiamService<Group> { // NOSONAR - Builder constructs instances of this class


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
    public Group getGroup(UUID id, AccessToken accessToken) {
        return getResource(id, accessToken);
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
     * delete the given {@link Group} at the OSIAM DB.
     * @param uuid id to be delete
     * @param accessToken the OSIAM access token from for the current session
     */
    public void deleteGroup(UUID id, AccessToken accessToken) {
    	deleteResource(id, accessToken);
    }
    
    /**
     * saves the given {@link Group} to the OSIAM DB.
     * @param group group to be saved
     * @param accessToken the OSIAM access token from for the current session
     * @return the same group Object like the given but with filled metadata and a new valid uuid
     */
    public Group createGroup(Group group, AccessToken accessToken) {
        return createResource(group , accessToken);
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
