package org.osiam.client.query.metamodel;


public abstract class Addresses_ {

    private Addresses_(){}

    public static final StringAttribute type = new StringAttribute("addresses.type");
    public static final StringAttribute streetAddress = new StringAttribute("addresses.streetAddress");
    public static final StringAttribute locality = new StringAttribute("addresses.locality");
    public static final StringAttribute region = new StringAttribute("addresses.region");
    public static final StringAttribute postalCode = new StringAttribute("addresses.postalCode");
    public static final StringAttribute country = new StringAttribute("addresses.country");
    public static final StringAttribute formatted = new StringAttribute("addresses.formatted");
    public static final StringAttribute primary = new StringAttribute("addresses.primary");
    public static final StringAttribute operation = new StringAttribute("addresses.operation");
}
