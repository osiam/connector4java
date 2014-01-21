package org.osiam.resources.scim;


public interface MultiValuedAttributeWithDisplay {

    /**
     * Gets the human readable name, primarily used for display purposes.
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2">SCIM core schema 2.0, section
     * 3.2</a>
     * </p>
     * 
     * @return the display attribute
     */
    public String getDisplay();

    interface Builder {

        /**
         * Sets the human readable name (See {@link MultiValuedAttribute#getDisplay()}).
         * 
         * @param display
         *            a human readable name
         * @return the builder itself
         */
        public <T extends Builder> T setDisplay(String display);

    }
}