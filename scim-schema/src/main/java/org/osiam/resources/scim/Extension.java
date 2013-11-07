package org.osiam.resources.scim;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.osiam.resources.helper.ExtensionSerializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;


/**
 * The extension class models a deserialized view of schema extensions as
 * specified by the scim 2.0 specification.
 */
@JsonSerialize(using = ExtensionSerializer.class)
public class Extension {

    private static final Function<Field, String> MAP_FIELDTYPEANDVALUE_TO_STRING = new Function<Field, String>() {
        @Override
        public String apply(Field fieldTypeAndValue) {
            return fieldTypeAndValue.value;
        }
    };

    @JsonIgnore
    private String urn;

    private Map<String, Field> fields = new HashMap<>();

    /**
     * Default constructor for Jackson
     */
    public Extension() {
    }

    public Extension(String urn) {
        this.urn = urn;
    }

    /**
     * Returns the URN of this extension.
     *
     * @return The URN
     */
    public String getUrn() {
        return urn;
    }

    /**
     * Return the value for the field with a given name and type.
     *
     * @param field     The name of the field to retrieve the value of.
     * @param extensionFieldType The type of the field.
     * @return The value for the field with the given name.
     * @throws NoSuchElementException   if this schema does not contain a field of the given name.
     * @throws IllegalArgumentException if the given field is null or an empty string or if the extensionFieldType is null.
     */
    public <T> T getField(String field, ExtensionFieldType<T> extensionFieldType) {
        if (field == null || field.isEmpty()) {
            throw new IllegalArgumentException("Invalid field name");
        }
        if (extensionFieldType == null) {
            throw new IllegalArgumentException("Invalid field type");
        }

        if (!isFieldPresent(field)) {
            throw new NoSuchElementException("Field " + field + " not valid in this extension");
        }

        return extensionFieldType.fromString(fields.get(field).value);
    }

    /**
     * Update the field with the given name to the given value.
     *
     * @param field The name of the field whose value to set
     * @param value The new value of the field.
     * @throws IllegalArgumentException if the given field is null or does not exists.
     */
    @Deprecated
    public void setField(String field, String value) {
        if (field == null || !fields.containsKey(field)) {
            throw new IllegalArgumentException("Invalid field name");
        }
        fields.put(field, new Field(ExtensionFieldType.STRING, value));
    }

    public void addOrUpdateField(String field, String value) {
        addOrUpdateField(field, value, ExtensionFieldType.STRING);
    }

    public void addOrUpdateField(String field, Boolean value) {
        addOrUpdateField(field, value, ExtensionFieldType.BOOLEAN);
    }

    public void addOrUpdateField(String field, byte[] value) {
        addOrUpdateField(field, value, ExtensionFieldType.BINARY);
    }

    public void addOrUpdateField(String field, BigInteger value) {
        addOrUpdateField(field, value, ExtensionFieldType.INTEGER);
    }

    public void addOrUpdateField(String field, BigDecimal value) {
        addOrUpdateField(field, value, ExtensionFieldType.DECIMAL);
    }

    public void addOrUpdateField(String field, Date value) {
        addOrUpdateField(field, value, ExtensionFieldType.DATE_TIME);
    }

    public void addOrUpdateField(String field, URI value) {
        addOrUpdateField(field, value, ExtensionFieldType.REFERENCE);
    }

    public <T> void addOrUpdateField(String field, T value, ExtensionFieldType<T> type) {
        if (field == null || field.isEmpty()) {
            throw new IllegalArgumentException("Invalid field name");
        }
        if (value == null) {
            throw new IllegalArgumentException("Invalid value");
        }
        fields.put(field, new Field(type, type.toString(value)));
    }

    /**
     * Provide a unmodifiable view on the entries in this schema as a map.
     *
     * @return The Entries of this schema as an map.
     */
    @JsonIgnore
    public Map<String, Field> getAllFields() {
        return ImmutableMap.copyOf(fields);
    }

    /**
     * Checks if the given field is present in this extension because not every
     * field is mandatory (according to scim 2.0 spec).
     *
     * @param field Name of the field to check
     * @return true if the given field is present, else false
     */
    public boolean isFieldPresent(String field) {
        return fields.containsKey(field);
    }

    public static final class Field {
        private final ExtensionFieldType<?> type;
        private final String value;

        public Field(ExtensionFieldType<?> type, String value) {
            this.type = type;
            this.value = value;
        }

        public ExtensionFieldType<?> getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((type == null) ? 0 : type.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Field other = (Field) obj;
            if (type == null) {
                if (other.type != null)
                    return false;
            } else if (!type.equals(other.type))
                return false;
            if (value == null) {
                if (other.value != null)
                    return false;
            } else if (!value.equals(other.value))
                return false;
            return true;
        }

    }
}
