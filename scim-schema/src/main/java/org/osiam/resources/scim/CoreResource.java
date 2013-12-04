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

/**
 * Java class for CoreResource complex type.
 */
public abstract class CoreResource extends Resource {

    private String externalId;

    protected CoreResource() {
    }

    protected CoreResource(Builder builder) {
        super(builder);
        this.externalId = builder.externalId;
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
     * Abstract builder class that subclass builder inherit from.
     */
    public abstract static class Builder extends Resource.Builder {
        private String externalId;

        protected Builder(CoreResource coreResource) {
            super(coreResource);
            this.externalId = coreResource.externalId;
        }

        protected Builder() {
            super();
        }

        /**
         * Gets the external id (See {@link CoreResource#getExternalId()}).
         *
         * @param externalId
         *            the xternalId
         *
         * @return the builder itself
         */
        public Builder setExternalId(String externalId) {
            this.externalId = externalId;
            return this;
        }
    }
}
