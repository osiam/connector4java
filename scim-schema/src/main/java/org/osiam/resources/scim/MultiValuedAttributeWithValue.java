package org.osiam.resources.scim;

public interface MultiValuedAttributeWithValue {

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
    public abstract String getValue();

    interface Builder {

        /**
         * Sets the attribute's significant value (See {@link MultiValuedAttribute#getValue()}).
         * 
         * @param value
         *            the value attribute
         * @return the builder itself
         */
        public <T extends Builder> T setValue(String value);
    }
}