package org.osiam.client.query.metamodel;


public abstract class User_ {

    private User_(){}
    public static final StringAttribute id = new StringAttribute("id");
    public static final StringAttribute externalId = new StringAttribute("externalId");
    public static volatile Meta_ meta;
    public static final StringAttribute userName = new StringAttribute("userName");
    public static volatile Name_ name;
    public static final StringAttribute displayName = new StringAttribute("displayName");
    public static final StringAttribute nickName = new StringAttribute("nickName");
    public static final StringAttribute profileUrl = new StringAttribute("profileUrl");
    public static final StringAttribute title = new StringAttribute("title");
    public static final StringAttribute userType = new StringAttribute("userType");
    public static final StringAttribute preferredLanguage = new StringAttribute("preferredLanguage");
    public static final StringAttribute locale = new StringAttribute("locale");
    public static final StringAttribute timezone = new StringAttribute("timezone");
    public static final StringAttribute active = new StringAttribute("active");
    public static volatile Emails_ emails;
    public static volatile PhoneNumbers_ phoneNumber;
    public static volatile Ims_ ims;
    public static volatile Photos_ photos;
    public static volatile Addresses_ addresses;
    public static volatile UserGroups_ groups;
    public static volatile Entitlements_ entitlements;
    public static volatile Roles_ toles;

}
