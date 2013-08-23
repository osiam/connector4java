package org.osiam.client.query.metamodel;
/*
* for licensing see the file license.txt.
*/

/**
 * all Member atttributes from a Group
 */
public abstract class Members_ {

    private Members_(){}

    public static final StringAttribute value = new StringAttribute("members.value");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
}
