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
 * this enum like class is used to define and to convert from/toString all extension fields
 * which are used in the scim User
 * 
 */
public abstract class FieldType<T> {

	private String name;

    private FieldType(String name) {
        this.name = name;
    }

    /**
     * converts the given String into the actual FieldType
     * @param stringValue  value to be converted
     * @return given String value as the actual Type
     */
    public abstract T fromString(String stringValue);

    /**
     * 
     * @param value to be converted into a String
     * @return the given value as String
     */
    public abstract String toString(T value);

    /**
     * 
     * @return the name of the FieldType
     */
    public final String getName() {
        return name;
    }

    /**
     * the name of the FieldType
     */
    @Override
    public String toString() {
        return getName();
    }
    
	/**
	 * 
	 * @param name name of the FieldType how it is returned by getName()
	 * @return returns the correct FieldType based on the given name
	 * @throws IllegalArgumentException if the given name is not recognized
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
     * FieldType of the type String
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
     * FieldType of the type Integer
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
     * FieldType of the type Decimal like 12345.67
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
     * FieldType of the type Boolean
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
     * FieldType of the type DateTime in ISO DateTimeFormat with the timeZone UTC
     * like '2011-08-01T18:29:49.000Z'
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
     * FieldType of the type Binary Array
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
     * FieldType of the type Reference represented by a URI
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
