package org.osiam.resources.scim;

import org.osiam.resources.scim.MultiValuedAttribute.Builder;

public interface MultiValuedAttributeWithType {

    /**
     * Gets the type of the attribute.
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2">SCIM core schema 2.0, section
     * 3.2</a>
     * </p>
     * 
     * @return the actual type
     */
    public <T extends MultiValuedAttributeType> T getType();

    interface Builder {

        /**
         * Sets the label indicating the attribute's function (See {@link MultiValuedAttribute#getType()}).
         * 
         * @param type
         *            the type of the attribute
         * @return the builder itself
         */
        
        public <T extends Builder> T setType(MultiValuedAttributeType type);
    }
}