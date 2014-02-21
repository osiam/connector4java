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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represent a Group resource.
 * 
 * <p>
 * For more detailed information please look at the <a
 * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-8">SCIM core schema 2.0, sections 8</a>
 * </p>
 */
@JsonInclude(Include.NON_EMPTY)
public class Group extends Resource {

    private String displayName;
    private Set<MemberRef> members = new HashSet<>();

    /**
     * Default constructor for Jackson
     */
    private Group() {
    }

    private Group(Builder builder) {
        super(builder);
        this.displayName = builder.displayName;
        this.members = builder.members;
    }

    /**
     * Gets the human readable name of this {@link Group}.
     * 
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the list of members of this Group.
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-8">SCIM core schema 2.0, sections 8</a>
     * </p>
     * 
     * @return the list of Members as a Set
     */
    public Set<MemberRef> getMembers() {
        return members;
    }

    @Override
    public String toString() {
        return "Group [displayName=" + displayName + ", members=" + members + ", getId()=" + getId()
                + ", getExternalId()=" + getExternalId() + ", getMeta()=" + getMeta() + ", getSchemas()="
                + getSchemas() + "]";
    }

    /**
     * Builder class that is used to build {@link Group} instances
     */
    public static class Builder extends Resource.Builder {

        private String displayName;
        private Set<MemberRef> members = new HashSet<>();

        public Builder() {
            super();
            this.schemas.add(Constants.GROUP_CORE_SCHEMA);
        }

        /**
         * Constructs a new builder by copying all values from the given {@link Group}
         * 
         * @param group
         *            {@link Group} to be copied from
         */
        public Builder(Group group) {
            super(group);
            displayName = group.displayName;
            members = group.members;
        }

        /**
         * Constructs a new builder and sets the display name (See {@link Group#getDisplayName()}).
         * 
         * @param displayName
         *            the display name
         */
        public  Builder(String displayName) {
            this();
            if (displayName == null) {
                throw new IllegalArgumentException("The given resource must not be null");
            }
            this.displayName = displayName;
        }

        @Override
        public Builder setId(String id) {
            super.setId(id);
            return this;
        }

        @Override
        public Builder setMeta(Meta meta) {
            super.setMeta(meta);
            return this;
        }

        @Override
        public Builder setExternalId(String externalId) {
            super.setExternalId(externalId);
            return this;
        }

        @Override
        public Builder setSchemas(Set<String> schemas) {
            super.setSchemas(schemas);
            return this;
        }

        /**
         * Sets the list of members as {@link Set} (See {@link Group#getMembers()}).
         * 
         * @param members
         *            the set of members
         * @return the builder itself
         */
        public Builder setMembers(Set<MemberRef> members) {
            this.members = members;
            return this;
        }

        @Override
        public Group build() {
            return new Group(this);
        }
    }
}
