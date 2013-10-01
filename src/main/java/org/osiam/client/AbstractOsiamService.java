package org.osiam.client;
/*
 * for licensing see the file license.txt.
 */

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.osiam.client.exception.ConflictException;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.ForbiddenException;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.NotFoundException;
import org.osiam.client.exception.OsiamErrorMessage;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryResult;
import org.osiam.resources.scim.CoreResource;

/**
 * AbstractOsiamService provides all basic methods necessary to manipulate the Entities registered in the
 * given OSIAM installation. For the construction of an instance please use the included {@link AbstractOsiamService.Builder}
 */
abstract class AbstractOsiamService<T extends CoreResource> {

    private HttpGet webResource;
    private Class<T> type;
    private String typeName;
    private ObjectMapper mapper;
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private DefaultHttpClient httpclient;
    private ContentType contentType;
    private String endpoint;

    @SuppressWarnings("unchecked")
    protected AbstractOsiamService(@SuppressWarnings("rawtypes") Builder builder) {
        mapper = new ObjectMapper();
        contentType = ContentType.create("application/json");
        webResource = builder.getWebResource();
        type = (Class<T>)
                ((ParameterizedType) getClass().getGenericSuperclass())
                        .getActualTypeArguments()[0];
        typeName = type.getSimpleName();
        endpoint = builder.endpoint;
    }

    protected String getEndpoint(){
    	return endpoint;
    }
   
    protected T getResource(String id, AccessToken accessToken) {
        ensureIdIsNotNull(id);
        ensureAccessTokenIsNotNull(accessToken);

        final T resource;
        InputStream content = null;
        try {
            HttpGet realWebResource = createRealWebResource(accessToken);
            realWebResource.setURI(new URI(webResource.getURI() + "/" + id));

            httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(realWebResource);
            int httpStatus = response.getStatusLine().getStatusCode();

            if (httpStatus != SC_OK) { // NOSONAR - false-positive from clover; if-expression is correct
                String errorMessage;
                switch (httpStatus) {
                    case SC_UNAUTHORIZED:
                        errorMessage = getErrorMessageUnauthorized(response);
                        throw new UnauthorizedException(errorMessage);
                    case SC_NOT_FOUND:
                        errorMessage = getErrorMessage(response, "No " + typeName + " with given id " + id);
                        throw new NoResultException(errorMessage);
                    case SC_FORBIDDEN:
                    	errorMessage = getErrorMessageForbidden(accessToken, "get");
                        throw new ForbiddenException(errorMessage);
                    default:
                        errorMessage = getErrorMessageDefault(response, httpStatus);
                        throw new ConnectionInitializationException(errorMessage);
                }
            }

            content = response.getEntity().getContent();
            resource = mapSingleResourceResponse(content);

            return resource;
        } catch (IOException | URISyntaxException e) {
            throw new ConnectionInitializationException("Unable to setup connection", e); // NOSONAR - its ok to have this message several times
        }finally{
        	try {
				content.close();
			} catch (Exception ignore) {/* if fails we don't care */}
        }
    }

    protected List<T> getAllResources(AccessToken accessToken) {
        return searchResources("count=" + Integer.MAX_VALUE, accessToken).getResources();
    }

    protected QueryResult<T> searchResources(String queryString, AccessToken accessToken) {
        ensureAccessTokenIsNotNull(accessToken);
        
        final InputStream queryResult;
        try {
            HttpGet realWebResource = createRealWebResource(accessToken);
            realWebResource.setURI(new URI(webResource.getURI() + (queryString.isEmpty() ? "" : "?" + queryString))); // NOSONAR - false-positive from clover; if-expression is correct

            httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(realWebResource);
            int httpStatus = response.getStatusLine().getStatusCode();

            if (httpStatus != SC_OK) { // NOSONAR - false-positive from clover; if-expression is correct
                String errorMessage;
                switch (httpStatus) {
                    case SC_UNAUTHORIZED:
                        errorMessage = getErrorMessageUnauthorized(response);
                        throw new UnauthorizedException(errorMessage);
                    case SC_FORBIDDEN:
                    	errorMessage = getErrorMessageForbidden(accessToken, "get");
                        throw new ForbiddenException(errorMessage);
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

    protected String getErrorMessageForbidden(AccessToken accessToken, String process){
    	return "Insufficient scope (" + accessToken.getScope() + ") to " + process + " this " + typeName + ".";
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
        }finally{
        	content.close();
        }
        if(errorMessage == null){// NOSONAR - false-positive from clover; if-expression is correct
            errorMessage = defaultErrorMessage;
        }
        return errorMessage;
    }

    protected HttpGet createRealWebResource(AccessToken accessToken) {
        HttpGet realWebResource;
        try {
            realWebResource = (HttpGet) webResource.clone();
            realWebResource.addHeader(AUTHORIZATION, BEARER + accessToken.getToken());
            return realWebResource;
        } catch (CloneNotSupportedException ignore) {
            // safe to ignore - HttpGet implements Cloneable!
            throw new RuntimeException("This should not happen!"); // NOSONAR - this exception will never be thrown
        }
    }

    protected void deleteResource(String id, AccessToken accessToken) {
        ensureIdIsNotNull(id);
        ensureAccessTokenIsNotNull(accessToken);

        try {
            URI uri = new URI(webResource.getURI() + "/" + id);

            HttpDelete realWebResource = new HttpDelete(uri);
            realWebResource.addHeader(AUTHORIZATION, BEARER + accessToken.getToken());

            httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(realWebResource);
            int httpStatus = response.getStatusLine().getStatusCode();

            if (httpStatus != SC_OK) { // NOSONAR - false-positive from clover; if-expression is correct
                String errorMessage;
                switch (httpStatus) {
                    case SC_UNAUTHORIZED:
                        errorMessage = getErrorMessageUnauthorized(response);
                        throw new UnauthorizedException(errorMessage);
                    case SC_NOT_FOUND:
                        errorMessage = getErrorMessage(response, "No " + typeName + " with given id " + id);
                        throw new NoResultException(errorMessage);
                    case  SC_CONFLICT:
                        errorMessage = getErrorMessage(response, "Unable to delete: " + response.getStatusLine().getReasonPhrase());
                        throw new ConflictException(errorMessage);
                    case SC_FORBIDDEN:
                    	errorMessage = getErrorMessageForbidden(accessToken, "delete");
                        throw new ForbiddenException(errorMessage);
                    default:
                        errorMessage = getErrorMessageDefault(response, httpStatus);
                        throw new ConnectionInitializationException(errorMessage);
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw new ConnectionInitializationException("Unable to setup connection", e);  // NOSONAR - its ok to have this message several times
        }
    }

    protected T createResource(T resource, AccessToken accessToken) {
        ensureResourceIsNotNull(resource);
        ensureAccessTokenIsNotNull(accessToken);

        final T returnResource;
        InputStream content = null;
        try {
            HttpPost realWebResource = new HttpPost(webResource.getURI());
            realWebResource.addHeader(AUTHORIZATION, BEARER + accessToken.getToken());

            String userAsString = mapper.writeValueAsString(resource);

            realWebResource.setEntity(new StringEntity(userAsString, contentType));

            httpclient = new DefaultHttpClient();
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
                    case SC_FORBIDDEN:
                    	errorMessage = getErrorMessageForbidden(accessToken, "create");
                        throw new ForbiddenException(errorMessage);
                    default:
                        errorMessage = getErrorMessageDefault(response, httpStatus);
                        throw new ConnectionInitializationException(errorMessage);
                }
            }

            content = response.getEntity().getContent();
            returnResource = mapSingleResourceResponse(content);

            return returnResource;
        } catch (IOException e) {
            throw new ConnectionInitializationException("Unable to setup connection", e); // NOSONAR - its ok to have this message several times
        }finally{
        	try {
				content.close();
			} catch (Exception ignore) {/* if fails we don't care */}
        }
    }

    protected T updateResource(String id, T resource , AccessToken accessToken){
        ensureResourceIsNotNull(resource);
        ensureAccessTokenIsNotNull(accessToken);
    	ensureIdIsNotNull(id);
        
        final T returnResource;
        InputStream content = null;
        try {
            HttpPatch realWebResource = new HttpPatch(webResource.getURI() + "/" + id);
            realWebResource.addHeader(AUTHORIZATION, BEARER + accessToken.getToken());

            String userAsString = mapper.writeValueAsString(resource);
            realWebResource.setEntity(new StringEntity(userAsString, contentType));
            httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(realWebResource);
            int httpStatus = response.getStatusLine().getStatusCode();

            if (httpStatus != SC_OK) { // NOSONAR - false-positive from clover; if-expression is correct
                String errorMessage;
                switch (httpStatus) {
                    case SC_UNAUTHORIZED:
                        errorMessage = getErrorMessageUnauthorized(response);
                        throw new UnauthorizedException(errorMessage);
                    case  SC_BAD_REQUEST:
                        errorMessage = getErrorMessage(response, "Wrong " + typeName + ". Unable to update");
                        throw new ConflictException(errorMessage);
                    case  SC_CONFLICT:
                        errorMessage = getErrorMessage(response, typeName + " with Conflicts. Unable to update");
                        throw new ConflictException(errorMessage);
                    case  SC_NOT_FOUND:
                        errorMessage = getErrorMessage(response, "A " + typeName + " with the id " + id + " could be found to be updated.");
                        throw new NotFoundException(errorMessage);
                    case SC_FORBIDDEN:
                    	errorMessage = getErrorMessageForbidden(accessToken, "update");
                        throw new ForbiddenException(errorMessage);
                    default:
                        errorMessage = getErrorMessageDefault(response, httpStatus);
                        throw new ConnectionInitializationException(errorMessage);
                }
            }

            content = response.getEntity().getContent();
            returnResource = mapSingleResourceResponse(content);

            return returnResource;
        } catch (IOException e) {
            throw new ConnectionInitializationException("Unable to setup connection", e); // NOSONAR - its ok to have this message several times
        }finally{
        	try {
				content.close();
			} catch (Exception ignore) {/* if fails we don't care */}
        }
    }
    
    private void ensureResourceIsNotNull(T resource){
        if (resource == null) { // NOSONAR - false-positive from clover; if-expression is correct
            throw new IllegalArgumentException("The given " + typeName + " can't be null.");
        }
    }
    
    private void ensureAccessTokenIsNotNull(AccessToken accessToken){
        if (accessToken == null) { // NOSONAR - false-positive from clover; if-expression is correct
            throw new IllegalArgumentException("The given accessToken can't be null."); // NOSONAR - false-positive from clover; it's ok if message occurs several times
        }
    }
    
    private void ensureIdIsNotNull(String id){
        if (id == null) { // NOSONAR - false-positive from clover; if-expression is correct
            throw new IllegalArgumentException("The given id can't be null.");
        }
    }

    protected static class Builder<T> {
        private String endpoint;
        private Class<T> type;
        private String typeName;

        @SuppressWarnings("unchecked")
        protected Builder(String endpoint) {
            this.endpoint = endpoint;
            this.type = (Class<T>)
                    ((ParameterizedType) getClass().getGenericSuperclass())
                            .getActualTypeArguments()[0];
            typeName = type.getSimpleName();
        }

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
