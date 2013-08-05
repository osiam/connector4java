package org.osiam.client;
/*
 * for licensing see the file license.txt.
 */

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

/**
 * AbstractOsiamService provides all basic methods necessary to manipulate the Entities registered in the
 * given OSIAM installation. For the construction of an instance please use the included {@link AbstractOsiamService.Builder}
 */
abstract class AbstractOsiamService<T> {

    protected WebResource webResource;
    private Class<T> type;
    private String typeName;

    /**
     * The protected constructor for the AbstractOsiamService. Please use the {@link AbstractOsiamService.Builder}
     * to construct one.
     *
     * @param userWebResource a valid WebResource to connect to a given OSIAM server
     */
    @SuppressWarnings("unchecked")
    protected AbstractOsiamService(WebResource userWebResource) {
        this.webResource = userWebResource;
        this.type = (Class<T>)
                ((ParameterizedType) getClass().getGenericSuperclass())
                        .getActualTypeArguments()[0];
        typeName = type.getSimpleName();
    }

    /**
     * Provide the {@link URI} this Service uses for queries.
     *
     * @return The URI used by this Service
     */
    public URI getUri() {
        return webResource.getURI();
    }

    /**
     * this method retrieves a Ressource of the given Type with the given id. If no Entry with the given id can be found an
     * {@link NoResultException} is thrown.
     *
     * @param id          the uuid from the wanted ressource
     * @param accessToken the access token from OSIAM for the actual session
     * @return the given type with the given id
     * @throws UnauthorizedException if the request could not be authorized. For example the access-token is not valid anymore.
     * @throws NoResultException     if no user with the given id can be found
     * @throws ConnectionInitializationException
     *                               if no connection to the given OSIAM services could be initialized
     */
    protected T getResourceByUUID(UUID id, AccessToken accessToken) {
        T resource;
        try {
            resource = webResource.path(id.toString()).
                    header("Authorization", "Bearer " + accessToken.getToken())
                    .accept(MediaType.APPLICATION_JSON_TYPE).get(type);
        } catch (UniformInterfaceException e) {
            switch (e.getResponse().getStatus()) {
                case SC_UNAUTHORIZED:
                    throw new UnauthorizedException("You are not authorized to access OSIAM. Please make sure your access token is valid");
                case SC_NOT_FOUND:
                    throw new NoResultException("No " + typeName + " with given UUID " + id);
                default:
                    throw e;
            }
        } catch (ClientHandlerException e) {
            throw new ConnectionInitializationException("Unable to setup connection", e);
        }
        return resource;
    }

    /**
     * The Builder class is used to prove a WebResource to build the needed Service
     *
     * @param <T> a org.osiam.resources.scim.User or a org.osiam.resources.scim.Group
     */
    protected static class Builder<T> {
        private String endpoint;
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private Class<T> type;
        private String typeName;

        /**
         * Set up the Builder for the construction of  an {@link AbstractOsiamService} instance for the OAuth2 service at
         * the given endpoint
         *
         * @param endpoint The URL at which the OSIAM server lives.
         */
        @SuppressWarnings("unchecked")
        protected Builder(String endpoint) {
            this.endpoint = endpoint;
            this.type = (Class<T>)
                    ((ParameterizedType) getClass().getGenericSuperclass())
                            .getActualTypeArguments()[0];
            typeName = type.getSimpleName();
        }

        /**
         * Add a ClientId to the service
         *
         * @param clientId The client-Id
         * @return The builder itself
         */
        public Builder<T> withClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         * Add a clientSecret to the service
         *
         * @param clientSecret The client secret
         * @return The builder itself
         */
        public Builder<T> withClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        /**
         * add a redirectURI to the service
         *
         * @param redirectUri The redirectUri
         * @return The Builder itself
         */
        public Builder<T> withRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        /**
         * creates a WebResource to the needed endpoint
         *
         * @return
         */
        protected WebResource getWebResource() {
            WebResource webResource;
            try {
                webResource = WebResourceProducer.createWebResource(new URI(endpoint + "/" + typeName + "s/"));
            } catch (URISyntaxException e) {
                throw new ConnectionInitializationException("Unable to setup connection " + endpoint +
                        "is not a valid URI.", e);
            }
            return webResource;
        }
    }
}
