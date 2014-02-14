package org.osiam.resources.scim.meta;

import java.lang.reflect.Field;

public class Attribute<O, T> {

    private final String name;
    private final Field field;
    private final Class<O> ownerClazz;
    private final Class<T> typeClazz;

    public Attribute(String name, Class<O> ownerClazz, Class<T> typeClazz) {
        this.name = name;
        this.ownerClazz = ownerClazz;
        this.typeClazz = typeClazz;

   
        try {
            this.field = ownerClazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Cannot initialize meta model attribute " + name + " for class "
                    + ownerClazz.getSimpleName());
        }
    }

    public String getName() {
        return name;
    }

    public Field getField() {
        return field;
    }
}