package org.osiam.resources.scim;

import org.codehaus.jackson.annotate.JsonUnwrapped;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * The extension class models a deserialized view of schema extensions as specified by the scim 2.0 specification.
 */
public class Extension {

    @JsonUnwrapped
    private Map<String, String> fields = new HashMap<>();

    /**
     * Default constructor for Jackson
     */
    public Extension () {
    }

    public Extension(Map<String, String> fields) {
        this.fields = new HashMap<>(fields);
    }
    /**
     * Return the value for the field with a given name
     * @param field The name of the field to retrieve the value of.
     * @return The value for the field with the given name.
     * @throws NoSuchElementException if this schema does not contain a field of the given name.
     * @throws IllegalArgumentException if the given field is null or an empty string.
     */
    public String getField(String field){
        if (field == null || field.isEmpty()){
            throw new IllegalArgumentException("Invalid field name");
        }
        if (! fields.containsKey(field)){
            throw new NoSuchElementException("Field " + field + " not valid in this extension");
        }
        return fields.get(field);
    }

    /**
     * Update the field with the given name to the given value.
     * @param field The name of the field whose value to set
     * @param value The new value of the field.
     * @throws IllegalArgumentException if the given field is null or does not exists.
     */
    public void setField(String field, String value){
        if (field == null || !fields.containsKey(field)) {
            throw new IllegalArgumentException("Invalid field name");
        }
        fields.put(field, value);
    }

    /**
     * Provide a unmodifiable view on the entries in this schema as a map.
     * @return The Entries of this schema as an map.
     */
    public Map<String, String> getAllFields(){
        return Collections.unmodifiableMap(fields);
    }
}
