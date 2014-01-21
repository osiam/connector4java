/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.osiam.resources.scim;

public class Email extends MultiValuedAttributeNew
        implements MultiValuedAttributeWithValue, MultiValuedAttributeWithDisplay {

    private String value;
    private String display;
    private Boolean primary;
    private String type;

    public Email(Builder builder) {
        super(builder);
        this.value = builder.value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDisplay() {
        return display;
    }

    public static class Builder extends MultiValuedAttributeNew.Builder
            implements MultiValuedAttributeWithValue.Builder,
            MultiValuedAttributeWithDisplay.Builder,
            MultiValuedAttributeWithType.Builder{

        private String value;
        private String display;
        private Email.Type type;

        @Override
        public Email build() {
            return new Email(this);
        }

        @Override
        public Builder setType(
                Email.Type type) {
            this.type = type;
            return this;
        }

        @Override
        public <T extends org.osiam.resources.scim.MultiValuedAttributeWithDisplay.Builder> T setDisplay(String display) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T extends org.osiam.resources.scim.MultiValuedAttributeWithValue.Builder> T setValue(String value) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T extends MultiValuedAttributeNew> T build() {
            // TODO Auto-generated method stub
            return null;
        }


  




    }

    /**
     * Represents an email type. Canonical values are available as static constants.
     */
    public static class Type extends MultiValuedAttributeType {
        public static final Type WORK = new Type("work");
        public static final Type HOME = new Type("home");
        public static final Type OTHER = new Type("other");

        public Type(String value) {
            super(value);
        }
    }

}