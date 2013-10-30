package org.osiam.resources.scim;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * The extension class models a deserialized view of schema extensions as
 * specified by the scim 2.0 specification.
 */
public class Extension {

    @JsonIgnore
    private String urn;

    private Map<String, String> fields = new HashMap<>();

    /**
     * Default constructor for Jackson
     */
    public Extension() {
    }

    public Extension(String urn, Map<String, String> fields) {
        this.urn = urn;
        this.fields = new HashMap<>(fields);
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
     * Return the value for the field with a given name
     * 
     * @param field
     *            The name of the field to retrieve the value of.
     * @return The value for the field with the given name.
     * @throws NoSuchElementException
     *             if this schema does not contain a field of the given name.
     * @throws IllegalArgumentException
     *             if the given field is null or an empty string.
     */
    public String getField(String field) {
        if (field == null || field.isEmpty()) {
            throw new IllegalArgumentException("Invalid field name");
        }
        if (!isFieldPresent(field)) {
            throw new NoSuchElementException("Field " + field + " not valid in this extension");
        }
        return fields.get(field);
    }

    /**
     * Update the field with the given name to the given value.
     * 
     * @param field
     *            The name of the field whose value to set
     * @param value
     *            The new value of the field.
     * @throws IllegalArgumentException
     *             if the given field is null or does not exists.
     */
    public void setField(String field, String value) {
        if (field == null || !fields.containsKey(field)) {
            throw new IllegalArgumentException("Invalid field name");
        }
        fields.put(field, value);
    }

    /**
     * Provide a unmodifiable view on the entries in this schema as a map.
     * 
     * @return The Entries of this schema as an map.
     */
    @JsonAnyGetter
    public Map<String, String> getAllFields() {
        return Collections.unmodifiableMap(fields);
    }

    /**
     * Checks if the given field is present in this extension because not every
     * field is mandatory (according to scim 2.0 spec).
     * 
     * @param field Name of the field to check
     * @return true if the given field is present, else false
     */
    public boolean isFieldPresent(String field) {
        if (!fields.containsKey(field)) {
            return false;
        }

        return true;
    }
}
