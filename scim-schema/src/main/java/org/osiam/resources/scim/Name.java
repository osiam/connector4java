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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Java class for name complex type.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class Name {

    private String formatted;
    private String familyName;
    private String givenName;
    private String middleName;
    private String honorificPrefix;
    private String honorificSuffix;

    /**
     * needed for json serializing
     */
    public Name() {
    }

    private Name(Builder builder) {
        this.formatted = builder.formatted;
        this.familyName = builder.familyName;
        this.givenName = builder.givenName;
        this.middleName = builder.middleName;
        this.honorificPrefix = builder.honorificPrefix;
        this.honorificSuffix = builder.honorificSuffix;

    }

    public static class Builder {
        private String formatted;
        private String familyName;
        private String givenName;
        private String middleName;
        private String honorificPrefix;
        private String honorificSuffix;

        public Builder setFormatted(String formatted) {
            this.formatted = formatted;
            return this;
        }

        public Builder setFamilyName(String familyName) {
            this.familyName = familyName;
            return this;
        }

        public Builder setGivenName(String givenName) {
            this.givenName = givenName;
            return this;
        }

        public Builder setMiddleName(String middleName) {
            this.middleName = middleName;
            return this;
        }

        public Builder setHonorificPrefix(String honorificPrefix) {
            this.honorificPrefix = honorificPrefix;
            return this;
        }

        public Builder setHonorificSuffix(String honorificSuffix) {
            this.honorificSuffix = honorificSuffix;
            return this;
        }

        public Name build() {
            return new Name(this);
        }
    }

    /**
     * Gets the value of the formatted property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getFormatted() {
        return formatted;
    }

    /**
     * Gets the value of the familyName property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * Gets the value of the givenName property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * Gets the value of the middleName property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Gets the value of the honorificPrefix property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getHonorificPrefix() {
        return honorificPrefix;
    }


    /**
     * Gets the value of the honorificSuffix property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getHonorificSuffix() {
        return honorificSuffix;
    }

    @Override
    public boolean equals(Object o) { // NOSONAR - Cyclomatic Complexity can be over 10
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Name other = (Name) o;

        if (familyName != null ? !familyName.equals(other.familyName) : other.familyName != null) {
            return false;
        }
        if (formatted != null ? !formatted.equals(other.formatted) : other.formatted != null) {
            return false;
        }
        if (givenName != null ? !givenName.equals(other.givenName) : other.givenName != null) {
            return false;
        }
        if (honorificPrefix != null ? !honorificPrefix.equals(other.honorificPrefix) : other.honorificPrefix != null) {
            return false;
        }
        if (honorificSuffix != null ? !honorificSuffix.equals(other.honorificSuffix) : other.honorificSuffix != null) {
            return false;
        }
        if (middleName != null ? !middleName.equals(other.middleName) : other.middleName != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = formatted != null ? formatted.hashCode() : 0;
        result = 31 * result + (familyName != null ? familyName.hashCode() : 0);
        result = 31 * result + (givenName != null ? givenName.hashCode() : 0);
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (honorificPrefix != null ? honorificPrefix.hashCode() : 0);
        result = 31 * result + (honorificSuffix != null ? honorificSuffix.hashCode() : 0);
        return result;
    }
}
