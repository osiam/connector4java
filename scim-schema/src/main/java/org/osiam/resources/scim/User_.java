package org.osiam.resources.scim;

import org.osiam.resources.scim.meta.MapAttribute;
import org.osiam.resources.scim.meta.SetAttribute;
import org.osiam.resources.scim.meta.SingularAttribute;


public class User_ extends Resource_ {

    public static SingularAttribute<User, String> userName = new SingularAttribute<>("userName", User.class, String.class);
    public static SingularAttribute<User, Name> name = new SingularAttribute<>("name", User.class, Name.class);
    public static SingularAttribute<User, String> displayName = new SingularAttribute<>("displayName", User.class, String.class);
    public static SingularAttribute<User, String> nickName = new SingularAttribute<>("nickName", User.class, String.class);
    public static SingularAttribute<User, String> profileUrl = new SingularAttribute<>("profileUrl", User.class, String.class);
    public static SingularAttribute<User, String> title = new SingularAttribute<>("title", User.class, String.class);
    public static SingularAttribute<User, String> userType = new SingularAttribute<>("userType", User.class, String.class);
    public static SingularAttribute<User, String> preferredLanguage = new SingularAttribute<>("preferredLanguage", User.class, String.class);
    public static SingularAttribute<User, String> locale = new SingularAttribute<>("locale", User.class, String.class);
    public static SingularAttribute<User, String> timezone = new SingularAttribute<>("timezone", User.class, String.class);
    public static SingularAttribute<User, Boolean> active = new SingularAttribute<>("active", User.class, Boolean.class);
    public static SingularAttribute<User, String> password = new SingularAttribute<>("password", User.class, String.class);
    public static SetAttribute<User, MultiValuedAttribute> emails = new SetAttribute<>("emails", User.class, MultiValuedAttribute.class);
    public static SetAttribute<User, MultiValuedAttribute> phoneNumbers = new SetAttribute<>("phoneNumbers", User.class, MultiValuedAttribute.class);
    public static SetAttribute<User, MultiValuedAttribute> ims = new SetAttribute<>("ims", User.class, MultiValuedAttribute.class);
    public static SetAttribute<User, MultiValuedAttribute> photos = new SetAttribute<>("photos", User.class, MultiValuedAttribute.class);
    public static SetAttribute<User, Address> addresses = new SetAttribute<>("addresses", User.class, Address.class);
    public static SetAttribute<User, MultiValuedAttribute> groups = new SetAttribute<>("groups", User.class, MultiValuedAttribute.class);
    public static SetAttribute<User, MultiValuedAttribute> entitlements = new SetAttribute<>("entitlements", User.class, MultiValuedAttribute.class);
    public static SetAttribute<User, MultiValuedAttribute> roles = new SetAttribute<>("roles", User.class, MultiValuedAttribute.class);
    public static SetAttribute<User, MultiValuedAttribute> x509Certificates = new SetAttribute<>("x509Certificates", User.class, MultiValuedAttribute.class);
    public static MapAttribute<User, String, Extension> extensions = new MapAttribute<>("extensions", User.class, Extension.class);
}