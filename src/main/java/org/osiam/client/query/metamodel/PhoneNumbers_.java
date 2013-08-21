package org.osiam.client.query.metamodel;
/*
 * for licensing see the file license.txt.
 */

/**
 * all phone number atttributes from a User
 */
 public abstract class PhoneNumbers_ {

    private  PhoneNumbers_(){}

    public static final StringAttribute value = new StringAttribute("phoneNumbers.value");
    public static final StringAttribute type = new StringAttribute("phoneNumbers.type");
}
