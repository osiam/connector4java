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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.osiam.client.exception.ConflictException;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.ForbiddenException;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.NotFoundException;
import org.osiam.client.exception.OsiamErrorMessage;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.resources.helper.UserDeserializer;
import org.osiam.resources.scim.CoreResource;
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
        SimpleModule userDeserializerModule = new SimpleModule("userDeserializerModule", new Version(1, 0, 0, null))
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

    public URI getUri() {
        return webResource.getURI();
    }

    protected String getEndpoint() {
        return endpoint;
    }

    protected T getResource(String id, AccessToken accessToken) {
        ensureReferenceIsNotNull(id, "The given id can't be null.");
        ensureAccessTokenIsNotNull(accessToken);

        final T resource;
        InputStream content = null;
        try {
            HttpGet realWebResource = createRealWebResource(accessToken);
            realWebResource.setURI(new URI(webResource.getURI() + "/" + id));

            httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(realWebResource);
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
                    throw new ConnectionInitializationException(errorMessage);
                }
            }

            content = response.getEntity().getContent();
            resource = mapSingleResourceResponse(content);

            return resource;
        } catch (IOException | URISyntaxException e) {
            throw createSetupConnectionException(e);
        } finally {
            try {
                content.close();
            } catch (Exception ignore) {/* if fails we don't care */
            }
        }
    }

    protected List<T> getAllResources(AccessToken accessToken) {
        return searchResources("count=" + Integer.MAX_VALUE, accessToken).getResources();
    }

    protected SCIMSearchResult<T> searchResources(String queryString, AccessToken accessToken) {
        ensureAccessTokenIsNotNull(accessToken);

        final InputStream queryResult;
        try {
            HttpGet realWebResource = createRealWebResource(accessToken);
            URI uri = new URI(webResource.getURI() + (queryString.isEmpty() ? "" : "?" + queryString));
            realWebResource.setURI(uri);

            httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(realWebResource);
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
                default:
                    errorMessage = getErrorMessageDefault(response, httpStatus);
                    throw new ConnectionInitializationException(errorMessage);
                }
            }

            queryResult = response.getEntity().getContent();

            final SCIMSearchResult<T> result;
            JavaType queryResultType = TypeFactory.defaultInstance().constructParametricType(SCIMSearchResult.class,
                    type);

            try {
                result = mapper.readValue(queryResult, queryResultType);
            } catch (IOException e) {
                throw new ConnectionInitializationException("Unable to deserialize query result", e);
            }
            return result;

        } catch (IOException | URISyntaxException e) {
            throw createSetupConnectionException(e);
        }
    }

    protected SCIMSearchResult<T> searchResources(Query query, AccessToken accessToken) {
        if (query == null) {
            throw new IllegalArgumentException("The given queryBuilder can't be null.");
        }
        return searchResources(query.toString(), accessToken);
    }

    protected T mapSingleResourceResponse(InputStream content) throws IOException {
        return mapper.readValue(content, type);
    }

    protected String getErrorMessageForbidden(AccessToken accessToken, String process) {
        return "Insufficient scope (" + accessToken.getScope() + ") to " + process + " this " + typeName + ".";
    }

    protected String getErrorMessageUnauthorized(HttpResponse httpResponse) throws IOException {
        return getErrorMessage(httpResponse,
                "You are not authorized to access OSIAM. Please make sure your access token is valid");
    }

    protected String getErrorMessageDefault(HttpResponse httpResponse, int httpStatus) throws IOException {
        return getErrorMessage(httpResponse,
                String.format("Unable to setup connection (HTTP Status Code: %d)", httpStatus));
    }

    protected String getErrorMessage(HttpResponse httpResponse, String defaultErrorMessage) throws IOException {
        InputStream content = httpResponse.getEntity().getContent();
        String errorMessage;
        try {
            OsiamErrorMessage error = mapper.readValue(content, OsiamErrorMessage.class);
            errorMessage = error.getDescription();
        } catch (Exception e) { // NOSONAR - we catch everything
            errorMessage = defaultErrorMessage;
        } finally {
            content.close();
        }
        if (errorMessage == null) {
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
            throw new RuntimeException("This should not happen!"); // NOSONAR - this exception should never be thrown
        }
    }

    protected void deleteResource(String id, AccessToken accessToken) {
        ensureReferenceIsNotNull(id, "The given id can't be null.");
        ensureAccessTokenIsNotNull(accessToken);

        try {
            URI uri = new URI(webResource.getURI() + "/" + id);

            HttpDelete realWebResource = new HttpDelete(uri);
            realWebResource.addHeader(AUTHORIZATION, BEARER + accessToken.getToken());

            httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(realWebResource);
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
                    throw new ConnectionInitializationException(errorMessage);
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw createSetupConnectionException(e);
        }
    }

    protected T createResource(T resource, AccessToken accessToken) {
        ensureReferenceIsNotNull(resource, "The given " + typeName + " can't be null.");
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
                    throw new ConnectionInitializationException(errorMessage);
                }
            }

            content = response.getEntity().getContent();
            returnResource = mapSingleResourceResponse(content);

            return returnResource;
        } catch (IOException e) {
            throw createSetupConnectionException(e);
        } finally {
            try {
                content.close();
            } catch (Exception ignore) {/* if fails we don't care */
            }
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

        final T returnResource;
        InputStream content = null;

        try {
            realWebResource.addHeader(AUTHORIZATION, BEARER + accessToken.getToken());

            String userAsString = mapper.writeValueAsString(resource);
            realWebResource.setEntity(new StringEntity(userAsString, contentType));
            httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(realWebResource);
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
            throw createSetupConnectionException(e);
        } finally {
            try {
                content.close();
            } catch (Exception ignore) {
                /* if fails we don't care */
            }
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

    private ConnectionInitializationException createSetupConnectionException(Throwable e) {
        return new ConnectionInitializationException("Unable to setup connection", e);
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
