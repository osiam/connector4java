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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.Strings;

/**
 * This class represents a multi valued attribute.
 * 
 * <p>
 * For more detailed information please look at the <a href=
 * "http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2">SCIM
 * core schema 2.0, section 3.2</a>
 * </p>
 */
@JsonInclude(Include.NON_EMPTY)
public abstract class MultiValuedAttributeNew {

	private String operation;
	private String value;
	private String display;
	private String type;
	private boolean primary;

	/**
	 * Default constructor for Jackson
	 */
	protected MultiValuedAttributeNew() {
	}

	protected MultiValuedAttributeNew(Builder builder) {
		this.value = builder.value;
		this.display = builder.display;
		this.primary = builder.primary;
		this.type = builder.type;
		this.operation = builder.operation;
	}

	/**
	 * Gets the attribute's significant value; e.g., the e-mail address, phone
	 * number etc.
	 * 
	 * <p>
	 * For more detailed information please look at the <a href=
	 * "http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2"
	 * >SCIM core schema 2.0, section 3.2</a>
	 * </p>
	 * 
	 * @return the value of the actual multi value attribute
	 */
	protected String getValue() {
		return value;
	}

	/**
	 * Gets the human readable name, primarily used for display purposes.
	 * 
	 * <p>
	 * For more detailed information please look at the <a href=
	 * "http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2"
	 * >SCIM core schema 2.0, section 3.2</a>
	 * </p>
	 * 
	 * @return the display attribute
	 */
	protected String getDisplay() {
		return display;
	}

	/**
	 * Gets a Boolean value indicating the 'primary' or preferred attribute
	 * value for this attribute.
	 * 
	 * <p>
	 * For more detailed information please look at the <a href=
	 * "http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2"
	 * >SCIM core schema 2.0, section 3.2</a>
	 * </p>
	 * 
	 * @return the primary attribute
	 */
	protected boolean isPrimary() {
		return primary;
	}

	/**
	 * Gets the type of the attribute.
	 * 
	 * <p>
	 * For more detailed information please look at the <a href=
	 * "http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2"
	 * >SCIM core schema 2.0, section 3.2</a>
	 * </p>
	 * 
	 * @return the actual type
	 */
	protected String getType() {
		return type;
	}

	/**
	 * Gets the operation applied during a PATCH request.
	 * 
	 * <p>
	 * For more detailed information please look at the <a href=
	 * "http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2"
	 * >SCIM core schema 2.0, section 3.2</a>
	 * </p>
	 * 
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * Builder class that is used to build {@link MultiValuedAttributeNew}
	 * instances
	 */
	public abstract static class Builder<B extends MultiValuedAttributeNew.Builder> {

		private String operation;
		private String value;
		private String display;
		private String type;
		private boolean primary;
		private B builder;

		protected void setBuilder(B self) {
			this.builder = self;
		}

		/**
		 * Sets the attribute's significant value (See
		 * {@link MultiValuedAttributeNew#getValue()}).
		 * 
		 * @param value
		 *            the value attribute
		 * @return the builder itself
		 */
		protected B setValue(String value) {
			this.value = value;
			return builder;
		}

		/**
		 * Sets the human readable name (See
		 * {@link MultiValuedAttributeNew#getDisplay()}).
		 * 
		 * @param display
		 *            a human readable name
		 * @return the builder itself
		 */
		protected B setDisplay(String display) {
			this.display = display;
			return builder;
		}

		/**
		 * Sets the primary attribute (See
		 * {@link MultiValuedAttributeNew#isPrimary()}).
		 * 
		 * @param the
		 *            primary attribute
		 * @return the builder itself
		 */
		protected B setPrimary(boolean primary) {
			this.primary = primary;
			return builder;
		}

		/**
		 * Sets the label indicating the attribute's function (See
		 * {@link MultiValuedAttributeNew#getType()}).
		 * 
		 * @param type
		 *            the type of the attribute
		 * @return the builder itself
		 */
		protected B setType(Type type) {
			this.type = type;
			return builder;
		}

		/**
		 * Sets the operation (See
		 * {@link MultiValuedAttributeNew#getOperation()}).
		 * 
		 * @param operation
		 *            only "delete" is supported at the moment
		 * @return the builder itself
		 */
		public B setOperation(String operation) {
			this.operation = operation;
			return builder;
		}

		/**
		 * Builds a MultiValuedAttribute Object with the given parameters
		 * 
		 * @return a new MultiValuedAttribute Object
		 */
		protected abstract MultiValuedAttributeNew build();
	}

	public abstract static class Type {

		private final String value;

		protected Type(String value) {
			if (Strings.isNullOrEmpty(value)) {
				throw new IllegalArgumentException(String.format(
						"The value of %s can't be null or empty.", getClass()
								.getName()));
			}
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("MultiValuedAttributeType [value=").append(value)
					.append(", getClass()=").append(getClass()).append("]");
			return builder.toString();
		}

	}
}
