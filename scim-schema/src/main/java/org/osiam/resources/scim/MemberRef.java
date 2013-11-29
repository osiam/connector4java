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

public class MemberRef extends MultiValuedAttribute { // NOSONAR - will be constructed by the builder or jackson

    @JsonProperty("$ref")
    private String reference;

    /**
     * For Jackson
     */
    private MemberRef() {
    }

    private MemberRef(Builder builder) {
        super(builder);
        reference = builder.reference;
    }

    public String getReference() {
        return reference;
    }

    public static class Builder extends MultiValuedAttribute.Builder {
        private String reference;

        public MemberRef.Builder setReference(String reference) {
            this.reference = reference;
            return this;
        }

        @Override
        public MemberRef build() {
            return new MemberRef(this);
        }

        @Override
        public MemberRef.Builder setValue(String value) {
            super.setValue(value);
            return this;
        }

        @Override
        public MemberRef.Builder setDisplay(String display) {
            super.setDisplay(display);
            return this;
        }

        @Override
        public MemberRef.Builder setPrimary(Boolean primary) {
            super.setPrimary(primary);
            return this;
        }

        @Override
        public MemberRef.Builder setType(String type) {
            super.setType(type);
            return this;
        }

        @Override
        public MemberRef.Builder setOperation(String operation) {
            super.setOperation(operation);
            return this;
        }

    }
}
