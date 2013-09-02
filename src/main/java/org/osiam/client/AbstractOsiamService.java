package org.osiam.client;
/*
 * for licensing see the file license.txt.
 */

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.osiam.client.exception.*;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryResult;
import org.osiam.resources.scim.CoreResource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.apache.http.HttpStatus.*;

/**
 * AbstractOsiamService provides all basic methods necessary to manipulate the Entities registered in the
 * given OSIAM installation. For the construction of an instance please use the included {@link AbstractOsiamService.Builder}
 */
abstract class AbstractOsiamService<T extends CoreResource> {

    private HttpGet webResource;
    private Class<T> type;
    private String typeName;
    private ObjectMapper mapper;

    /**
     * The protected constructor for the AbstractOsiamService. Please use the {@link AbstractOsiamService.Builder}
     * to construct one.
     *
     * @param userWebResource a valid WebResource to connect to a given OSIAM server
     */
    @SuppressWarnings("unchecked")
    protected AbstractOsiamService(HttpGet userWebResource) {
        mapper = new ObjectMapper();
        webResource = userWebResource;
        type = (Class<T>)
                ((ParameterizedType) getClass().getGenericSuperclass())
                        .getActualTypeArguments()[0];
        typeName = type.getSimpleName();
    }

    /**
     * Provide the {@link java.net.URI} this Service uses for queries.
     *
     * @return The URI used by this Service
     */
    public URI getUri() {
        return webResource.getURI();
    }

    /**
     * Retrieve the Resource of the given Type with the given id. If no resource with the given id can be found, an
     * {@link org.osiam.client.exception.NoResultException} is thrown.
     *
     * @param id          the uuid from the wanted resource
     * @param accessToken the access token from OSIAM for the actual session
     * @return The resource of the given type with the given id
     * @throws org.osiam.client.exception.UnauthorizedException if the request could not be authorized.
     * @throws org.osiam.client.exception.NoResultException     if no user with the given id can be found
     * @throws org.osiam.client.exception.ConnectionInitializationException
     *                               if the connection to the given OSIAM service could be initialized
     */
    protected T getResource(UUID id, AccessToken accessToken) {
        final T resource;

        if (id == null) { // NOSONAR - false-positive from clover; if-expression is correct
            throw new IllegalArgumentException("The given id can't be null.");
        }

        if (accessToken == null) { // NOSONAR - false-positive from clover; if-expression is correct
            throw new IllegalArgumentException("The given accessToken can't be null."); // NOSONAR - false-positive from clover; simple if, it's ok if message occurs several times
        }

        try {
            // TODO: httpClient as instance member
            DefaultHttpClient httpclient = new DefaultHttpClient();

            HttpGet realWebResource = createRealWebResource(accessToken);
            realWebResource.setURI(new URI(webResource.getURI() + "/" + id.toString()));

            HttpResponse response = httpclient.execute(realWebResource);
            int httpStatus = response.getStatusLine().getStatusCode();

            if (httpStatus != SC_OK) { // NOSONAR - false-positive from clover; if-expression is correct
                String errorMessage;
                switch (httpStatus) {
                    case SC_UNAUTHORIZED:
                        errorMessage = getErrorMessageUnauthorized(response);
                        throw new UnauthorizedException(errorMessage);
                    case SC_NOT_FOUND:
                        errorMessage = getErrorMessage(response, "No " + typeName + " with given UUID " + id);
                        throw new NoResultException(errorMessage);
                    default:
                        errorMessage = getErrorMessageDefault(response, httpStatus);
                        throw new ConnectionInitializationException(errorMessage);
                }
            }

            InputStream content = response.getEntity().getContent();
            resource = mapSingleResourceResponse(content);

            return resource;
        } catch (IOException | URISyntaxException e) {
            throw new ConnectionInitializationException("Unable to setup connection", e); // NOSONAR - its ok to have this message several times
        }
    }

    protected QueryResult<T> getAllResources(AccessToken accessToken) {
        return searchResources("", accessToken);
    }

    protected QueryResult<T> searchResources(String queryString, AccessToken accessToken) {
        final InputStream queryResult;

        if (accessToken == null) { // NOSONAR - false-positive from clover; if-expression is correct
            throw new IllegalArgumentException("The given accessToken can't be null.");  // NOSONAR - false-positive from clover; it's ok if message occurs several times
        }

        try {
            // TODO: httpClient as instance member
            DefaultHttpClient httpclient = new DefaultHttpClient();

            HttpGet realWebResource = createRealWebResource(accessToken);
            realWebResource.setURI(new URI(webResource.getURI() + (queryString.isEmpty() ? "" : "?" + queryString))); // NOSONAR - false-positive from clover; if-expression is correct

            HttpResponse response = httpclient.execute(realWebResource);
            int httpStatus = response.getStatusLine().getStatusCode();

            if (httpStatus != SC_OK) { // NOSONAR - false-positive from clover; if-expression is correct
                String errorMessage;
                switch (httpStatus) {
                    case SC_UNAUTHORIZED:
                        errorMessage = getErrorMessageUnauthorized(response);
                        throw new UnauthorizedException(errorMessage);
                    default:
                        errorMessage = getErrorMessageDefault(response, httpStatus);
                        throw new ConnectionInitializationException(errorMessage);
                }
            }

            queryResult = response.getEntity().getContent();

            final QueryResult<T> result;
            JavaType queryResultType = TypeFactory.defaultInstance().constructParametricType(QueryResult.class, type);

            try {
                result = mapper.readValue(queryResult, queryResultType);
            } catch (IOException e) {
                throw new ConnectionInitializationException("Unable to deserialize query result", e);
            }
            return result;

        } catch (IOException | URISyntaxException e) {
            throw new ConnectionInitializationException("Unable to setup connection", e); // NOSONAR - its ok to have this message several times
        }

    }

    protected QueryResult<T> searchResources(Query query, AccessToken accessToken) {
        if (query == null) { // NOSONAR - false-positive from clover; if-expression is correct
            throw new IllegalArgumentException("The given queryBuilder can't be null.");
        }
        return searchResources(query.toString(), accessToken);
    }

    protected T mapSingleResourceResponse(InputStream content) throws IOException {
	    return mapper.readValue(content, type);
    }

    protected String getErrorMessageUnauthorized(HttpResponse httpResponse) throws IOException {
        return getErrorMessage(httpResponse, "You are not authorized to access OSIAM. Please make sure your access token is valid");
    }

    protected String getErrorMessageDefault(HttpResponse httpResponse, int httpStatus) throws IOException {
        return getErrorMessage(httpResponse, String.format("Unable to setup connection (HTTP Status Code: %d)", httpStatus));
    }


    protected String getErrorMessage(HttpResponse httpResponse, String defaultErrorMessage) throws IOException {
        InputStream content = httpResponse.getEntity().getContent();
        String errorMessage;
        try{
            OsiamErrorMessage error = mapper.readValue(content, OsiamErrorMessage.class);
            errorMessage = error.getDescription();
        } catch (Exception e){    // NOSONAR - we catch everything
            errorMessage = defaultErrorMessage;
        }
        return errorMessage;
    }

    protected HttpGet createRealWebResource(AccessToken accessToken) {
        HttpGet realWebResource;
        try {
            realWebResource = (HttpGet) webResource.clone();
            realWebResource.addHeader("Authorization", "Bearer " + accessToken.getToken());
            return realWebResource;
        } catch (CloneNotSupportedException ignore) {
            // safe to ignore - HttpGet implements Cloneable!
            throw new RuntimeException("This should not happen!"); // NOSONAR - this exception will never be thrown
        }
    }

    protected void deleteResource(UUID id, AccessToken accessToken) {

        if (id == null) { // NOSONAR - false-positive from clover; if-expression is correct
            throw new IllegalArgumentException("The given id can't be null.");
        }

        if (accessToken == null) { // NOSONAR - false-positive from clover; if-expression is correct
            throw new IllegalArgumentException("The given accessToken can't be null.");  // NOSONAR - false-positive from clover; it's ok if message occurs several times
        }

        try {
            // TODO: httpClient as instance member
            DefaultHttpClient httpclient = new DefaultHttpClient();

            URI uri = new URI(webResource.getURI() + "/" + id.toString());

            HttpDelete realWebResource = new HttpDelete(uri);
            realWebResource.addHeader("Authorization", "Bearer " + accessToken.getToken());

            HttpResponse response = httpclient.execute(realWebResource);
            int httpStatus = response.getStatusLine().getStatusCode();

            if (httpStatus != SC_OK) { // NOSONAR - false-positive from clover; if-expression is correct
                String errorMessage;
                switch (httpStatus) {
                    case SC_UNAUTHORIZED:
                        errorMessage = getErrorMessageUnauthorized(response);
                        throw new UnauthorizedException(errorMessage);
                    case SC_NOT_FOUND:
                        errorMessage = getErrorMessage(response, "No " + typeName + " with given UUID " + id);
                        throw new NoResultException(errorMessage);
                    case  SC_CONFLICT:
                        errorMessage = getErrorMessage(response, "Unable to save: " + response.getStatusLine().getReasonPhrase());
                        throw new ConflictException(errorMessage);
                    default:
                        errorMessage = getErrorMessageDefault(response, httpStatus);
                        throw new ConnectionInitializationException(errorMessage);
                }
            }

        } catch (IOException | URISyntaxException e) {
            throw new ConnectionInitializationException("Unable to setup connection", e);  // NOSONAR - its ok to have this message several times
        }
    }

    protected T createResource(T resource , AccessToken accessToken) {
        final T returnResource;

        if (resource == null) { // NOSONAR - false-positive from clover; if-expression is correct
            throw new IllegalArgumentException("The given resource can't be null.");
        }

        if (accessToken == null) { // NOSONAR - false-positive from clover; if-expression is correct
            throw new IllegalArgumentException("The given accessToken can't be null."); // NOSONAR - false-positive from clover; it's ok if message occurs several times
        }

        try {
            // TODO: httpClient as instance member
            DefaultHttpClient httpclient = new DefaultHttpClient();

            HttpPost realWebResource = new HttpPost(webResource.getURI());
            realWebResource.addHeader("Authorization", "Bearer " + accessToken.getToken());

            ObjectMapper mapper = new ObjectMapper();

            mapper.configure( SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false );

            String userAsString = mapper.writeValueAsString(resource);

            realWebResource.setEntity(new StringEntity(userAsString,
                    ContentType.create("application/json")));

            HttpResponse response = httpclient.execute(realWebResource);
            int httpStatus = response.getStatusLine().getStatusCode();

            if (httpStatus != SC_CREATED) { // NOSONAR - false-positive from clover; if-expression is correct
                String errorMessage;
                switch (httpStatus) {
                    case SC_UNAUTHORIZED:
                        errorMessage = getErrorMessageUnauthorized(response);
                        throw new UnauthorizedException(errorMessage);
                    case  SC_CONFLICT:
                        errorMessage = getErrorMessage(response, "Unable to save");
                        throw new ConflictException(errorMessage);
                    default:
                        errorMessage = getErrorMessageDefault(response, httpStatus);
                        throw new ConnectionInitializationException(errorMessage);
                }
            }

            InputStream content = response.getEntity().getContent();
            returnResource = mapSingleResourceResponse(content);

            return returnResource;
        } catch (IOException e) {
            throw new ConnectionInitializationException("Unable to setup connection", e); // NOSONAR - its ok to have this message several times
        }
    }

    protected T updateResource(UUID id, T resource , AccessToken accessToken){
        final T returnResource;
          try      {
        if (resource == null) { // NOSONAR - false-positive from clover; if-expression is correct
            throw new IllegalArgumentException("The given resource can't be null.");
        }

        if (accessToken == null) { // NOSONAR - false-positive from clover; if-expression is correct
            throw new IllegalArgumentException("The given accessToken can't be null."); // NOSONAR - false-positive from clover; it's ok if message occurs several times
        }

        if (id == null) { // NOSONAR - false-positive from clover; if-expression is correct
            throw new IllegalArgumentException("The id can't be null.");
        }

        try {
            // TODO: httpClient as instance member
            DefaultHttpClient httpclient = new DefaultHttpClient();

            HttpPatch realWebResource = new HttpPatch(webResource.getURI() + "/" + id.toString());
            realWebResource.addHeader("Authorization", "Bearer " + accessToken.getToken());

            ObjectMapper mapper = new ObjectMapper();

            mapper.configure( SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false );

            String userAsString = mapper.writeValueAsString(resource);

            realWebResource.setEntity(new StringEntity(userAsString,
                    ContentType.create("application/json")));

            HttpResponse response = httpclient.execute(realWebResource);
            int httpStatus = response.getStatusLine().getStatusCode();

            if (httpStatus != SC_OK) { // NOSONAR - false-positive from clover; if-expression is correct
                String errorMessage;
                switch (httpStatus) {
                    case SC_UNAUTHORIZED:
                        errorMessage = getErrorMessageUnauthorized(response);
                        throw new UnauthorizedException(errorMessage);
                    case  SC_BAD_REQUEST:
                        errorMessage = getErrorMessage(response, "Unable to update");
                        throw new ConflictException(errorMessage);
                    default:
                        errorMessage = getErrorMessageDefault(response, httpStatus);
                        throw new ConnectionInitializationException(errorMessage);
                }
            }

            InputStream content = response.getEntity().getContent();
            returnResource = mapSingleResourceResponse(content);

            return returnResource;
        } catch (IOException e) {
            throw new ConnectionInitializationException("Unable to setup connection", e); // NOSONAR - its ok to have this message several times
        }
    }      catch (Exception e){
              throw  new RuntimeException("FEHLER::: " + e.getMessage());
          }
    }

    /**
     * The Builder class is used to prove a WebResource to build the needed Service
     *
     * @param <T> a org.osiam.resources.scim.User or a org.osiam.resources.scim.Group
     */
    protected static class Builder<T> {
        private String endpoint;
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
         * creates a WebResource to the needed endpoint
         *
         * @return The webresource for the type of OSIAM-service
         */
        protected HttpGet getWebResource() {
            HttpGet webResource;
            try {
                webResource = new HttpGet(new URI(endpoint + "/" + typeName + "s"));
                webResource.addHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());
            } catch (URISyntaxException e) {
                throw new ConnectionInitializationException("Unable to setup connection " + endpoint +
                        "is not a valid URI.", e);
            }
            return webResource;
        }
    }
}
