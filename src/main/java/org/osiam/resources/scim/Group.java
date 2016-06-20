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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import org.osiam.resources.exception.SCIMDataValidationException;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represent a Group resource.
 * <p>
 * For more detailed information please look at the
 * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-8">SCIM core schema 2.0, sections 8</a>
 * </p>
 */
@JsonInclude(Include.NON_EMPTY)
public final class Group extends Resource implements Serializable {

    public static final String SCHEMA = "urn:ietf:params:scim:schemas:core:2.0:Group";
    private static final long serialVersionUID = -2995603177584656028L;

    private final String displayName;
    private final Set<MemberRef> members;

    @JsonCreator
    private Group(@JsonProperty("id") String id,
                  @JsonProperty("externalId") String externalId,
                  @JsonProperty("meta") Meta meta,
                  @JsonProperty(value = "schemas", required = true) Set<String> schemas,
                  @JsonProperty("displayName") String displayName,
                  @JsonProperty("members") Set<MemberRef> members) {
        super(id, externalId, meta, schemas);
        this.displayName = displayName;
        this.members = members != null ? ImmutableSet.copyOf(members) : ImmutableSet.<MemberRef>of();
    }

    Group(Builder builder) {
        this(builder.getId(), builder.getExternalId(), builder.getMeta(), builder.getSchemas(),
                builder.displayName, builder.members);
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
     * <p/>
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-8">SCIM core schema 2.0, sections
     * 8</a>
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

        /**
         * @deprecated Change the display name with {@link #setDisplayName(String)}. Will be removed in 1.12 or 2.0.
         */
        @Deprecated
        public Builder(String displayName, Group group) {
            super(group);
            addSchema(SCHEMA);
            if (group != null) {
                this.displayName = group.displayName;
                members.addAll(group.members);
            }
            if (!Strings.isNullOrEmpty(displayName)) {
                this.displayName = displayName;
            }
        }

        /**
         * creates a new Group without a displayName
         */
        public Builder() {
            this(null, null);
        }

        /**
         * Constructs a new builder by copying all values from the given {@link Group}
         *
         * @param group {@link Group} to be copied from
         * @throws SCIMDataValidationException if the given group is null
         */
        public Builder(Group group) {
            this(null, group);
            if (group == null) {
                throw new SCIMDataValidationException("The given group can't be null.");
            }
        }

        /**
         * Constructs a new builder and sets the display name (See {@link Group#getDisplayName()}).
         *
         * @param displayName the display name
         * @throws SCIMDataValidationException if the displayName is null or empty
         */
        public Builder(String displayName) {
            this(displayName, null);
            if (displayName == null) {
                throw new SCIMDataValidationException("The given resource can't be null");
            }
        }

        /**
         * @deprecated You should not need to set the ID with a client. Will be removed in 1.12 or 2.0.
         */
        @Deprecated
        @Override
        public Builder setId(String id) {
            super.setId(id);
            return this;
        }

        /**
         * @deprecated You should not need to set the meta attribute with a client. Will be removed in 1.12 or 2.0.
         */
        @Deprecated
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

        /**
         * @deprecated Don't use this method - let the extensions add their schema themselves. Will be removed in
         * version 1.10 or 2.0
         */
        @Override
        @Deprecated
        public Builder setSchemas(Set<String> schemas) {
            super.setSchemas(schemas);
            return this;
        }

        /**
         * Sets the display name (See {@link Group#getDisplayName()}).
         *
         * @param displayName the display name to set
         * @return the builder itself
         */
        public Builder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        /**
         * @deprecated Use {@link #removeMembers()} followed by {@link #addMembers(Collection)}. Will be removed in 1.12 or 2.0.
         */
        @Deprecated
        public Builder setMembers(Set<MemberRef> members) {
            this.members = members;
            return this;
        }

        /**
         * Add the given member to the {@link Group}.
         *
         * @param member The member to add.
         * @return The builder itself
         */
        public Builder addMember(MemberRef member) {
            members.add(member);
            return this;
        }

        /**
         * Add the given members to the {@link Group}.
         *
         * @param members The members to add
         * @return The builder itself
         */
        public Builder addMembers(Collection<MemberRef> members) {
            if (members != null) {
                for (MemberRef entry : members) {
                    this.addMember(entry);
                }
            }
            return this;
        }

        /**
         * Remove the given member from the {@link Group}
         *
         * @param member The member to add.
         * @return The builder itself
         */
        public Builder removeMember(MemberRef member) {
            members.remove(member);
            return this;
        }

        /**
         * Removes all members from this {@link Group}
         *
         * @return the builder itself
         */
        public Builder removeMembers() {
            this.members.clear();
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Group build() {
            return new Group(this);
        }
    }
}
