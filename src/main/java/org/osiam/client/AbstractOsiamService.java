package org.osiam.client;
/*
 * for licensing see the file license.txt.
 */

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryResult;
import org.osiam.resources.scim.CoreResource;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
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
abstract class AbstractOsiamService<T extends CoreResource> {

    protected WebResource webResource;
    private Class<T> type;
    private String typeName;
    private ObjectMapper mapper = new ObjectMapper();

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
     * Retrieve the Resource of the given Type with the given id. If no resource with the given id can be found, an
     * {@link NoResultException} is thrown.
     *
     * @param id          the uuid from the wanted resource
     * @param accessToken the access token from OSIAM for the actual session
     * @return The resource of the given type with the given id
     * @throws UnauthorizedException if the request could not be authorized.
     * @throws NoResultException     if no user with the given id can be found
     * @throws ConnectionInitializationException
     *                               if the connection to the given OSIAM service could be initialized
     */
    protected T getResourceByUUID(UUID id, AccessToken accessToken) {
        T resource;
        if (id == null) {
            throw new IllegalArgumentException("The given id can't be null.");
        }
        if (accessToken == null) {
            throw new IllegalArgumentException("The given accessToken can't be null.");
        }
        try {
            resource = webResource.path("/" + id.toString()).
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

    protected QueryResult<T> getAllResources(AccessToken accessToken) {
        final String queryResult;
        if (accessToken == null) {
            throw new IllegalArgumentException("The given accessToken can't be null.");
        }
        try {
            queryResult = webResource.header("Authorization", "Bearer " + accessToken.getToken())
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .get(String.class);
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
        final QueryResult<T> result;
        JavaType queryResultType = TypeFactory.defaultInstance().constructParametricType(QueryResult.class, type);
        try {
            result = mapper.readValue(queryResult, queryResultType);
        } catch (IOException e) {
            throw new ConnectionInitializationException("Unable to deserialize query result", e);
        }
        return result;
    }

    protected QueryResult<T> searchResources(String queryString, AccessToken accessToken) {

        final String queryResultAsString;
        if (accessToken == null) {
            throw new IllegalArgumentException("The given accessToken can't be null.");
        }
        WebResource queryResource = webResource.queryParam("access_token", accessToken.getToken());
        try {
            String[] queryParams = queryString.split("&");
            for (String queryParam : queryParams) {
                String[] kv = queryParam.split("=");
                queryResource = queryResource.queryParam(kv[0], kv[1]);
            }
            queryResultAsString = queryResource.accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);

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
        final QueryResult<T> result;
        JavaType queryResultType = TypeFactory.defaultInstance().constructParametricType(QueryResult.class, type);
        try {
            result = mapper.readValue(queryResultAsString, queryResultType);
        } catch (IOException e) {
            throw new ConnectionInitializationException("Unable to deserialize query result", e);
        }
        return result;
    }

    protected QueryResult<T> searchResources(Query query, AccessToken accessToken) {
        if (query == null) {
            throw new IllegalArgumentException("The given queryBuilder can't be null.");
        }
        return searchResources(query.toString(), accessToken);
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
         * @return The webresource for the type of OSIAM-service
         */
        protected WebResource getWebResource() {
            WebResource webResource;
            try {
                webResource = WebResourceProducer.createWebResource(new URI(endpoint + "/" + typeName + "s"));
            } catch (URISyntaxException e) {
                throw new ConnectionInitializationException("Unable to setup connection " + endpoint +
                        "is not a valid URI.", e);
            }
            return webResource;
        }
    }
}
