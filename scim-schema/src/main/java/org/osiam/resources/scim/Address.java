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
 * A physical mailing address for a User
 * <p>
 * For more detailed information please look at the <a
 * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0</a>
 * </p>
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class Address extends MultiValuedAttribute { // NOSONAR - Builder constructs instances of this class

    private String formatted;
    private String streetAddress;
    private String locality;
    private String region;
    private String postalCode;
    private String country;

    /**
     * Default constructor for Jackson
     */
    private Address() {
    }

    private Address(Builder builder) {
        super(builder);
        this.formatted = builder.formatted;
        this.streetAddress = builder.streetAddress;
        this.locality = builder.locality;
        this.region = builder.region;
        this.postalCode = builder.postalCode;
        this.country = builder.country;
    }

    /**
     * Gets the full mailing address, formatted for display or use with a mailing label.
     *
     * @return the formatted address
     */
    public String getFormatted() {
        return formatted;
    }

    /**
     * Gets the full street address, which may include house number, street name, etc.
     *
     * @return the street address
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     * Gets the city or locality
     *
     * @return the city or locality
     */
    public String getLocality() {
        return locality;
    }

    /**
     * Gets the state or region
     *
     * @return region the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * Gets the postal code
     *
     * @return postalCode the postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Gets the country name in ISO 3166-1 alpha 2 format, e.g. "DE" or "US".
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Builder class that is used to build {@link Address} instances
     */
    public static class Builder extends MultiValuedAttribute.Builder {
        private String formatted;
        private String streetAddress;
        private String locality;
        private String region;
        private String postalCode;
        private String country;

        /**
         * Sets the full mailing address (See {@link Address#getFormatted()}).
         *
         * @param formatted
         *            the formatted address
         *
         * @return the builder itself
         */
        public Builder setFormatted(String formatted) {
            this.formatted = formatted;
            return this;
        }

        /**
         * Sets the full street address component, (See {@link Address#getStreetAddress()}).
         *
         * @param streetAddress
         *            the street address
         *
         * @return the builder itself
         */
        public Builder setStreetAddress(String streetAddress) {
            this.streetAddress = streetAddress;
            return this;
        }

        /**
         * Sets the city or locality.
         *
         * @param locality
         *            the locality
         *
         * @return the builder itself
         */
        public Builder setLocality(String locality) {
            this.locality = locality;
            return this;
        }

        /**
         * Sets the state or region.
         *
         * @param region
         *            the region
         *
         * @return the builder itself
         */
        public Builder setRegion(String region) {
            this.region = region;
            return this;
        }

        /**
         * Sets the postal code
         *
         * @param postalCode
         *            the postal code
         *
         * @return the builder itself
         */
        public Builder setPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        /**
         * Sets the country name (See {@link Address#getCountry()}).
         *
         * @param country
         *            the country
         *
         * @return the builder itself
         */
        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }

        @Override
        public Builder setPrimary(Boolean primary) {
            super.setPrimary(primary);
            return this;
        }

        @Override
        public Address build() {
            return new Address(this);
        }

    }
}
