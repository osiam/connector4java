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

/**
 *
 * This class represents a {@link User} or a {@link Group} which are members of an actual {@link Group}
 *
 * <p>
 * For more detailed information please look at the <a
 * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-8">SCIM core schema 2.0, sections 8</a>
 * </p>
 */
public class MemberRef extends MultiValuedAttribute { // NOSONAR - will be constructed by the builder or jackson

    @JsonProperty("$ref")
    private String reference;

    /**
     * Default constructor for Jackson
     */
    private MemberRef() {
    }

    private MemberRef(Builder builder) {
        super(builder);
        reference = builder.reference;
    }

    /**
     * Gets the reference to the actual SCIM Resource.
     *
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-8">SCIM core schema 2.0, sections 8</a>
     * </p>
     *
     * @return the reference of the actual resource
     */
    public String getReference() {
        return reference;
    }

    /**
     * The Builder class is used to construct instances of the {@link MemberRef}
     */
    public static class Builder extends MultiValuedAttribute.Builder {
        private String reference;

        /**
         * Sets the reference (See {@link MemberRef#getReference()}).
         *
         * @param reference
         *            the scim conform reference to the member
         * @return the builder itself
         */
        public Builder setReference(String reference) {
            this.reference = reference;
            return this;
        }

        @Override
        public Builder setValue(String value) {
            super.setValue(value);
            return this;
        }

        @Override
        public Builder setDisplay(String display) {
            super.setDisplay(display);
            return this;
        }

        @Override
        public Builder setPrimary(Boolean primary) {
            super.setPrimary(primary);
            return this;
        }

        @Override
        public Builder setType(String type) {
            super.setType(type);
            return this;
        }

        @Override
        public Builder setOperation(String operation) {
            super.setOperation(operation);
            return this;
        }

        @Override
        public MemberRef build() {
            return new MemberRef(this);
        }
    }
}
