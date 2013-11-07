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

package org.osiam.resources.scim.extension;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.io.BaseEncoding;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This enum like class represents the valid extension field types. Instances of this class also define methods for
 * converting these types from and to {@link String}.
 * 
 * @param <T>
 *            the actual type this {@link FieldType} represents
 */
public abstract class FieldType<T> {

    private String name;

    private FieldType(String name) {
        this.name = name;
    }

    /**
     * Converts the given {@link String} to the actual type.
     * 
     * @param stringValue
     *            the {@link String} value to be converted
     * @return the given {@link String} value converted to the actual Type
     */
    public abstract T fromString(String stringValue);

    /**
     * Converts a value of the actual type to {@link String}.
     * 
     * @param value
     *            the value to be converted
     * @return the given value as {@link String}
     */
    public abstract String toString(T value);

    /**
     * Returns the name of the {@link FieldType}
     * 
     * @return the name of the {@link FieldType}
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns a string representation of the {@link FieldType} which is its name.
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Retrieves a {@link FieldType} by its name.
     * 
     * @param name
     *            the name of the {@link FieldType} as it is returned by toString()
     * @return the {@link FieldType} based on the given name
     * @throws IllegalArgumentException
     *             if there is no {@link FieldType} with the given name
     */
    public static FieldType<?> valueOf(String name) {
        switch (name) {
        case "STRING":
            return STRING;
        case "INTEGER":
            return INTEGER;
        case "DECIMAL":
            return DECIMAL;
        case "BOOLEAN":
            return BOOLEAN;
        case "DATE_TIME":
            return DATE_TIME;
        case "BINARY":
            return BINARY;
        case "REFERENCE":
            return REFERENCE;
        default:
            throw new IllegalArgumentException("Type " + name + " does not exist");
        }
    }

    /**
     * FieldType for the Scim type String (actual type is {@link String})
     */
    public static final FieldType<String> STRING = new FieldType<String>("STRING") {

        @Override
        public String fromString(String stringValue) {
            ensureValueIsNotNull(stringValue);
            return stringValue;
        }

        @Override
        public String toString(String value) {
            ensureValueIsNotNull(value);
            return value;
        }

    };

    /**
     * FieldType for the Scim type Integer (actual type is {@link BigInteger})
     */
    public static final FieldType<BigInteger> INTEGER = new FieldType<BigInteger>("INTEGER") {

        @Override
        public BigInteger fromString(String stringValue) {
            ensureValueIsNotNull(stringValue);
            try {
                return new BigInteger(stringValue);
            } catch (NumberFormatException e) {
                throw createConversionException(stringValue, "BigInteger");
            }
        }

        @Override
        public String toString(BigInteger value) {
            ensureValueIsNotNull(value);
            return value.toString();
        }

    };

    /**
     * FieldType for the Scim type Decimal (actual type is {@link BigDecimal})
     */
    public static final FieldType<BigDecimal> DECIMAL = new FieldType<BigDecimal>("DECIMAL") {

        @Override
        public BigDecimal fromString(String stringValue) {
            ensureValueIsNotNull(stringValue);
            try {
                return new BigDecimal(stringValue);
            } catch (NumberFormatException e) {
                throw createConversionException(stringValue, "BigDecimal");
            }
        }

        @Override
        public String toString(BigDecimal value) {
            ensureValueIsNotNull(value);
            return value.toString();
        }

    };

    /**
     * FieldType for the Scim type Boolean (actual type is {@link Boolean})
     */
    public static final FieldType<Boolean> BOOLEAN = new FieldType<Boolean>("BOOLEAN") {

        @Override
        public Boolean fromString(String stringValue) {
            ensureValueIsNotNull(stringValue);
            try {
                return new Boolean(stringValue);
            } catch (NumberFormatException e) {
                throw createConversionException(stringValue, "Boolean");
            }
        }

        @Override
        public String toString(Boolean value) {
            ensureValueIsNotNull(value);
            return value.toString();
        }

    };

    /**
     * FieldType for the Scim type DateTime (actual type is {@link Date}). Valid values are in ISO DateTimeFormat with
     * the timeZone UTC like '2011-08-01T18:29:49.000Z'
     */
    public static final FieldType<Date> DATE_TIME = new FieldType<Date>("DATE_TIME") {

        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime().withZoneUTC();

        @Override
        public Date fromString(String stringValue) {
            ensureValueIsNotNull(stringValue);
            try {
                long millis = dateTimeFormatter.parseDateTime(stringValue).getMillis();
                return new Date(millis);
            } catch (NumberFormatException e) {
                throw createConversionException(stringValue, "Date");
            }
        }

        @Override
        public String toString(Date value) {
            ensureValueIsNotNull(value);
            return dateTimeFormatter.print(value.getTime());
        }

    };

    /**
     * FieldType for the Scim type Binary (actual type is {@code byte[]})
     */
    public static final FieldType<byte[]> BINARY = new FieldType<byte[]>("BINARY") {

        @Override
        public byte[] fromString(String stringValue) {
            ensureValueIsNotNull(stringValue);
            try {
                return BaseEncoding.base64().decode(stringValue);
            } catch (IllegalArgumentException e) {
                throw createConversionException(stringValue, "byte[]");
            }
        }

        @Override
        public String toString(byte[] value) {
            ensureValueIsNotNull(value);
            return BaseEncoding.base64().encode(value);
        }

    };

    /**
     * FieldType for the Scim type Reference (actual type is {@link URI})
     */
    public static final FieldType<URI> REFERENCE = new FieldType<URI>("REFERENCE") {

        @Override
        public URI fromString(String stringValue) {
            ensureValueIsNotNull(stringValue);
            try {
                return new URI(stringValue);
            } catch (URISyntaxException e) {
                throw createConversionException(stringValue, "URI");
            }
        }

        @Override
        public String toString(URI value) {
            ensureValueIsNotNull(value);
            return value.toString();
        }

    };

    protected IllegalArgumentException createConversionException(String stringValue, String targetType) {
        return new IllegalArgumentException("The value " + stringValue + " cannot be converted into a " + targetType
                + ".");
    }

    protected void ensureValueIsNotNull(Object value) {
        checkArgument(value != null, "The given value cannot be null.");
    }

}
