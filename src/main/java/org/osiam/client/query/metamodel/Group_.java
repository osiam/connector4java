package org.osiam.client.query.metamodel;
/*
* for licensing see the file license.txt.
*/

/**
 * all fields from a Group
 */
public abstract class Group_ {

    private Group_(){}

    public static final StringAttribute id = new StringAttribute("id");    // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute displayName = new StringAttribute("displayName");      // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute externalId = new StringAttribute("externalId");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'

    /**
     * all meta attributes from a User
     */
    public abstract static class Meta {
        public static final StringAttribute resourceType = new StringAttribute("meta.resourceType");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final DateAttribute created = new DateAttribute("meta.created");   // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final DateAttribute lastModified = new DateAttribute("meta.lastModified"); // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute location = new StringAttribute("meta.location");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute version = new StringAttribute("meta.version");   // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    }

    /**
     * all Member attributes from a Group
     */
    public abstract static class Members {
        public static final StringAttribute value = new StringAttribute("members.value");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute type = new StringAttribute("members.type");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    }
}
