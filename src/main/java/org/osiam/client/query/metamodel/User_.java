package org.osiam.client.query.metamodel;
/*
* for licensing see the file license.txt.
*/

/**
 * all atttributes from a User
 */
public abstract class User_ {

    private User_(){}
    public static final StringAttribute id = new StringAttribute("id"); // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute externalId = new StringAttribute("externalId");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static volatile Meta_ meta;    // NOSONAR - false-positive from clover; visibility can't be private
    public static final StringAttribute userName = new StringAttribute("userName");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static volatile Name_ name;   // NOSONAR - false-positive from clover; visibility can't be private
    public static final StringAttribute displayName = new StringAttribute("displayName");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute nickName = new StringAttribute("nickName");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute profileUrl = new StringAttribute("profileUrl"); // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute title = new StringAttribute("title");       // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute userType = new StringAttribute("userType");    // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute preferredLanguage = new StringAttribute("preferredLanguage");// NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute locale = new StringAttribute("locale");     // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute timezone = new StringAttribute("timezone");   // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute active = new StringAttribute("active");// NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static volatile Emails_ emails;      // NOSONAR - false-positive from clover; visibility can't be private
    public static volatile PhoneNumbers_ phoneNumber;     // NOSONAR - false-positive from clover; visibility can't be private
    public static volatile Ims_ ims;       // NOSONAR - false-positive from clover; visibility can't be private
    public static volatile Photos_ photos;      // NOSONAR - false-positive from clover; visibility can't be private
    public static volatile Addresses_ addresses;  // NOSONAR - false-positive from clover; visibility can't be private
    public static volatile UserGroups_ groups;      // NOSONAR - false-positive from clover; visibility can't be private
    public static volatile Entitlements_ entitlements;   // NOSONAR - false-positive from clover; visibility can't be private
    public static volatile Roles_ toles;    // NOSONAR - false-positive from clover; visibility can't be private

}
