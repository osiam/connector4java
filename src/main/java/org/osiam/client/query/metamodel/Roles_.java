package org.osiam.client.query.metamodel;
/*
 * for licensing see the file license.txt.
 */

/**
 * all role atttributes from a User
 */
 public abstract class Roles_ {

    private Roles_(){}

    public static final StringAttribute value = new StringAttribute("roles.value"); // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute type = new StringAttribute("roles.type");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
}
