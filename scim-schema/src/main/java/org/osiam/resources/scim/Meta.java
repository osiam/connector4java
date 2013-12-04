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

package org.osiam.resources.scim;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.osiam.resources.helper.JsonDateSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * This class represents the meta data of a resource.
 *
 * <p>
 * For more detailed information please look at the <a
 * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02">SCIM core schema 2.0</a>
 * </p>
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class Meta {

    @JsonSerialize(using = JsonDateSerializer.class)
    private Date created;
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date lastModified;
    private String location;
    private String version;
    private Set<String> attributes = new HashSet<>();
    private String resourceType;

    /**
     * Default constructor for Jackson
     */
    private Meta() {
    }

    private Meta(Builder builder) {
        this.created = builder.created;
        this.lastModified = builder.lastModified;
        this.attributes = builder.attributes;
        this.location = builder.location;
        this.version = builder.version;
        this.resourceType = builder.resourceType;
    }

    /**
     * Gets the URI of the Resource being returned.
     *
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-5">SCIM core schema 2.0, section 5</a>
     * </p>
     *
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * it needs to be deleted after the builder has the possibility to accept old Meta Object it is only needed by the
     * server at the moment
     *
     * @param location
     */
    @Deprecated
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the version of the Resource being returned.
     *
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-5">SCIM core schema 2.0, section 5</a>
     * </p>
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the attributes to be deleted from the Resource
     *
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-5">SCIM core schema 2.0, section 5</a>
     * </p>
     *
     * @return a set of attributes to be deleted
     */
    public Set<String> getAttributes() {
        return attributes;
    }

    /**
     * Gets the date when the {@link Resource} was created
     *
     * @return the creation date
     */
    public Date getCreated() {
        if (created != null) {
            return new Date(created.getTime());
        }
        return null;
    }

    /**
     * Gets the date when the {@link Resource} was last modified
     *
     * @return the last modified date
     */
    public Date getLastModified() {
        if (lastModified != null) {
            return new Date(lastModified.getTime());
        }
        return null;
    }

    /**
     * Gets the type of the Resource (User or Group)
     *
     * @return the type of the actual resource
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Builder class that is used to build {@link Meta} instances
     */
    public static class Builder {
        private final Date created;
        private final Date lastModified;
        private String location;
        private String version;
        private Set<String> attributes = new HashSet<>();
        private String resourceType;

        /**
         * Constructs a new builder with the created and last modified time set to the current time
         */
        public Builder() {
            this.created = new Date(System.currentTimeMillis());
            this.lastModified = this.created;
        }

        /**
         * Will set created to given value and lastModified to System.currentTime Only be used by the server. Will be
         * ignored by PUT and PATCH operations
         */
        public Builder(Date created) {
            this.created = created != null ? new Date(created.getTime()) : null;
            this.lastModified = new Date(System.currentTimeMillis());
        }

        /**
         * Constructs a new builder with the created and last modified time set to the given values
         */
        public Builder(Date created, Date lastModified) {
            this.created = created != null ? new Date(created.getTime()) : null;
            this.lastModified = lastModified != null ? new Date(lastModified.getTime()) : null;
        }

        /**
         * Set the location (See {@link Meta#getLocation()}).
         *
         * @param location
         *            the resource uri
         * @return the builder itself
         */
        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        /**
         * Sets the version of the Resource (See {@link Meta#getVersion()}).
         *
         * @param version
         *            the version of the resource
         * @return the builder itself
         */
        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Sets the type of the Resource (See {@link Meta#getResourceType()}).
         *
         * @param resourceType
         *            the type
         * @return the builder itself
         */
        public Builder setResourceType(String resourceType) {
            this.resourceType = resourceType;
            return this;
        }

        /**
         * Sets the names of the attributes to be removed from the Resource.
         *
         * @param attributes
         *            name of attributes to be deleted
         * @return the builder itself
         */
        public Builder setAttributes(Set<String> attributes) {
            this.attributes = attributes;
            return this;
        }

        /**
         * Builds a Meta Object with the given parameters
         *
         * @return a new Meta Object
         */
        public Meta build() {
            return new Meta(this);
        }
    }
}
