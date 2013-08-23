package org.osiam.client.query.metamodel;
/*
 * for licensing see the file license.txt.
 */

/**
 * all ims atttributes from a User
 */
 public abstract class Ims_ {

    private Ims_(){}

    public static final StringAttribute value = new StringAttribute("ims.value"); // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute type = new StringAttribute("ims.type");   // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
}
