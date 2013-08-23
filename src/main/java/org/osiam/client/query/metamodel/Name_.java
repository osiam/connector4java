package org.osiam.client.query.metamodel;
/*
* for licensing see the file license.txt.
*/

/**
 * all name atttributes from a User
 */
public class Name_ {

    private  Name_(){}

    public static final StringAttribute formatted = new StringAttribute("name.formatted");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute familyName = new StringAttribute("name.familyName"); // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute givenName = new StringAttribute("name.givenName"); // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
}
