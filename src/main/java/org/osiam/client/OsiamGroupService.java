package org.osiam.client;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.resources.scim.Group;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
/*
 * for licensing see in the license.txt
 */

/**
 * OsiamGroupService provides all methods necessary to manipulate the Group-Entities registered in the
 * given OSIAM installation. For the construction of an instance please use the included {@link OsiamGroupService.Builder}
 */
public class OsiamGroupService {

    private final WebResource groupWebResource;
    
    /**
     * The private constructor for the OsiamGroupService. Please use the {@link OsiamGroupService.Builder}
     * to construct one.
     *
     * @param groupWebResource a valid WebResource to connect to a given OSIAM server
     */
    private OsiamGroupService(WebResource groupWebResource) {
        this.groupWebResource = groupWebResource;
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
        final Group group;
        try {
            group = groupWebResource.path(id.toString()).
                    header("Authorization", "Bearer " + accessToken.getToken()).get(Group.class);
        } catch (UniformInterfaceException e) {
            switch (e.getResponse().getStatus()) {
                case SC_UNAUTHORIZED:
                    throw new UnauthorizedException("You are not authorized to access OSIAM. Please make sure your access token is valid");
                case SC_NOT_FOUND:
                    throw new NoResultException("No Group with given UUID " + id);
                default:
                    throw e;
            }
        } catch (ClientHandlerException e) {
            throw new ConnectionInitializationException("Unable to setup connection", e);
        }
        return group;
    }
    
    /**
     * The Builder class is used to construct instances of the {@link OsiamGroupService}
     */
    public static class Builder {
        private String endpoint;
        
        /**
         * Set up the Builder for the construction of  an {@link OsiamGroupService} instance for the OAuth2 service at
         * the given endpoint
         *
         * @param endpoint The URL at which the OSIAM server lives.
         */
        public Builder(String endpoint) {
            this.endpoint = endpoint;
        }
        
        /**
         * constructes a OsiamGroupService with the given values
         * @return a valid OsiamGroupService
         */
        public OsiamGroupService build() {
            WebResource webResource;
            try {
                webResource = WebResourceProducer.createWebResource(new URI(endpoint + "/Groups/"));
            } catch (URISyntaxException e) {
                throw new ConnectionInitializationException("Unable to setup connection " + endpoint +
                        "is not a valid URI.", e);
            }
            return new OsiamGroupService(webResource);
        }
    }
}
