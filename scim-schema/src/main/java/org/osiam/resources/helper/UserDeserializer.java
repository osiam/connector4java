package org.osiam.resources.helper;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.type.JavaType;
import org.osiam.resources.scim.User;

public class UserDeserializer extends StdDeserializer<User> {

    public UserDeserializer(Class<?> vc) {
        super(vc);
    }

    public UserDeserializer(JavaType valueType) {
        super(valueType);
    }

    @Override
    public User deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return null;
    }

}
