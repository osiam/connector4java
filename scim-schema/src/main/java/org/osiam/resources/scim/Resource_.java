package org.osiam.resources.scim;

import org.osiam.resources.scim.meta.SetAttribute;
import org.osiam.resources.scim.meta.SingularAttribute;

public class Resource_ {  

    protected Resource_(){
    }

    public static SingularAttribute<Resource, String> id = new SingularAttribute<>("id", Resource.class, String.class); // NOSONAR : not finished yet
    public static SingularAttribute<Resource, String> externalId = new SingularAttribute<>("externalId", Resource.class, String.class); // NOSONAR : not finished yet
    public static SingularAttribute<Resource, Meta> meta = new SingularAttribute<>("meta", Resource.class, Meta.class); // NOSONAR : not finished yet
    public static SetAttribute<Resource, String> schemas = new SetAttribute<>("schemas", Resource.class, String.class); // NOSONAR : not finished yet

}