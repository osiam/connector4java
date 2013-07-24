package org.osiam.client;
/*
 * for licensing see in the license.txt
 */

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.resources.scim.User;

import java.util.UUID;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

/**
 * A OSIAM Service which will be connected to the given OSIAM and provides all needed User methods
 */
public class OsiamUserService {

    private WebResource userWebResource;

    /**
     * With a OsiamUserService is it possible to retrieve or to save any User saved in the OSIAM server
     * The needed WebResource can be build with help of the ServiceBuilder.buildUserService(...)
     *
     * @param userWebResource a valid WebResource to connect to a given OSIAM server
     */
    public OsiamUserService(WebResource userWebResource) {
        this.userWebResource = userWebResource;
    }

    /**
     * this method retrieves a single User with the given id. If no user with the given id can be found an
     * {@link NoResultException} is thrown.
     *
     * @param id          the uuid from the wanted user
     * @param accessToken the access token from OSIAM for the actual session
     * @return the user with the given id
     * @throws UnauthorizedException in case you are not authorized. For example the access-token is not valid anymore
     * @throws NoResultException     is thrown if no user with the given id can be found
     * @throws ConnectionInitializationException
     *                               is thrown if no connection to the given OSIAM services could be initialized
     */
    public User getUserByUUID(UUID id, AccessToken accessToken) {
        final User user;
        try {
            user = userWebResource.path(id.toString()).
                    header("Authorization", "Bearer " + accessToken.getToken()).get(User.class);
        } catch (UniformInterfaceException e) {
            switch (e.getResponse().getStatus()) {
                case SC_UNAUTHORIZED:
                    throw new UnauthorizedException("You are not authorized to access OSIAM. Please make sure your access token is valid");
                case SC_NOT_FOUND:
                    throw new NoResultException("No User with given UUID " + id);
                default:
                    throw e;
            }
        } catch (ClientHandlerException e) {
            throw new ConnectionInitializationException("Unable to setup connection", e);
        }
        return user;
    }
}
