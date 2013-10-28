package org.osiam.resources.helper;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonLocation;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.osiam.resources.scim.Constants;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.User;

public class UserDeserializer extends StdDeserializer<User> {

    public UserDeserializer(Class<?> valueClass) {
        super(valueClass);
    }

    public UserDeserializer(JavaType valueType) {
        super(valueType);
    }

    @Override
    public User deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode rootNode = jp.readValueAsTree();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        User user = mapper.readValue(rootNode, User.class);

        if (user.getSchemas().size() == 1) {
            return user;
        }

        User.Builder builder = new User.Builder(user);

        for (String urn : user.getSchemas()) {
            if (urn.equals(Constants.CORE_SCHEMA)) {
                continue;
            }

            JsonNode extensionNode = rootNode.get(urn);
            if (extensionNode == null) {
                throw new JsonParseException("Registered extension not present.", JsonLocation.NA);
            }
            Map<String, String> map = mapper.readValue(extensionNode, new TypeReference<Map<String, String>>() {
            });
            builder.addExtension(urn, new Extension(map));
        }
        return builder.build();
    }

}
