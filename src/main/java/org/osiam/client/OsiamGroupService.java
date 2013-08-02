package org.osiam.client;
/*
 * for licensing see in the license.txt
 */

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.QueryResult;
import org.osiam.resources.scim.Group;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * OsiamGroupService provides all methods necessary to manipulate the Group-Entities registered in the
 * given OSIAM installation. For the construction of an instance please use the included {@link OsiamGroupService.Builder}
 */
public class OsiamGroupService extends AbstractOsiamService<Group>{

    /**
     * The private constructor for the OsiamGroupService. Please use the {@link OsiamGroupService.Builder}
     * to construct one.
     *
     * @param groupWebResource a valid WebResource to connect to a given OSIAM server
     */
    private OsiamGroupService(WebResource groupWebResource) {
    	super(groupWebResource);
    }

    /**
     * this method retrieves a single Group with the given id. If no group with the given id can be found an
     * {@link NoResultException} is thrown.
     *
     * @param id          				the uuid from the wanted group
     * @param accessToken 				the access token from OSIAM for the actual session
     * @return 							the group with the given id
     * @throws UnauthorizedException 	if the request could not be authorized. For example the access-token is not valid anymore.
     * @throws NoResultException     	if no group with the given id can be found
     * @throws ConnectionInitializationException
     *                                	if no connection to the given OSIAM services could be initialized
     */
    public Group getGroupByUUID(UUID id, AccessToken accessToken) {
    	return getResourceByUUID(id, accessToken);
    }

    /**
     * this method retrieves a list of the first 100 groups. I also gives back the information about the total number of groups
     * saved in OSIAM
     * @param accessToken the access token from OSIAM for the actual session
     * @return a QueryResult Containing a list of all groups
     */
    public QueryResult getAllGroups(AccessToken accessToken) {
        QueryResult resource;
        try {
            resource = webResource.header("Authorization", "Bearer " + accessToken.getToken())
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .get(QueryResult.class);
        } catch (UniformInterfaceException e) {
            switch (e.getResponse().getStatus()) {
                case SC_UNAUTHORIZED:
                    throw new UnauthorizedException("You are not authorized to access OSIAM. Please make sure your access token is valid");
                default:
                    throw e;
            }
        } catch (ClientHandlerException e) {
            throw new ConnectionInitializationException("Unable to setup connection", e);
        }
        return resource;
    }

    /**
     * The Builder class is used to construct instances of the {@link OsiamGroupService}
     */
    public static class Builder extends AbstractOsiamService.Builder<Group> {

        /**
         * Set up the Builder for the construction of  an {@link OsiamGroupService} instance for the OAuth2 service at
         * the given endpoint
         *
         * @param endpoint The URL at which the OSIAM server lives.
         */
        public Builder(String endpoint) {
        	super(endpoint);
        }

        /**
         * constructs a OsiamGroupService with the given values
         * @return a valid OsiamGroupService
         */
		public OsiamGroupService build() {
			return new OsiamGroupService(super.getWebResource());
		}
    }
}
