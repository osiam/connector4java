package org.osiam.resources.scim.meta;

public class SetAttribute<O, T> extends Attribute<O, T> {
    public SetAttribute(String name, Class<O> ownerClazz, Class<T> typeClazz) {
        super(name, ownerClazz, typeClazz);
    }
}