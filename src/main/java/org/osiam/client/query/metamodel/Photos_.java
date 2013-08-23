package org.osiam.client.query.metamodel;
/*
 * for licensing see the file license.txt.
 */

/**
 * all photo atttributes from a User
 */
 public abstract class Photos_ {

    private Photos_(){}

    public static final StringAttribute value = new StringAttribute("photos.value");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute type = new StringAttribute("photos.type");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
}
