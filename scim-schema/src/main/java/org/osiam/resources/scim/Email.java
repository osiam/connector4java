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

public class Email extends MultiValuedAttributeNew {

    public Email(Builder builder) {
        super(builder);
    }
    
	@Override
	public String getOperation() {
		return super.getOperation();
	}

	@Override
	public String getValue() {
		return super.getValue();
	}

	@Override
	public String getDisplay() {
		return super.getDisplay();
	}

	@Override
	public boolean isPrimary() {
		return super.isPrimary();
	}

	@Override
	protected String getType() {
		return super.getType();
	}

	public static class Builder extends
			MultiValuedAttributeNew.Builder<Email.Builder> {

		public Builder() {
			setBuilder(this);
		}

		@Override
		public Email build() {
			return new Email(this);
		}

		@Override
		public Builder setDisplay(String display) {
			return super.setDisplay(display);

		}

		@Override
		public Builder setValue(String value) {
			return super.setValue(value);
		}

		// @Override
		public Builder setType(Type type) {
			// super.setType(type);
			return this;
		}

		@Override
		public Builder setPrimary(boolean primary) {
			super.setPrimary(primary);
			return this;
		}
	}

    /**
     * Represents an email type. Canonical values are available as static constants.
     */
    public static class Type extends MultiValuedAttributeNew.Type {
        public static final Type WORK = new Type("work");
        public static final Type HOME = new Type("home");
        public static final Type OTHER = new Type("other");

        public Type(String value) {
            super(value);
        }
    }

}