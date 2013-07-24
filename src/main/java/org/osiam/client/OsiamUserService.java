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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

/**
 * OsiamUserService provides all methods necessary to manipulate the User-Entities registered in the
 * given OSIAM installation. For the construction of an instance please use the included {@link OsiamUserService.Builder}
 */
public class OsiamUserService {

    private final WebResource userWebResource;

    /**
     * The private constructor for the OSIAMUserService. Please use the {@link OsiamUserService.Builder}
     * to construct one.
     *
     * @param userWebResource a valid WebResource to connect to a given OSIAM server
     */
    private OsiamUserService(WebResource userWebResource) {
        this.userWebResource = userWebResource;
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

    /**
     * The Builder class is used to construct instances of the {@link OsiamUserService}
     */
    public static class Builder {
        private String endpoint;
        private String clientId;
        private String clientSecret;
        private String redirectUri;

        /**
         * Set up the Builder for the construction of  an {@link OsiamUserService} instance for the OAuth2 service at
         * the given endpoint
         *
         * @param endpoint The URL at which the OSIAM server lives.
         */
        public Builder(String endpoint) {
            this.endpoint = endpoint;
        }

        /**
         * Add a ClientId to the service
         *
         * @param clientId The client-Id
         * @return The builder itself
         */
        public Builder withClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         * Add a clientSecret to the service
         *
         * @param clientSecret The client secret
         * @return The builder itself
         */
        public Builder withClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        /**
         * add a redirectURI to the service
         *
         * @param redirectUri The redirectUri
         * @return The Builder itself
         */
        public Builder withRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public OsiamUserService build() {

            WebResource webResource;
            try {
                webResource = WebResourceProducer.createWebResource(new URI(endpoint + "/Users/"));
            } catch (URISyntaxException e) {
                throw new ConnectionInitializationException("Unable to setup connection " + endpoint +
                        "is not a valid URI.", e);
            }
            return new OsiamUserService(webResource);
        }
    }
}
