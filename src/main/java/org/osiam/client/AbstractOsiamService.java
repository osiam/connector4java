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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.RequestEntityProcessing;
import org.osiam.client.exception.ConflictException;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.ForbiddenException;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.OsiamClientException;
import org.osiam.client.exception.OsiamRequestException;
import org.osiam.client.exception.ScimErrorMessage;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.nquery.Query;
import org.osiam.client.oauth.AccessToken;
import org.osiam.resources.helper.UserDeserializer;
import org.osiam.resources.scim.Resource;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Strings;

/**
 * AbstractOsiamService provides all basic methods necessary to manipulate the
 * Entities registered in the given OSIAM installation. For the construction of
 * an instance please use the included {@link AbstractOsiamService.Builder}
 */
abstract class AbstractOsiamService<T extends Resource> {

    protected static final String CONNECTION_SETUP_ERROR_STRING = "Cannot connect to server";
    private static final Client client = ClientBuilder.newClient(new ClientConfig()
            .connectorProvider(new ApacheConnectorProvider())
            .property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.BUFFERED)
            .property(ClientProperties.CONNECT_TIMEOUT, 2500)
            .property(ClientProperties.READ_TIMEOUT, 5000)
            .property(ApacheClientProperties.CONNECTION_MANAGER, new PoolingHttpClientConnectionManager()));

    protected static final String AUTHORIZATION = "Authorization";
    protected static final String ACCEPT = "Accept";
    protected static final String BEARER = "Bearer ";

    private final HttpGet webResource;
    private final Class<T> type;
    private final String typeName;
    protected final ObjectMapper mapper;
    private final String endpoint;

    private DefaultHttpClient httpclient;

    protected final WebTarget targetEndpoint;

    protected AbstractOsiamService(Builder<T> builder) {
        type = builder.type;
        typeName = builder.typeName;
        endpoint = builder.endpoint;
        webResource = builder.getWebResource();

        mapper = new ObjectMapper();
        SimpleModule userDeserializerModule = new SimpleModule("userDeserializerModule", Version.unknownVersion())
                .addDeserializer(User.class, new UserDeserializer(User.class));
        mapper.registerModule(userDeserializerModule);

        targetEndpoint = client.target(endpoint);
    }

    protected T getResource(String id, AccessToken accessToken) {
        checkArgument(!Strings.isNullOrEmpty(id), "The given id must not be null nor empty.");
        checkAccessTokenIsNotNull(accessToken);

        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path(typeName + "s").path(id).request(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER + accessToken.getToken())
                    .get();

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        checkAndHandleResponse(content, status, accessToken, "get", id);

        return mapToResource(content);
    }

    protected List<T> getAllResources(AccessToken accessToken) {
        return searchResources("count=" + Integer.MAX_VALUE, accessToken).getResources();
    }

    /**
     * @deprecated Use
     *             {@link AbstractOsiamService#searchResources(Query, AccessToken)}
     */
    @Deprecated
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
                errorMessage = getErrorMessageUnauthorized(response);
                throw new UnauthorizedException(errorMessage);
            case SC_FORBIDDEN:
                errorMessage = extractErrorMessageForbidden(accessToken, "get");
                throw new ForbiddenException(errorMessage);
            case SC_CONFLICT:
                errorMessage = getErrorMessage(response, "Unable to search with the search string '" + queryString
                        + "': "
                        + response.getStatusLine().getReasonPhrase());
                throw new ConflictException(errorMessage);
            default:
                errorMessage = getErrorMessageDefault(response, httpStatus);
                throw new OsiamRequestException(httpStatus, errorMessage);
            }
        }

        try {
            InputStream queryResult = response.getEntity().getContent();
            JavaType queryResultType = TypeFactory.defaultInstance().constructParametricType(SCIMSearchResult.class,
                    type);

            return mapper.readValue(queryResult, queryResultType);
        } catch (IOException e) {
            throw new ConnectionInitializationException("Unable to deserialize query result", e);
        }
    }

    /**
     * @deprecated Use
     *             {@link AbstractOsiamService#searchResources(Query, AccessToken)}
     */
    @Deprecated
    protected SCIMSearchResult<T> searchResources(org.osiam.client.query.Query query, AccessToken accessToken) {
        if (query == null) {
            throw new IllegalArgumentException("The given queryBuilder can't be null.");
        }
        return searchResources(query.toString(), accessToken);
    }

    protected SCIMSearchResult<T> searchResources(Query query, AccessToken accessToken) {
        // TODO implement
        return null;
    }

    protected void deleteResource(String id, AccessToken accessToken) {
        checkArgument(!Strings.isNullOrEmpty(id), "The given id must not be null nor empty.");
        checkAccessTokenIsNotNull(accessToken);

        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path(typeName + "s").path(id).request(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER + accessToken.getToken())
                    .delete();

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        checkAndHandleResponse(content, status, accessToken, "delete", id);
    }

    protected T createResource(T resource, AccessToken accessToken) {
        checkNotNull(resource, "The given %s must not be null nor empty.", typeName);
        checkAccessTokenIsNotNull(accessToken);

        String resourceAsString;
        try {
            resourceAsString = mapper.writeValueAsString(resource);
        } catch (JsonProcessingException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path(typeName + "s").request(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER + accessToken.getToken())
                    .post(Entity.entity(resourceAsString, MediaType.APPLICATION_JSON));

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        checkAndHandleResponse(content, status, accessToken, "create", null);

        return mapToResource(content);
    }

    protected T updateResource(String id, T resource, AccessToken accessToken) {
        return modifyResource(id, resource, "PATCH", accessToken);
    }

    protected T replaceResource(String id, T resource, AccessToken accessToken) {
        return modifyResource(id, resource, "PUT", accessToken);
    }

    private T modifyResource(String id, T resource, String method, AccessToken accessToken) {
        checkArgument(!Strings.isNullOrEmpty(id), "The given id must not be null nor empty.");
        checkNotNull(resource, "The given %s must not be null nor empty.", typeName);
        checkAccessTokenIsNotNull(accessToken);

        String resourceAsString;
        try {
            resourceAsString = mapper.writeValueAsString(resource);
        } catch (JsonProcessingException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path(typeName + "s").path(id).request(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER + accessToken.getToken())
                    .method(method, Entity.entity(resourceAsString, MediaType.APPLICATION_JSON));

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        checkAndHandleResponse(content, status, accessToken, method.equals("PUT") ? "replace" : "update", id);

        return mapToResource(content);
    }

    protected T mapToResource(String content) {
        return mapToType(content, type);
    }

    protected <U> U mapToType(String content, Class<U> type) {
        try {
            return mapper.readValue(content, type);
        } catch (IOException e) {
            throw new OsiamClientException(String.format("Unable to parse %s: %s", typeName, content), e);
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

    protected void checkAndHandleResponse(String content, StatusType status, AccessToken accessToken, String action,
            String id) {
        if (status.getFamily() == Family.SUCCESSFUL) {
            return;
        }

        if (status.getStatusCode() == Status.UNAUTHORIZED.getStatusCode()) {
            String errorMessage = extractErrorMessageUnauthorized(content, status);
            throw new UnauthorizedException(errorMessage);
        } else if (status.getStatusCode() == Status.BAD_REQUEST.getStatusCode()) {
            String errorMessage = extractErrorMessage(content, status, "Unable to update");
            throw new ConflictException(errorMessage);
        } else if (status.getStatusCode() == Status.NOT_FOUND.getStatusCode()) {
            String errorMessage = extractErrorMessage(content, status, String.format("No %s with given id %s was found", typeName, id));
            throw new NoResultException(errorMessage);
        } else if (status.getStatusCode() == Status.FORBIDDEN.getStatusCode()) {
            String errorMessage = extractErrorMessageForbidden(accessToken, action);
            throw new ForbiddenException(errorMessage);
        } else if (status.getStatusCode() == Status.CONFLICT.getStatusCode()) {
            String errorMessage = extractErrorMessage(content, status, "Unable to " + action);
            throw new ConflictException(errorMessage);
        } else {
            String errorMessage = extractErrorMessageDefault(content, status);
            throw new OsiamRequestException(status.getStatusCode(), errorMessage);
        }

    }

    protected String extractErrorMessageForbidden(AccessToken accessToken, String process) {
        return "Insufficient scope (" + accessToken.getScopes() + ") to " + process + " this " + typeName + ".";
    }

    protected String extractErrorMessageUnauthorized(String content, StatusType status) {
        return extractErrorMessage(content, status,
                "You are not authorized to access OSIAM. Please make sure your access token is valid");
    }

    private String getErrorMessageUnauthorized(HttpResponse httpResponse) {
        return getErrorMessage(httpResponse,
                "You are not authorized to access OSIAM. Please make sure your access token is valid");
    }

    protected String extractErrorMessageDefault(String content, StatusType status) {
        return extractErrorMessage(content, status,
                String.format("Unable to setup connection (HTTP Status Code: %d)", status.getStatusCode()));
    }

    private String getErrorMessageDefault(HttpResponse httpResponse, int httpStatus) {
        return getErrorMessage(httpResponse,
                String.format("Unable to setup connection (HTTP Status Code: %d)", httpStatus));
    }

    protected String extractErrorMessage(String content, StatusType status, String defaultErrorMessage) {
        try {
            ScimErrorMessage error = new ObjectMapper().readValue(content, ScimErrorMessage.class);

            return error.getDescription();
        } catch (ProcessingException | IOException e) {
            String errorMessage = String.format("Could not deserialize the error response for the HTTP status '%s'.",
                    status.getReasonPhrase());
            if (content != null) {
                errorMessage += String.format(" Original response: %s", content);
            }

            return errorMessage;
        }
    }

    protected String getErrorMessage(HttpResponse httpResponse, String defaultErrorMessage) {
        String errorMessage;
        InputStream content = null;
        String inputStreamStringValue = null;

        try {
            content = httpResponse.getEntity().getContent();
            inputStreamStringValue = IOUtils.toString(content, "UTF-8");
            ObjectMapper errorMapper = new ObjectMapper();
            ScimErrorMessage error = errorMapper.readValue(inputStreamStringValue, ScimErrorMessage.class);
            errorMessage = error.getDescription();
        } catch (Exception e) { // NOSONAR - we catch everything
            errorMessage = " Could not deserialize the error response for the status code \""
                    + httpResponse.getStatusLine().getReasonPhrase() + "\".";
            if (inputStreamStringValue != null) {
                errorMessage += " Original response: " + inputStreamStringValue;
            }
        }

        return errorMessage;
    }

    private <R extends HttpRequestBase> R addDefaultHeaderToRequest(R request, AccessToken accessToken) {
        request.addHeader(AUTHORIZATION, BEARER + accessToken.getToken());
        request.addHeader(ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        return request;
    }

    protected String getEndpoint() {
        return endpoint;
    }

    protected static void checkAccessTokenIsNotNull(AccessToken accessToken) {
        checkNotNull(accessToken, "The given accessToken must not be null.");
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