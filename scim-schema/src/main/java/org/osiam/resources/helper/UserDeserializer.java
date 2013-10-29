package org.osiam.resources.helper;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
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
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        User user = mapper.readValue(rootNode.toString(), User.class);

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
            Map<String, String> map = mapper.readValue(extensionNode.toString(), new TypeReference<Map<String, String>>() {
            });
            builder.addExtension(urn, new Extension(map));
        }
        return builder.build();
    }

}
