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

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.osiam.client.exception.ConflictException;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.ForbiddenException;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.OsiamRequestException;
import org.osiam.client.exception.ScimErrorMessage;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.resources.helper.UserDeserializer;
import org.osiam.resources.scim.Resource;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.User;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * AbstractOsiamService provides all basic methods necessary to manipulate the Entities registered in the given OSIAM
 * installation. For the construction of an instance please use the included {@link AbstractOsiamService.Builder}
 */
abstract class AbstractOsiamService<T extends Resource> {

    private static final String CONNECTION_SETUP_ERROR_STRING = "Cannot connect to server";
    protected final HttpGet webResource;
    private Class<T> type;
    private String typeName;
    private ObjectMapper mapper;
    protected static final String AUTHORIZATION = "Authorization";
    protected static final String ACCEPT = "Accept";
    protected static final String BEARER = "Bearer ";
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

        HttpResponse response;
        try {
            URI uri = new URI(webResource.getURI() + "/" + id);

            HttpGet realWebResource = new HttpGet(uri);
            realWebResource = addDefaultHeaderToRequest(realWebResource, accessToken);

            response = httpclient.execute(realWebResource);
        } catch (IOException | URISyntaxException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }
        int httpStatus = response.getStatusLine().getStatusCode();

        if (httpStatus != SC_OK) {
            String errorMessage;
            switch (httpStatus) {
            case SC_UNAUTHORIZED:
                errorMessage = getErrorMessage(response);
                throw new UnauthorizedException(errorMessage);
            case SC_NOT_FOUND:
                errorMessage = getErrorMessage(response);
                throw new NoResultException(errorMessage);
            case SC_FORBIDDEN:
                errorMessage = getErrorMessage(response);
                throw new ForbiddenException(errorMessage);
            default:
                errorMessage = getErrorMessage(response);
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

        HttpResponse response;
        try {
            URI uri = new URI(webResource.getURI() + (queryString.isEmpty() ? "" : "?" + queryString));

            HttpGet realWebResource = new HttpGet(uri);
            realWebResource = addDefaultHeaderToRequest(realWebResource, accessToken);

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
                errorMessage = getErrorMessage(response);
                throw new UnauthorizedException(errorMessage);
            case SC_FORBIDDEN:
                errorMessage = getErrorMessage(response);
                throw new ForbiddenException(errorMessage);
            case SC_CONFLICT:
                errorMessage = getErrorMessage(response);
                throw new ConflictException(errorMessage);
            default:
                errorMessage = getErrorMessage(response);
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
            realWebResource = addDefaultHeaderToRequest(realWebResource, accessToken);

            response = httpclient.execute(realWebResource);
        } catch (URISyntaxException | IOException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        int httpStatus = response.getStatusLine().getStatusCode();

        if (httpStatus != SC_OK) {
            String errorMessage;
            switch (httpStatus) {
            case SC_UNAUTHORIZED:
                errorMessage = getErrorMessage(response);
                throw new UnauthorizedException(errorMessage);
            case SC_NOT_FOUND:
                errorMessage = getErrorMessage(response);
                throw new NoResultException(errorMessage);
            case SC_CONFLICT:
                errorMessage = getErrorMessage(response);
                throw new ConflictException(errorMessage);
            case SC_FORBIDDEN:
                errorMessage = getErrorMessage(response);
                throw new ForbiddenException(errorMessage);
            default:
                errorMessage = getErrorMessage(response);
                throw new OsiamRequestException(httpStatus, errorMessage);
            }
        }
    }

    protected T createResource(T resource, AccessToken accessToken) {
        ensureReferenceIsNotNull(resource, "The given " + typeName + " can't be null.");
        ensureAccessTokenIsNotNull(accessToken);

        HttpPost realWebResource = new HttpPost(webResource.getURI());
        realWebResource = addDefaultHeaderToRequest(realWebResource, accessToken);

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
                errorMessage = getErrorMessage(response);
                throw new UnauthorizedException(errorMessage);
            case SC_CONFLICT:
                errorMessage = getErrorMessage(response);
                throw new ConflictException(errorMessage);
            case SC_FORBIDDEN:
                errorMessage = getErrorMessage(response);
                throw new ForbiddenException(errorMessage);
            default:
                errorMessage = getErrorMessage(response);
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

        realWebResource = addDefaultHeaderToRequest(realWebResource, accessToken);
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
                errorMessage = getErrorMessage(response);
                throw new UnauthorizedException(errorMessage);
            case SC_BAD_REQUEST:
                errorMessage = getErrorMessage(response);
                throw new ConflictException(errorMessage);
            case SC_CONFLICT:
                errorMessage = getErrorMessage(response);
                throw new ConflictException(errorMessage);
            case SC_NOT_FOUND:
                errorMessage = getErrorMessage(response);
                throw new NoResultException(errorMessage);
            case SC_FORBIDDEN:
                errorMessage = getErrorMessage(response);
                throw new ForbiddenException(errorMessage);
            default:
                errorMessage = getErrorMessage(response);
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

    protected String getErrorMessage(HttpResponse httpResponse) {
        String errorMessage;
        InputStream content = null;
        String inputStreamStringValue = null;

        try {
            content = httpResponse.getEntity().getContent();
            inputStreamStringValue = IOUtils.toString(content, "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            ScimErrorMessage error = mapper.readValue(inputStreamStringValue, ScimErrorMessage.class);
            errorMessage = error.getDescription();
        } catch (Exception e) { // NOSONAR - we catch everything
            errorMessage = "Could not deserialize the error response for the status code \""
                    + httpResponse.getStatusLine().getReasonPhrase() + "\".";
            if (inputStreamStringValue != null) {
                errorMessage += " Original response: " + inputStreamStringValue;
            }
        }

        return errorMessage;
    }

    protected T mapSingleResourceResponse(InputStream content) throws IOException {
        return mapper.readValue(content, type);
    }

    private <R extends HttpRequestBase> R addDefaultHeaderToRequest(R request, AccessToken accessToken) {
        request.addHeader(AUTHORIZATION, BEARER + accessToken.getToken());
        request.addHeader(ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        return request;
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