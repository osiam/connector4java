package org.osiam.client;
/*
 * for licensing see in the license.txt
 */
import com.sun.jersey.api.client.WebResource;
import org.osiam.resources.scim.User;

import java.util.UUID;

/**
 * A OSIAM Service which will be connected to the given OSIAM and provides all needed User methods
 */
public class OsiamUserService {

    private WebResource userWebResource;

    /**
     * The needed WebResource can be build with help of the ServiceBuilder 
     * @param userWebResource a valid WebResource to connect to a given OSIAM server
     */
    public OsiamUserService(WebResource userWebResource) {
        this.userWebResource = userWebResource;
    }

    /**
     * 
     * @param id the uuid from the wanted user
     * @param accessToken the access token from OSIAM for the actual session
     * @return a single User with the given id. Null if no user could be found
     */
    public User getUserByUUID(UUID id, String accessToken) {
        return userWebResource.path(id.toString()).
                header("Authorization", "Bearer " + accessToken).get(User.class);
    }
}