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

/**
 * This class represents a multi valued attribute.
 *
 * <p>
 * For more detailed information please look at the <a
 * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2">SCIM core schema 2.0, section 3.2</a>
 * </p>
 */
@JsonInclude(Include.NON_EMPTY)
public class MultiValuedAttribute {

    private String value;
    private String display;
    private Boolean primary;
    private String type;
    private String operation;

    /**
     * Default constructor for Jackson
     */
    protected MultiValuedAttribute() {
    }

    protected MultiValuedAttribute(Builder builder) {
        this.value = builder.value;
        this.display = builder.display;
        this.primary = builder.primary;
        this.type = builder.type;
        this.operation = builder.operation;
    }

    /**
     * Gets the attribute's significant value; e.g., the e-mail address, phone number etc.
     *
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2">SCIM core schema 2.0, section
     * 3.2</a>
     * </p>
     *
     * @return the value of the actual multi value attribute
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the human readable name, primarily used for display purposes.
     *
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2">SCIM core schema 2.0, section
     * 3.2</a>
     * </p>
     *
     * @return the display attribute
     */
    public String getDisplay() {
        return display;
    }

    /**
     * Gets a Boolean value indicating the 'primary' or preferred attribute value for this attribute.
     *
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2">SCIM core schema 2.0, section
     * 3.2</a>
     * </p>
     *
     * @return the primary attribute
     */
    public Boolean isPrimary() {
        return primary;
    }

    /**
     * Gets the type of the attribute.
     *
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2">SCIM core schema 2.0, section
     * 3.2</a>
     * </p>
     *
     * @return the actual type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the operation applied during a PATCH request.
     *
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2">SCIM core schema 2.0, section
     * 3.2</a>
     * </p>
     *
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Builder class that is used to build {@link MultiValuedAttribute} instances
     */
    public static class Builder {

        private String value;
        private String display;
        private Boolean primary;
        private String type;
        private String operation;

        /**
         * Sets the attribute's significant value (See {@link MultiValuedAttribute#getValue()}).
         *
         * @param value
         *            the value attribute
         * @return the builder itself
         */
        public Builder setValue(String value) {
            this.value = value;
            return this;
        }

        /**
         * Sets the human readable name (See {@link MultiValuedAttribute#getDisplay()}).
         *
         * @param display
         *            a human readable name
         * @return the builder itself
         */
        public Builder setDisplay(String display) {
            this.display = display;
            return this;
        }

        /**
         * Sets the primary attribute (See {@link MultiValuedAttribute#isPrimary()}).
         *
         * @param the
         *            primary attribute
         * @return the builder itself
         */
        public Builder setPrimary(Boolean primary) {
            this.primary = primary;
            return this;
        }

        /**
         * Sets the label indicating the attribute's function (See {@link MultiValuedAttribute#getType()}).
         *
         * @param type
         *            the type of the attribute
         * @return the builder itself
         */
        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        /**
         * Sets the operation (See {@link MultiValuedAttribute#getOperation()}).
         *
         * @param operation
         *            only "delete" is supported at the moment
         * @return the builder itself
         */
        public Builder setOperation(String operation) {
            this.operation = operation;
            return this;
        }

        /**
         * Builds a MultiValuedAttribute Object with the given parameters
         *
         * @return a new MultiValuedAttribute Object
         */
        public MultiValuedAttribute build() {
            return new MultiValuedAttribute(this);
        }
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((display == null) ? 0 : display.hashCode());
        result = prime * result + ((operation == null) ? 0 : operation.hashCode());
        result = prime * result + ((primary == null) ? 0 : primary.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MultiValuedAttribute other = (MultiValuedAttribute) obj;
        if (display == null) {
            if (other.display != null) {
                return false;
            }
        } else if (!display.equals(other.display)) {
            return false;
        }
        if (operation == null) {
            if (other.operation != null) {
                return false;
            }
        } else if (!operation.equals(other.operation)) {
            return false;
        }
        if (primary == null) {
            if (other.primary != null) {
                return false;
            }
        } else if (!primary.equals(other.primary)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }
}
