package org.osiam.resources.scim;

import org.osiam.resources.scim.meta.SetAttribute;
import org.osiam.resources.scim.meta.SingularAttribute;

public class Group_ extends Resource_ {

    public static SingularAttribute<Group, String> displayName = new SingularAttribute<>("displayName", Group.class, String.class);
    public static SetAttribute<Group, MemberRef> members = new SetAttribute<>("members", Group.class, MemberRef.class);
}