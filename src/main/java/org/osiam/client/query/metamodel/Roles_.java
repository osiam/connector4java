package org.osiam.client.query.metamodel;
/*
 * for licensing see the file license.txt.
 */

/**
 * all role atttributes from a User
 */
 public abstract class Roles_ {

    private Roles_(){}

    public static final StringAttribute value = new StringAttribute("roles.value");
    public static final StringAttribute type = new StringAttribute("roles.type");
}
