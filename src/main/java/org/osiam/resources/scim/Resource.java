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
package org.osiam.resources.scim;

import com.google.common.collect.ImmutableSet;
import org.osiam.resources.exception.SCIMDataValidationException;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a SCIM Resource and is the base class for {@link User}s and {@link Group}s.
 */
public abstract class Resource implements Serializable {

    private static final long serialVersionUID = 1726103518645055449L;

    private final String id;
    private final String externalId;
    private final Meta meta;
    private final Set<String> schemas;

    Resource(String id, String externalId, Meta meta, Set<String> schemas) {
        this.id = id;
        this.externalId = externalId;
        this.meta = meta;
        if (schemas == null || schemas.isEmpty()) {
            throw new SCIMDataValidationException("Schemas cannot be null or empty!");
        }
        this.schemas = ImmutableSet.copyOf(schemas);
    }

    /**
     * Gets the Id of the resource.
     *
     * @return the id of the resource
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the external Id of the resource.
     * <p>
     * For more information please look at
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-5.1">SCIM core schema 2.0, section
     * 5.1</a>
     * </p>
     *
     * @return the externalId
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * Gets the meta attribute
     *
     * @return the meta
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * Gets the list of defined schemas
     *
     * @return a the list of schemas as a {@link Set}
     */
    public Set<String> getSchemas() {
        return schemas;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Resource other = (Resource) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    /**
     * The Builder class is used to construct instances of the {@link Resource}
     */
    public abstract static class Builder {

        private String externalId;
        private String id;
        private Meta meta;
        private Set<String> schemas = new HashSet<>();

        public Builder(Resource resource) {
            if (resource != null) {
                this.id = resource.id;
                this.externalId = resource.externalId;
                this.meta = resource.meta;
                this.schemas.addAll(resource.schemas);
            }
        }

        /**
         * @deprecated Don't use this method - let the extensions add their schema themselves. Will be removed in
         * version 1.8 or 2.0
         */
        @Deprecated
        public Builder setSchemas(Set<String> schemas) {
            this.schemas = schemas;
            return this;
        }

        protected void addSchema(String schema) {
            if (schemas == null) {
                schemas = new HashSet<>();
            }
            schemas.add(schema);
        }

        /**
         * @deprecated You should not need to set the ID with a client. Will be removed in 1.12 or 2.0.
         */
        @Deprecated
        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the external id (See {@link Resource#getExternalId()}).
         *
         * @param externalId the external id
         * @return the builder itself
         */
        public Builder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @deprecated Use {@link #externalId(String)}. Will be removed in 1.12 or 2.0.
         */
        @Deprecated
        public Builder setExternalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @deprecated You should not need to set the meta attribute with a client. Will be removed in 1.12 or 2.0.
         */
        @Deprecated
        public Builder setMeta(Meta meta) {
            this.meta = meta;
            return this;
        }

        protected String getExternalId() {
            return externalId;
        }

        protected String getId() {
            return id;
        }

        protected Meta getMeta() {
            return meta;
        }

        protected Set<String> getSchemas() {
            return schemas;
        }

        /**
         * Builds the Object of the Builder
         *
         * @return a new main Object of the Builder
         */
        public abstract <T> T build();
    }

}
