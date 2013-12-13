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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;

/**
 * Java class for Resource complex type.
 */
public abstract class Resource {

    private String id;
    private String externalId;
    private Meta meta;
    @JsonProperty(required = true)
    private Set<String> schemas;

    /**
     * Default constructor for Jackson
     */
    protected Resource() {
    }

    protected Resource(Builder builder) {
        this.id = builder.id;
        this.externalId = builder.externalId;
        this.meta = builder.meta;
        this.schemas = builder.schemas;
    }

    /**
     * Gets the value of the id property.
     *
     * @return the id of the resource
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the external Id of the resource.
     *
     * <p>
     * For more information please look at <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-5.1">SCIM core schema 2.0, section 5.1</a>
     * </p>
     *
     * @return the externalId
     *
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * Gets the value of the meta property.
     *
     * @return the meta
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * Gets a set of all containing schemas
     *
     * @return a set of schemas
     */
    public Set<String> getSchemas() {
        return schemas;
    }

    /**
     * The Builder class is used to construct instances of the {@link Resource}
     */
    public abstract static class Builder {
        private String id;
        private Meta meta;
        protected Set<String> schemas = new HashSet<>(); // NOSONAR - fields are needed in child classes
        protected String externalId; // NOSONAR - fields are needed in child classes

        public Builder(Resource resource) {
            if(resource == null){
                throw new IllegalArgumentException("The given resource must not be null");
            }
            this.id = resource.id;
            this.externalId = resource.externalId;
            this.meta = resource.meta;
            this.schemas = resource.schemas;
        }

        public Builder() {
        }

        /**
         * sets the schemas of the Resource. Only be set by the Osiam Server
         *
         * @param schemas
         *            actual schemas
         * @return the builder itself
         */
        public Builder setSchemas(Set<String> schemas) {
            this.schemas = schemas;
            return this;
        }

        /**
         * Sets the id of the resource. Only be set by the Osiam Server
         *
         * @param id
         *            if of the resource
         * @return the builder itself
         */
        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the external id (See {@link Resource#getExternalId()}).
         *
         * @param externalId
         *            the external id
         *
         * @return the builder itself
         */
        public Builder setExternalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * Sets the meta object of the actual resource
         *
         * @param meta
         *            the meta object
         * @return the builder itself
         */
        public Builder setMeta(Meta meta) {
            this.meta = meta;
            return this;
        }

        /**
         * Builds the Object of the Builder
         *
         * @return a new main Object of the Builder
         */
        public abstract <T> T build();
    }

}
