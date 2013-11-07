package org.osiam.resources.scim;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;

import org.osiam.resources.scim.extension.FieldType;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * The extension class models a deserialized view of schema extensions as
 * specified by the scim 2.0 specification.
 */
public class Extension {

    private static final Function<FieldTypeAndValue, String> MAP_FIELDTYPEANDVALUE_TO_STRING = new Function<FieldTypeAndValue, String>() {
        @Override
        public String apply(@Nonnull FieldTypeAndValue fieldTypeAndValue) {
            return fieldTypeAndValue.value;
        }
    };
    
    @JsonIgnore
    private String urn;

    private Map<String, FieldTypeAndValue> fields = new HashMap<>();

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
     * @param fieldType The type of the field.
     * @return The value for the field with the given name.
     * @throws NoSuchElementException   if this schema does not contain a field of the given name.
     * @throws IllegalArgumentException if the given field is null or an empty string or if the fieldType is null.
     */
    public <T> T getField(String field, FieldType<T> fieldType) {
        if (field == null || field.isEmpty()) {
            throw new IllegalArgumentException("Invalid field name");
        }
        if (fieldType == null) {
            throw new IllegalArgumentException("Invalid field type");
        }

        if (!isFieldPresent(field)) {
            throw new NoSuchElementException("Field " + field + " not valid in this extension");
        }

        return fieldType.fromString(fields.get(field).value);
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
        fields.put(field, new FieldTypeAndValue(FieldType.STRING, value));
    }

    public void addOrUpdateField(String field, String value) {
        addOrUpdateField(field, value, FieldType.STRING);
    }

    public void addOrUpdateField(String field, Boolean value) {
        addOrUpdateField(field, value, FieldType.BOOLEAN);
    }

    public void addOrUpdateField(String field, byte[] value) {
        addOrUpdateField(field, value, FieldType.BINARY);
    }

    public void addOrUpdateField(String field, BigInteger value) {
        addOrUpdateField(field, value, FieldType.INTEGER);
    }

    public void addOrUpdateField(String field, BigDecimal value) {
        addOrUpdateField(field, value, FieldType.DECIMAL);
    }

    public void addOrUpdateField(String field, Date value) {
        addOrUpdateField(field, value, FieldType.DATE_TIME);
    }

    public void addOrUpdateField(String field, URI value) {
        addOrUpdateField(field, value, FieldType.REFERENCE);
    }

    public <T> void addOrUpdateField(String field, T value, FieldType<T> type) {
        if (field == null || field.isEmpty()) {
            throw new IllegalArgumentException("Invalid field name");
        }
        if (value == null) {
            throw new IllegalArgumentException("Invalid value");
        }
        fields.put(field, new FieldTypeAndValue(type, type.toString(value)));
    }

    /**
     * Provide a unmodifiable view on the entries in this schema as a map.
     *
     * @return The Entries of this schema as an map.
     */
    @JsonAnyGetter
    @Deprecated
    public Map<String, String> getAllFields() {
        return Maps.transformValues(fields, MAP_FIELDTYPEANDVALUE_TO_STRING);
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

    private static final class FieldTypeAndValue {
        private final FieldType<?> type;
        private final String value;

        private FieldTypeAndValue(FieldType<?> type, String value) {
            this.type = type;
            this.value = value;
        }
    }
}
