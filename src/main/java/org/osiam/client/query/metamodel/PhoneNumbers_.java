package org.osiam.client.query.metamodel;
/*
 * for licensing see the file license.txt.
 */

/**
 * all phone number atttributes from a User
 */
 public abstract class PhoneNumbers_ {

    private  PhoneNumbers_(){}

    public static final StringAttribute value = new StringAttribute("phoneNumbers.value");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute type = new StringAttribute("phoneNumbers.type"); // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
}
