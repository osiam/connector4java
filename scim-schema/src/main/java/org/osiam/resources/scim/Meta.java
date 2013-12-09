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
 * Java class for meta complex type.
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

    //JSon Serializing ...
    public Meta() {
    }


    private Meta(Builder builder) {
        this.created = builder.created;
        this.lastModified = builder.lastModified;
        this.attributes = builder.attributes;
        this.location = builder.location;
        this.version = builder.version;
        this.resourceType = builder.resourceType;
    }

    public static class Builder {
        private final Date created;
        private final Date lastModified;
        private String location;
        private String version;
        private Set<String> attributes = new HashSet<>();
        private String resourceType;

        /**
         * Will set created, as well as lastModified to System.currentTime
         */
        public Builder() {
            this.created = new Date(System.currentTimeMillis());
            this.lastModified = new Date(System.currentTimeMillis());
        }

        /**
         * Will set created to given value and lastModified to System.currentTime
         */
        public Builder(Date created, Date lastModified) {
            this.created = created != null ? new Date(created.getTime()) : null;
            this.lastModified = lastModified != null ? new Date(lastModified.getTime()) : null;
        }

        /**
         * creates a new Meta.Builder based on the given Meta Object
         * @param meta an old meta Object
         */
        public Builder(Meta meta){
            if(meta == null){
                throw new IllegalArgumentException("The given Meta can't be null");
            }
            this.created = meta.created;
            this.lastModified = meta.lastModified;
            this.location = meta.location;
            this.version = meta.version;
            this.attributes = meta.attributes;
            this.resourceType = meta.resourceType;
        }

        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder setResourceType(String resourceType) {
            this.resourceType = resourceType;
            return this;
        }


        public Builder setAttributes(Set<String> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Meta build() {
            return new Meta(this);
        }
    }


    /**
     * Gets the value of the location property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the value of the version property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getVersion() {
        return version;
    }


    public Set<String> getAttributes() {
        return attributes;
    }

    public Date getCreated() {
        if (created != null) {
            return new Date(created.getTime());
        }
        return null;
    }

    public Date getLastModified() {
        if (lastModified != null) {
            return new Date(lastModified.getTime());
        }
        return null;
    }

    public String getResourceType() {
        return resourceType;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((resourceType == null) ? 0 : resourceType.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
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
        Meta other = (Meta) obj;
        if (attributes == null) {
            if (other.attributes != null) {
                return false;
            }
        } else if (!attributes.equals(other.attributes)) {
            return false;
        }
        if (created == null) {
            if (other.created != null) {
                return false;
            }
        } else if (!created.equals(other.created)) {
            return false;
        }
        if (lastModified == null) {
            if (other.lastModified != null) {
                return false;
            }
        } else if (!lastModified.equals(other.lastModified)) {
            return false;
        }
        if (location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!location.equals(other.location)) {
            return false;
        }
        if (resourceType == null) {
            if (other.resourceType != null) {
                return false;
            }
        } else if (!resourceType.equals(other.resourceType)) {
            return false;
        }
        if (version == null) {
            if (other.version != null) {
                return false;
            }
        } else if (!version.equals(other.version)) {
            return false;
        }
        return true;
    }


}
