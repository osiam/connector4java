package org.osiam.client.query.metamodel;
/*
* for licensing see the file license.txt.
*/

/**
 * all meta atttributes from a User or a Group
 */
public abstract class Meta_ {

    private Meta_(){}

    public static final StringAttribute resourceType = new StringAttribute("meta.resourceType");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final DateAttribute created = new DateAttribute("meta.created");   // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final DateAttribute lastModified = new DateAttribute("meta.lastModified"); // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute location = new StringAttribute("meta.location");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute version = new StringAttribute("meta.version");   // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'

}
