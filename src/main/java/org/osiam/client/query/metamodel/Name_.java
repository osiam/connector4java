package org.osiam.client.query.metamodel;
/*
* for licensing see the file license.txt.
*/

/**
 * all name atttributes from a User
 */
public class Name_ {

    private  Name_(){}

    public static final StringAttribute formatted = new StringAttribute("name.formatted");
    public static final StringAttribute familyName = new StringAttribute("name.familyName");
    public static final StringAttribute givenName = new StringAttribute("name.givenName");
}
