package org.osiam.resources.scim.meta;

public class MapAttribute<O, K, V> extends Attribute<O, V> {
    public MapAttribute(String name, Class<O> ownerClazz, Class<V> typeClazz) {
        super(name, ownerClazz, typeClazz);
    }
}