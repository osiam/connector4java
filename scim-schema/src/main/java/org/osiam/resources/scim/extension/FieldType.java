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

public abstract class FieldType<T> {

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

    private String name;

    private FieldType(String name) {
        this.name = name;
    }

    protected IllegalArgumentException createConversionException(String stringValue, String targetType) {
        return new IllegalArgumentException("The value " + stringValue + " cannot be converted into a " + targetType
                + ".");
    }

    protected void ensureValueIsNotNull(Object value) {
        checkArgument(value != null, "The given value cannot be null.");
    }

    public abstract T fromString(String stringValue);

    public abstract String toString(T value);

    public final String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
