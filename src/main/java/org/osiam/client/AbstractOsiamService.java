/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import org.glassfish.jersey.client.ClientProperties;
import org.osiam.client.exception.*;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryBuilder;
import org.osiam.resources.helper.UserDeserializer;
import org.osiam.resources.scim.*;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.osiam.client.OsiamConnector.objectMapper;

/**
 * AbstractOsiamService provides all basic methods necessary to manipulate the Entities registered in the given OSIAM
 * installation.
 */
abstract class AbstractOsiamService<T extends Resource> {

    static final String CONNECTION_SETUP_ERROR_STRING = "Cannot connect to OSIAM";
    private static final String AUTHORIZATION = "Authorization";
    static final String BEARER = "Bearer ";

    final WebTarget targetEndpoint;
    private final Class<T> type;
    private final String typeName;
    private final int connectTimeout;
    private final int readTimeout;
    private final Version version;

    AbstractOsiamService(String endpoint, Class<T> type, int connectTimeout, int readTimeout, Version version) {
        this.type = type;
        this.typeName = type.getSimpleName();
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.version = version;
        UserDeserializer userDeserializer =
                this.version == Version.OSIAM_2_LEGACY_SCHEMAS ? new UserDeserializer(OsiamUserService.LEGACY_SCHEMA) : new UserDeserializer();
        SimpleModule userDeserializerModule = new SimpleModule("userDeserializerModule", com.fasterxml.jackson.core.Version.unknownVersion())
                .addDeserializer(User.class, userDeserializer);
        objectMapper.registerModule(userDeserializerModule);

        targetEndpoint = OsiamConnector.getClient().target(endpoint);
    }

    static void checkAccessTokenIsNotNull(AccessToken accessToken) {
        checkNotNull(accessToken, "The given accessToken must not be null.");
    }

    T getResource(String id, AccessToken accessToken, String... attributes) {
        checkArgument(!Strings.isNullOrEmpty(id), "The given id must not be null nor empty.");
        checkAccessTokenIsNotNull(accessToken);

        StatusType status;
        String content;

        WebTarget target;
        if (attributes == null || attributes.length == 0) {
            target = targetEndpoint;
        } else {
            target = targetEndpoint.queryParam("attributes", Joiner.on(",").join(attributes));
        }
        try {
            Response response = target.path(typeName + "s").path(id).request(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER + accessToken.getToken())
                    .property(ClientProperties.CONNECT_TIMEOUT, connectTimeout)
                    .property(ClientProperties.READ_TIMEOUT, readTimeout)
                    .get();

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        checkAndHandleResponse(content, status, accessToken);

        return mapToResource(content);
    }

    List<T> getAllResources(AccessToken accessToken, String... attributes) {
        QueryBuilder qBuilder = new QueryBuilder().count(Integer.MAX_VALUE);
        if (attributes != null && attributes.length > 0) {
            qBuilder.attributes(Joiner.on(',').join(attributes));
        }
        Query query = qBuilder.build();
        return searchResources(query, accessToken).getResources();
    }

    SCIMSearchResult<T> searchResources(Query query, AccessToken accessToken) {
        checkNotNull(query, "The given query must not be null.");
        checkAccessTokenIsNotNull(accessToken);

        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path(typeName + "s")
                    .queryParam("attributes", query.getAttributes())
                    .queryParam("filter", query.getFilter())
                    .queryParam("sortBy", query.getSortBy())
                    .queryParam("sortOrder", query.getSortOrder())
                    .queryParam("startIndex",
                            query.getStartIndex() != QueryBuilder.DEFAULT_START_INDEX ? query.getStartIndex() : null)
                    .queryParam("count",
                            query.getCount() != QueryBuilder.DEFAULT_COUNT ? query.getCount() : null)
                    .request(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER + accessToken.getToken())
                    .property(ClientProperties.CONNECT_TIMEOUT, connectTimeout)
                    .property(ClientProperties.READ_TIMEOUT, readTimeout)
                    .get();

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        checkAndHandleResponse(content, status, accessToken);

        try {
            JavaType queryResultType = TypeFactory.defaultInstance().constructParametrizedType(SCIMSearchResult.class,
                    SCIMSearchResult.class, type);
            return objectMapper.readValue(content, queryResultType);
        } catch (IOException e) {
            throw new OsiamClientException(String.format("Unable to deserialize search result: %s", content), e);
        }
    }

    void deleteResource(String id, AccessToken accessToken) {
        checkArgument(!Strings.isNullOrEmpty(id), "The given id must not be null nor empty.");
        checkAccessTokenIsNotNull(accessToken);

        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path(typeName + "s").path(id).request(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER + accessToken.getToken())
                    .property(ClientProperties.CONNECT_TIMEOUT, connectTimeout)
                    .property(ClientProperties.READ_TIMEOUT, readTimeout)
                    .delete();

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        checkAndHandleResponse(content, status, accessToken);
    }

    T createResource(T resource, AccessToken accessToken) {
        checkNotNull(resource, "The given %s must not be null nor empty.", typeName);
        checkAccessTokenIsNotNull(accessToken);

        String resourceAsString;
        try {
            resourceAsString = mapToString(resource);
        } catch (JsonProcessingException | ClassCastException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path(typeName + "s").request(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER + accessToken.getToken())
                    .property(ClientProperties.CONNECT_TIMEOUT, connectTimeout)
                    .property(ClientProperties.READ_TIMEOUT, readTimeout)
                    .post(Entity.json(resourceAsString));

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        checkAndHandleResponse(content, status, accessToken);

        return mapToResource(content);
    }

    /**
     * @deprecated Updating with PATCH has been removed in OSIAM 3.0. This method is going to go away with version 1.12 or 2.0.
     */
    @Deprecated
    T updateResource(String id, T resource, AccessToken accessToken) {
        return modifyResource(id, resource, "PATCH", accessToken);
    }

    T replaceResource(String id, T resource, AccessToken accessToken) {
        return modifyResource(id, resource, "PUT", accessToken);
    }

    private T modifyResource(String id, T resource, String method, AccessToken accessToken) {
        checkArgument(!Strings.isNullOrEmpty(id), "The given id must not be null nor empty.");
        checkNotNull(resource, "The given %s must not be null nor empty.", typeName);
        checkAccessTokenIsNotNull(accessToken);

        String resourceAsString;
        try {
            resourceAsString = mapToString(resource);
        } catch (JsonProcessingException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        StatusType status;
        String content;
        try {
            Response response = targetEndpoint.path(typeName + "s").path(id).request(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER + accessToken.getToken())
                    .property(ClientProperties.CONNECT_TIMEOUT, connectTimeout)
                    .property(ClientProperties.READ_TIMEOUT, readTimeout)
                    .method(method, Entity.json(resourceAsString));

            status = response.getStatusInfo();
            content = response.readEntity(String.class);
        } catch (ProcessingException e) {
            throw new ConnectionInitializationException(CONNECTION_SETUP_ERROR_STRING, e);
        }

        checkAndHandleResponse(content, status, accessToken);

        return mapToResource(content);
    }

    private T mapToResource(String content) {
        return mapToType(content, type);
    }

    <U> U mapToType(String content, Class<U> type) {
        try {
            if (version == Version.OSIAM_2_LEGACY_SCHEMAS && (type == User.class || type == Group.class)) {
                ObjectNode resourceNode = (ObjectNode) objectMapper.readTree(content);
                switchToLegacySchema(resourceNode);
                return objectMapper.readValue(objectMapper.treeAsTokens(resourceNode), type);
            } else {
                return objectMapper.readValue(content, type);
            }
        } catch (IOException | ClassCastException e) {
            throw new OsiamClientException(String.format("Unable to parse %s: %s", typeName, content), e);
        }
    }

    private String mapToString(T resource) throws JsonProcessingException {
        if (version == Version.OSIAM_2_LEGACY_SCHEMAS) {
            ObjectNode resourceNode = objectMapper.valueToTree(resource);
            switchToLegacySchema(resourceNode);
            return resourceNode.toString();
        } else {
            return objectMapper.writeValueAsString(resource);
        }
    }

    private void switchToLegacySchema(ObjectNode resourceNode) {
        ArrayNode schemas = (ArrayNode) resourceNode.get("schemas");
        for (int i = 0; i < schemas.size(); i++) {
            if (getSchema().equals(schemas.get(i).textValue())) {
                schemas.remove(i);
            }
        }
        schemas.add(getLegacySchema());
    }

    protected abstract String getSchema();

    protected abstract String getLegacySchema();

    void checkAndHandleResponse(String content, StatusType status, AccessToken accessToken) {
        if (status.getFamily() == Family.SUCCESSFUL) {
            return;
        }

        if (status.getStatusCode() == Status.UNAUTHORIZED.getStatusCode()) {
            String errorMessage = extractErrorMessageUnauthorized(content, status);
            throw new UnauthorizedException(errorMessage);
        } else if (status.getStatusCode() == Status.BAD_REQUEST.getStatusCode()) {
            String errorMessage = extractErrorMessage(content, status);
            throw new BadRequestException(errorMessage);
        } else if (status.getStatusCode() == Status.NOT_FOUND.getStatusCode()) {
            String errorMessage = extractErrorMessage(content, status);
            throw new NoResultException(errorMessage);
        } else if (status.getStatusCode() == Status.FORBIDDEN.getStatusCode()) {
            String errorMessage = extractErrorMessageForbidden(accessToken);
            throw new ForbiddenException(errorMessage);
        } else if (status.getStatusCode() == Status.CONFLICT.getStatusCode()) {
            String errorMessage = extractErrorMessage(content, status);
            throw new ConflictException(errorMessage);
        } else {
            String errorMessage = extractErrorMessageDefault(content, status);
            throw new OsiamRequestException(status.getStatusCode(), errorMessage);
        }

    }

    private String extractErrorMessageForbidden(AccessToken accessToken) {
        return "Insufficient scopes: " + accessToken.getScopes();
    }

    private String extractErrorMessageUnauthorized(String content, StatusType status) {
        return extractErrorMessage(content, status);
    }

    private String extractErrorMessageDefault(String content, StatusType status) {
        return extractErrorMessage(content, status);
    }

    private String extractErrorMessage(String content, StatusType status) {
        String message;
        if (version == Version.OSIAM_2_LEGACY_SCHEMAS) {
            message = getScimErrorMessageLegacy(content);
        } else {
            message = getScimErrorMessage(content);
        }

        if (message == null) {
            message = getOAuthErrorMessage(content);
        }

        if (message == null) {
            message = String.format("Could not deserialize the error response for the HTTP status '%s'.",
                    status.getReasonPhrase());
            if (content != null) {
                message += String.format(" Original response: %s", content);
            }
        }

        return message;
    }

    private String getScimErrorMessage(String content) {
        try {
            ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
            return error.getDetail();
        } catch (ProcessingException | IOException e) {
            return null;
        }
    }

    private String getScimErrorMessageLegacy(String content) {
        try {
            Map<String, String> error = objectMapper.readValue(content, new TypeReference<Map<String, String>>() {
            });
            return error.get("description");
        } catch (ProcessingException | IOException e) {
            return null;
        }
    }

    private String getOAuthErrorMessage(String content) {
        try {
            OAuthErrorMessage error = objectMapper.readValue(content, OAuthErrorMessage.class);
            return error.getDescription();
        } catch (ProcessingException | IOException e) {
            return null;
        }
    }

    int getConnectTimeout() {
        return connectTimeout;
    }

    int getReadTimeout() {
        return readTimeout;
    }

    Version getVersion() {
        return version;
    }
}
