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

package org.osiam.client.query.metamodel;

/**
 * all attributes from a User
 */
public abstract class User_ {

    private User_(){}
    public static final StringAttribute id = new StringAttribute("id"); // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute externalId = new StringAttribute("externalId");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute userName = new StringAttribute("userName");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute displayName = new StringAttribute("displayName");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute nickName = new StringAttribute("nickName");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute profileUrl = new StringAttribute("profileUrl"); // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute title = new StringAttribute("title");       // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute userType = new StringAttribute("userType");    // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute preferredLanguage = new StringAttribute("preferredLanguage");// NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute locale = new StringAttribute("locale");     // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute timezone = new StringAttribute("timezone");   // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute active = new StringAttribute("active");// NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'

    /**
     * all meta attributes from a User
     */
    public abstract static class Meta {
        public static final StringAttribute resourceType = new StringAttribute("meta.resourceType");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final DateAttribute created = new DateAttribute("meta.created");   // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final DateAttribute lastModified = new DateAttribute("meta.lastModified"); // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute location = new StringAttribute("meta.location");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute version = new StringAttribute("meta.version");   // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    }

    /**
     * all name attributes from a User
     */
    public abstract static class Name {
        public static final StringAttribute formatted = new StringAttribute("name.formatted");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute familyName = new StringAttribute("name.familyName"); // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute givenName = new StringAttribute("name.givenName"); // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    }

    /**
     * all email attributes from a User
     */
    public abstract static class Emails {
        public static final StringAttribute value = new StringAttribute("emails.value");     // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute type = new StringAttribute("emails.type");   // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    }

    /**
     * all ims attributes from a User
     */
    public abstract static class Ims {
        public static final StringAttribute value = new StringAttribute("ims.value"); // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute type = new StringAttribute("ims.type");   // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    }

    /**
     * all photo attributes from a User
     */
    public abstract static class Photos {
        public static final StringAttribute value = new StringAttribute("photos.value");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute type = new StringAttribute("photos.type");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    }

    /**
     * address attribute from User
     */
    public abstract static class Addresses {
        public static final StringAttribute type = new StringAttribute("addresses.type");    // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute streetAddress = new StringAttribute("addresses.streetAddress");   // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute locality = new StringAttribute("addresses.locality");     // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute region = new StringAttribute("addresses.region");        // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute postalCode = new StringAttribute("addresses.postalCode");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute country = new StringAttribute("Address.country");        // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute formatted = new StringAttribute("addresses.formatted");    // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute primary = new StringAttribute("addresses.primary");         // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute operation = new StringAttribute("addresses.operation");     // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    }

    /**
     * all phone number attributes from a User
     */
    public abstract static  class PhoneNumbers {
        public static final StringAttribute value = new StringAttribute("phoneNumbers.value");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute type = new StringAttribute("phoneNumbers.type"); // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    }

    /**
     * all group attributes from a User
     */
    public abstract static  class Groups {
        public static final StringAttribute value = new StringAttribute("groups.value"); // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute type = new StringAttribute("groups.type");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    }

    /**
     * all entitlements attributes from a User
     */
    public abstract static class Entitlements {
        public static final StringAttribute value = new StringAttribute("entitlements.value");   // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute type = new StringAttribute("entitlements.type");    // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    }

    /**
     * all role attributes from a User
     */
    public abstract static class Roles_ {
       public static final StringAttribute value = new StringAttribute("roles.value"); // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
       public static final StringAttribute type = new StringAttribute("roles.type");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    }
}
