package org.osiam.resources.scim;


public abstract class MultiValuedAttributeNew {

    private String operation;
    
    public MultiValuedAttributeNew(Builder builder) {
        this.operation = builder.operation;
    }
    
    /**
     * Gets the operation applied during a PATCH request.
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2">SCIM core schema 2.0, section
     * 3.2</a>
     * </p>
     * 
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }
    
    /**
     * Builder class that is used to build {@link MultiValuedAttribute} instances
     */
    public static abstract class Builder {

        private String operation;

        /**
         * Sets the operation (See {@link MultiValuedAttribute#getOperation()}).
         * 
         * @param operation
         *            only "delete" is supported at the moment
         * @return the builder itself
         */
        public Builder setOperation(String operation) {
            this.operation = operation;
            return this;
        }

        /**
         * Builds a MultiValuedAttribute Object with the given parameters
         * 
         * @return a new MultiValuedAttribute Object
         */
        public abstract <T extends MultiValuedAttributeNew> T build();
    }
}
