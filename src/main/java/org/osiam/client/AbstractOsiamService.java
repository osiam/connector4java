/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.client;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.osiam.client.exception.*;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.resources.helper.UserDeserializer;
import org.osiam.resources.scim.Resource;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.User;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.apache.http.HttpStatus.*;

/**
 * AbstractOsiamService provides all basic methods necessary to manipulate the Entities registered in the given OSIAM
 * installation. For the construction of an instance please use the included {@link AbstractOsiamService.Builder}
 */
abstract class AbstractOsiamService<T extends Resource> {

    private static final String CONNECTION_SETUP_ERROR_STRING = "Cannot connect to server";
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
        SimpleModule userDeserializerModule = new SimpleModule("userDeserializerModule", Version.unknownVersion())
                .addDeserializer(User.class, new UserDeserializer(User.class));
        mapper.registerModule(userDeserializerModule);

        contentType = ContentType.create("application/json");
        webResource = builder.getWebResource();
        type = (Class<T>)
                ((ParameterizedType) getClass().getGenericSuperclass())
                        .getActualTypeArguments()[0];
        typeName = type.getSimpleName();
        endpoint = builder.endpoint;
    }

    protected T getResource(String id, AccessToken accessToken) {
        ensureReferenceIsNotNull(id, "The given id can't be null.");
        ensureAccessTokenIsNotNull(accessToken);
        httpclient = new DefaultHttpClient();

        HttpGet realWebResource = createRealWebResource(accessToken);
        HttpResponse response;
        try {
            realWebResource.setURI(new URI(webResource.getURI() + "/" + id));

            response = httpclient.execute(realWebResource);
        } catch (IOException | URISyntaxException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }
        int httpStatus = response.getStatusLine().getStatusCode();

        if (httpStatus != SC_OK) {
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
                throw new OsiamRequestException(httpStatus, errorMessage);
            }
        }

        try (InputStream content = response.getEntity().getContent()) {
            return mapSingleResourceResponse(content);

        } catch (IOException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }
    }

    protected List<T> getAllResources(AccessToken accessToken) {
        return searchResources("count=" + Integer.MAX_VALUE, accessToken).getResources();
    }

    protected SCIMSearchResult<T> searchResources(String queryString, AccessToken accessToken) {
        ensureAccessTokenIsNotNull(accessToken);

        httpclient = new DefaultHttpClient();

        HttpGet realWebResource = createRealWebResource(accessToken);
        HttpResponse response;
        try {
            URI uri = new URI(webResource.getURI() + (queryString.isEmpty() ? "" : "?" + queryString));
            realWebResource.setURI(uri);
            response = httpclient.execute(realWebResource);
        } catch (IOException | URISyntaxException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        int httpStatus = response.getStatusLine().getStatusCode();

        if (httpStatus != SC_OK) {
            String errorMessage;
            switch (httpStatus) {
                case SC_UNAUTHORIZED:
                    errorMessage = getErrorMessageUnauthorized(response);
                    throw new UnauthorizedException(errorMessage);
                case SC_FORBIDDEN:
                    errorMessage = getErrorMessageForbidden(accessToken, "get");
                    throw new ForbiddenException(errorMessage);
                case SC_CONFLICT:
                    errorMessage = getErrorMessage(response, "Unable to search with the search string '" + queryString + "': "
                            + response.getStatusLine().getReasonPhrase());
                    throw new ConflictException(errorMessage);
                default:
                    errorMessage = getErrorMessageDefault(response, httpStatus);
                    throw new OsiamRequestException(httpStatus, errorMessage);
            }
        }

        try (InputStream queryResult = response.getEntity().getContent()) {
            JavaType queryResultType = TypeFactory.defaultInstance().constructParametricType(SCIMSearchResult.class,
                    type);

            return mapper.readValue(queryResult, queryResultType);
        } catch (IOException e) {
            throw new ConnectionInitializationException("Unable to deserialize query result", e);
        }
    }

    protected SCIMSearchResult<T> searchResources(Query query, AccessToken accessToken) {
        if (query == null) {
            throw new IllegalArgumentException("The given queryBuilder can't be null.");
        }
        return searchResources(query.toString(), accessToken);
    }

    protected void deleteResource(String id, AccessToken accessToken) {
        ensureReferenceIsNotNull(id, "The given id can't be null.");
        ensureAccessTokenIsNotNull(accessToken);
        httpclient = new DefaultHttpClient();

        URI uri;
        HttpResponse response;
        try {
            uri = new URI(webResource.getURI() + "/" + id);
            HttpDelete realWebResource = new HttpDelete(uri);
            realWebResource.addHeader(AUTHORIZATION, BEARER + accessToken.getToken());
            response = httpclient.execute(realWebResource);
        } catch (URISyntaxException | IOException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        int httpStatus = response.getStatusLine().getStatusCode();

        if (httpStatus != SC_OK) {
            String errorMessage;
            switch (httpStatus) {
            case SC_UNAUTHORIZED:
                errorMessage = getErrorMessageUnauthorized(response);
                throw new UnauthorizedException(errorMessage);
            case SC_NOT_FOUND:
                errorMessage = getErrorMessage(response, "No " + typeName + " with given id " + id);
                throw new NoResultException(errorMessage);
            case SC_CONFLICT:
                errorMessage = getErrorMessage(response, "Unable to delete: "
                        + response.getStatusLine().getReasonPhrase());
                throw new ConflictException(errorMessage);
            case SC_FORBIDDEN:
                errorMessage = getErrorMessageForbidden(accessToken, "delete");
                throw new ForbiddenException(errorMessage);
            default:
                errorMessage = getErrorMessageDefault(response, httpStatus);
                throw new OsiamRequestException(httpStatus, errorMessage);
            }
        }
    }

    protected T createResource(T resource, AccessToken accessToken) {
        ensureReferenceIsNotNull(resource, "The given " + typeName + " can't be null.");
        ensureAccessTokenIsNotNull(accessToken);

        HttpPost realWebResource = new HttpPost(webResource.getURI());
        realWebResource.addHeader(AUTHORIZATION, BEARER + accessToken.getToken());

        httpclient = new DefaultHttpClient();

        HttpResponse response;
        try {
            String userAsString = mapper.writeValueAsString(resource);
            realWebResource.setEntity(new StringEntity(userAsString, contentType));
            response = httpclient.execute(realWebResource);
        } catch (IOException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        int httpStatus = response.getStatusLine().getStatusCode();

        if (httpStatus != SC_CREATED) {
            String errorMessage;
            switch (httpStatus) {
            case SC_UNAUTHORIZED:
                errorMessage = getErrorMessageUnauthorized(response);
                throw new UnauthorizedException(errorMessage);
            case SC_CONFLICT:
                errorMessage = getErrorMessage(response, "Unable to save");
                throw new ConflictException(errorMessage);
            case SC_FORBIDDEN:
                errorMessage = getErrorMessageForbidden(accessToken, "create");
                throw new ForbiddenException(errorMessage);
            default:
                errorMessage = getErrorMessageDefault(response, httpStatus);
                throw new OsiamRequestException(httpStatus, errorMessage);
            }
        }

        try (InputStream content = response.getEntity().getContent()) {
            return mapSingleResourceResponse(content);
        } catch (IOException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }
    }

    protected T updateResource(String id, T resource, AccessToken accessToken) {
        HttpPatch realWebResource = new HttpPatch(webResource.getURI() + "/" + id);
        return modifyResource(id, resource, accessToken, realWebResource);
    }

    protected T replaceResource(String id, T resource, AccessToken accessToken) {
        HttpPut realWebResource = new HttpPut(webResource.getURI() + "/" + id);
        return modifyResource(id, resource, accessToken, realWebResource);
    }

    private T modifyResource(String id, T resource, AccessToken accessToken,
            HttpEntityEnclosingRequestBase realWebResource) {
        ensureReferenceIsNotNull(resource, "The given " + typeName + " can't be null.");
        ensureAccessTokenIsNotNull(accessToken);
        ensureReferenceIsNotNull(id, "The given id can't be null.");

        realWebResource.addHeader(AUTHORIZATION, BEARER + accessToken.getToken());
        httpclient = new DefaultHttpClient();

        HttpResponse response;
        String userAsString;
        try {
            userAsString = mapper.writeValueAsString(resource);
            realWebResource.setEntity(new StringEntity(userAsString, contentType));
            response = httpclient.execute(realWebResource);
        } catch (IOException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        int httpStatus = response.getStatusLine().getStatusCode();

        if (httpStatus != SC_OK) {
            String errorMessage;
            switch (httpStatus) {
            case SC_UNAUTHORIZED:
                errorMessage = getErrorMessageUnauthorized(response);
                throw new UnauthorizedException(errorMessage);
            case SC_BAD_REQUEST:
                errorMessage = getErrorMessage(response, "Wrong " + typeName + ". Unable to update");
                throw new ConflictException(errorMessage);
            case SC_CONFLICT:
                errorMessage = getErrorMessage(response, typeName + " with Conflicts. Unable to update");
                throw new ConflictException(errorMessage);
            case SC_NOT_FOUND:
                errorMessage = getErrorMessage(response, "A " + typeName + " with the id " + id
                        + " could be found to be updated.");
                throw new NoResultException(errorMessage);
            case SC_FORBIDDEN:
                errorMessage = getErrorMessageForbidden(accessToken, "update");
                throw new ForbiddenException(errorMessage);
            default:
                errorMessage = getErrorMessageDefault(response, httpStatus);
                throw new OsiamRequestException(httpStatus, errorMessage);
            }
        }

        try (InputStream content = response.getEntity().getContent()) {
            return mapSingleResourceResponse(content);
        } catch (IOException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }
    }

    private void ensureAccessTokenIsNotNull(AccessToken accessToken) {
        ensureReferenceIsNotNull(accessToken, "The given accessToken can't be null.");
    }

    private void ensureReferenceIsNotNull(Object reference, String message) {
        if (reference == null) {
            throw new IllegalArgumentException(message);
        }
    }

    protected String getEndpoint() {
        return endpoint;
    }

    private String getErrorMessageForbidden(AccessToken accessToken, String process) {
        return "Insufficient scope (" + accessToken.getScope() + ") to " + process + " this " + typeName + ".";
    }

    private String getErrorMessageUnauthorized(HttpResponse httpResponse) {
        return getErrorMessage(httpResponse,
                "You are not authorized to access OSIAM. Please make sure your access token is valid");
    }

    private String getErrorMessageDefault(HttpResponse httpResponse, int httpStatus) {
        return getErrorMessage(httpResponse,
                String.format("Unable to setup connection (HTTP Status Code: %d)", httpStatus));
    }

    protected String getErrorMessage(HttpResponse httpResponse, String defaultErrorMessage) {
        String errorMessage;
        try (InputStream content = httpResponse.getEntity().getContent()) {
            OsiamErrorMessage error = mapper.readValue(content, OsiamErrorMessage.class);
            errorMessage = error.getDescription();
        } catch (Exception e) { // NOSONAR - we catch everything
            errorMessage = defaultErrorMessage;
        }
        if (errorMessage == null) {
            errorMessage = defaultErrorMessage;
        }
        return errorMessage;
    }

    protected T mapSingleResourceResponse(InputStream content) throws IOException {
        return mapper.readValue(content, type);
    }

    protected HttpGet createRealWebResource(AccessToken accessToken) {
        HttpGet realWebResource;
        try {
            realWebResource = (HttpGet) webResource.clone();
            realWebResource.addHeader(AUTHORIZATION, BEARER + accessToken.getToken());
            return realWebResource;
        } catch (CloneNotSupportedException ignore) {
            // safe to ignore - HttpGet implements Cloneable!
            throw new RuntimeException("This should not happen!"); // NOSONAR - this exception should never be thrown
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