package org.osiam.resources.scim;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MemberRef extends MultiValuedAttribute {

    @JsonProperty("$ref")
    private String reference;

    /**
     * For Jackson
     */
    private MemberRef() {
    }

    private MemberRef(Builder builder) {
        super(builder);
        reference = builder.reference;
    }

    public String getReference() {
        return reference;
    }

    public static class Builder extends MultiValuedAttribute.Builder {
        private String reference;

        public MemberRef.Builder setReference(String reference) {
            this.reference = reference;
            return this;
        }

        public MemberRef build() {
            return new MemberRef(this);
        }

        @Override
        public MemberRef.Builder setValue(String value) {
            super.setValue(value);
            return this;
        }

        @Override
        public MemberRef.Builder setDisplay(String display) {
            super.setDisplay(display);
            return this;
        }

        @Override
        public MemberRef.Builder setPrimary(Boolean primary) {
            super.setPrimary(primary);
            return this;
        }

        @Override
        public MemberRef.Builder setType(String type) {
            super.setType(type);
            return this;
        }

        @Override
        public MemberRef.Builder setOperation(String operation) {
            super.setOperation(operation);
            return this;
        }

    }
}
