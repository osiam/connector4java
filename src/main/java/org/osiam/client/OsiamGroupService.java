package org.osiam.client;
/*
 * for licensing see the file license.txt.
 */

import org.osiam.client.exception.NoResultException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryResult;
import org.osiam.client.update.UpdateGroup;
import org.osiam.resources.scim.Group;

import java.util.List;

/**
 * OsiamGroupService provides all methods necessary to manipulate the {@link Group} resources registered in the
 * given OSIAM installation. For the construction of an instance please use the {@link OsiamGroupService.Builder}
 */
public final class OsiamGroupService extends AbstractOsiamService<Group> { // NOSONAR - Builder constructs instances of this class

    /**
     * The private constructor for the OsiamGroupService. Please use the {@link OsiamGroupService.Builder}
     * to construct one.
     *
     * @param builder The Builder to build the OsiamGroupService from
     */
    private OsiamGroupService(Builder builder) {
        super(builder);
    }

    /**
     * Retrieve a single Group with the given id. If no group with the given id can be found a
     * {@link NoResultException} is thrown.
     *
     * @param id          the id of the wanted group
     * @param accessToken the access token from OSIAM for the current session.
     * @return the group with the given id.
     * @throws org.osiam.client.exception.UnauthorizedException
     *          if the request could not be authorized.
     * @throws org.osiam.client.exception.NoResultException
     *          if no user with the given id can be found
     * @throws org.osiam.client.exception.ForbiddenException
     *          if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *          if the connection to the given OSIAM service could not be initialized
     */
    public Group getGroup(String id, AccessToken accessToken) {
        return getResource(id, accessToken);
    }

    /**
     * Retrieve a list of the of all {@link Group} resources saved in the OSIAM service.
     * If you need to have all Group but the number is very big, this method can be slow.
     * In this case you can also use Query.Builder with no filter to split the number of Groups returned
     *
     * @param accessToken the OSIAM access token for the current session
     * @return a list of all groups
     * @throws org.osiam.client.exception.UnauthorizedException
     *          if the request could not be authorized.
     * @throws org.osiam.client.exception.ForbiddenException
     *          if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *          if the connection to the given OSIAM service could not be initialized
     */
    public List<Group> getAllGroups(AccessToken accessToken) {
        return getAllResources(accessToken);
    }

    /**
     * Search for existing groups by a given search string. For more detailed information about the possible logical
     * operators and usable fields please have a look into the wiki.
     *
     * @param queryString a string containing the needed search where statement
     * @param accessToken the OSIAM access token from for the current session
     * @return a QueryResult containing a list of all found Groups
     * @throws org.osiam.client.exception.UnauthorizedException
     *          if the request could not be authorized.
     * @throws org.osiam.client.exception.ForbiddenException
     *          if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *          if the connection to the given OSIAM service could not be initialized
     * @see <a href="https://github.com/osiam/connector4java/wiki/Working-with-groups#search-for-groups">https://github.com/osiam/connector4java/wiki/Working-with-groups#search-for-groups</a>
     */
    public QueryResult<Group> searchGroups(String queryString, AccessToken accessToken) {
        return searchResources(queryString, accessToken);
    }

    /**
     * Search for existing groups by a given @{link Query}. For more detailed information about the possible logical
     * operators and usable fields please have a look into the wiki.
     *
     * @param query       containing the needed search where statement
     * @param accessToken the OSIAM access token from for the current session
     * @return a QueryResult containing a list of all found Groups
     * @throws org.osiam.client.exception.UnauthorizedException
     *          if the request could not be authorized.
     * @throws org.osiam.client.exception.ForbiddenException
     *          if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *          if the connection to the given OSIAM service could not be initialized
     * @see <a href="https://github.com/osiam/connector4java/wiki/Working-with-groups#search-for-groups">https://github.com/osiam/connector4java/wiki/Working-with-groups#search-for-groups</a>
     */
    public QueryResult<Group> searchGroups(Query query, AccessToken accessToken) {
        return searchResources(query, accessToken);
    }

    /**
     * delete the given {@link Group} at the OSIAM DB.
     *
     * @param id          id of the Group to be deleted
     * @param accessToken the OSIAM access token from for the current session
     * @throws org.osiam.client.exception.UnauthorizedException
     *          if the request could not be authorized.
     * @throws org.osiam.client.exception.NoResultException
     *          if no group with the given id can be found
     * @throws org.osiam.client.exception.ConflictException
     *          if the Group could not be deleted
     * @throws org.osiam.client.exception.ForbiddenException
     *          if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *          if the connection to the given OSIAM service could not be initialized
     */
    public void deleteGroup(String id, AccessToken accessToken) {
        deleteResource(id, accessToken);
    }

    /**
     * saves the given {@link Group} to the OSIAM DB.
     *
     * @param group       group to be saved
     * @param accessToken the OSIAM access token from for the current session
     * @return the same group Object like the given but with filled metadata and a new valid id
     * @throws org.osiam.client.exception.UnauthorizedException
     *          if the request could not be authorized.
     * @throws org.osiam.client.exception.ConflictException
     *          if the Group could not be created
     * @throws org.osiam.client.exception.ForbiddenException
     *          if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *          if the connection to the given OSIAM service could not be initialized
     */
    public Group createGroup(Group group, AccessToken accessToken) {
        return createResource(group, accessToken);
    }

    /**
     * update the group of the given id with the values given in the Group Object.
     * For more detailed information how to set new field. Update Fields or to delete Fields please look in the wiki
     *
     * @param id          id of the Group to be updated
     * @param updateGroup all Fields that need to be updated
     * @param accessToken the OSIAM access token from for the current session
     * @return the updated group Object
     * @throws org.osiam.client.exception.UnauthorizedException
     *          if the request could not be authorized.
     * @throws org.osiam.client.exception.ConflictException
     *          if the Group could not be updated
     * @throws org.osiam.client.exception.NotFoundException
     *          if no group with the given id can be found
     * @throws org.osiam.client.exception.ForbiddenException
     *          if the scope doesn't allow this request
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *          if the connection to the given OSIAM service could not be initialized
     * @see <a href="https://github.com/osiam/connector4java/wiki/Working-with-groups">https://github.com/osiam/connector4java/wiki/Working-with-groups</a>
     */
    public Group updateGroup(String id, UpdateGroup updateGroup, AccessToken accessToken) {
        return updateResource(id, updateGroup.getScimConformUpdateGroup(), accessToken);
    }

    /**
     * Updates only the given fields of the Group and leaves the omitted fields untouched.
     * @param group A Group object with the values to update filled in
     * @param accessToken A valid AccessToken
     * @return The updated Group as seen by the server
     */
    public Group updateGroup(String id, Group group, AccessToken accessToken) {
        return updateResource(id, group, accessToken);
    }

    /**
     * Replaces the Group with the given Group. Any field set in the original Group but not in the
     * given Group will be removed. Including memberships.
     * @param group A Group object to replace the given Group with
     * @param accessToken A valid AccessToken
     * @return The replaced Group as seen by the server
     */
    public Group replaceGroup(String id, Group group, AccessToken accessToken){
        return replaceResource(id, group, accessToken);
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
            return new OsiamGroupService(this);
        }
    }
}
