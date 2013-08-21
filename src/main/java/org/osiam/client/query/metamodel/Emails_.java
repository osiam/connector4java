package org.osiam.client.query.metamodel;
/*
* for licensing see the file license.txt.
*/

/**
 * all email atttributes from a User
 */
 public abstract class Emails_ {

    private Emails_(){}

    public static final StringAttribute value = new StringAttribute("emails.value");
    public static final StringAttribute type = new StringAttribute("emails.type");
}
