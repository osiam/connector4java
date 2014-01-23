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

public class Im extends MultiValuedAttributeNew {

	public Im(Builder builder) {
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
			MultiValuedAttributeNew.Builder<Im.Builder> {

		public Builder() {
			setBuilder(this);
		}

		@Override
		public Im build() {
			return new Im(this);
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
	 * Represents an IM type. Canonical values are available as static
	 * constants.
	 */
	public static class Type extends MultiValuedAttributeType {
		public static final Type AIM = new Type("aim");
		public static final Type GTALK = new Type("gtalk");
		public static final Type ICQ = new Type("icq");
		public static final Type XMPP = new Type("xmpp");
		public static final Type MSN = new Type("msn");
		public static final Type SKYPE = new Type("skype");
		public static final Type QQ = new Type("qq");
		public static final Type YAHOO = new Type("yahoo");

		public Type(String value) {
			super(value);
		}
	}

}
