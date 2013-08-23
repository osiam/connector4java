package org.osiam.client.query.metamodel;
/*
* for licensing see the file license.txt.
*/

/**
 * address attribute from User
 */
public abstract class Addresses_ {

    private Addresses_(){}

    public static final StringAttribute type = new StringAttribute("addresses.type");    // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute streetAddress = new StringAttribute("addresses.streetAddress");   // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute locality = new StringAttribute("addresses.locality");     // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute region = new StringAttribute("addresses.region");        // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute postalCode = new StringAttribute("addresses.postalCode");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute country = new StringAttribute("addresses.country");        // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute formatted = new StringAttribute("addresses.formatted");    // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute primary = new StringAttribute("addresses.primary");         // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute operation = new StringAttribute("addresses.operation");     // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
}
