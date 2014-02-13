package org.osiam.resources.scim.meta;

public class SingularAttribute<O, T> extends Attribute<O, T> {
    public SingularAttribute(String name, Class<O> ownerClazz, Class<T> typeClazz) {
        super(name, ownerClazz, typeClazz);
    }
}