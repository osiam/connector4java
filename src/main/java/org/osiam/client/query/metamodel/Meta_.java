package org.osiam.client.query.metamodel;
/*
* for licensing see the file license.txt.
*/

/**
 * all meta atttributes from a User or a Group
 */
public abstract class Meta_ {

    private Meta_(){}

    public static final StringAttribute resourceType = new StringAttribute("meta.resourceType");
    public static final DateAttribute created = new DateAttribute("meta.created");
    public static final DateAttribute lastModified = new DateAttribute("meta.lastModified");
    public static final StringAttribute location = new StringAttribute("meta.location");
    public static final StringAttribute version = new StringAttribute("meta.version");

}
