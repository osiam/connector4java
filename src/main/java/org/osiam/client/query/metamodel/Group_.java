package org.osiam.client.query.metamodel;

public abstract class Group_ {

    private Group_(){}

    public static volatile Meta_ meta;
    public static final StringAttribute id = new StringAttribute("id");
    public static final StringAttribute displayName = new StringAttribute("displayName");
    public static volatile Members_ members;
}
