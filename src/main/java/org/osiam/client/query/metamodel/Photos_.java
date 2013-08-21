package org.osiam.client.query.metamodel;
/*
 * for licensing see the file license.txt.
 */

/**
 * all photo atttributes from a User
 */
 public abstract class Photos_ {

    private Photos_(){}

    public static final StringAttribute value = new StringAttribute("photos.value");
    public static final StringAttribute type = new StringAttribute("photos.type");
}
