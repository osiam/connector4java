package org.osiam.client.query.metamodel;
/*
* for licensing see the file license.txt.
*/

/**
 * all email atttributes from a User
 */
 public abstract class Emails_ {

    private Emails_(){}

    public static final StringAttribute value = new StringAttribute("emails.value");     // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute type = new StringAttribute("emails.type");   // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
}
