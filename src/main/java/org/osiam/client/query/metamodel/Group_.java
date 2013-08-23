package org.osiam.client.query.metamodel;
/*
* for licensing see the file license.txt.
*/

/**
 * all fields from a Group
 */
public abstract class Group_ {

    private Group_(){}

    public static volatile Meta_ meta;  // NOSONAR - false-positive from clover; visibility can't be private
    public static final StringAttribute id = new StringAttribute("id");    // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute displayName = new StringAttribute("displayName");      // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static volatile Members_ members;      // NOSONAR - false-positive from clover; visibility can't be private
}
