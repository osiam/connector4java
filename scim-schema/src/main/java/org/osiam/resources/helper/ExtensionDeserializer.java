package org.osiam.resources.helper;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;

import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.ExtensionFieldType;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;

/**
 * This class is package-private by intention. If you need to use it, something went wrong.
 */
class ExtensionDeserializer extends StdDeserializer<Extension> {

    private static final long serialVersionUID = 2581146730706177962L;

    private String urn;

    protected ExtensionDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Extension deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        if (urn == null || urn.isEmpty()) {
            throw new IllegalStateException("The URN cannot be null or empty");
        }
        JsonNode rootNode = jp.readValueAsTree();
        if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
            throw new JsonMappingException("Extension is of wrong JSON type");
        }
        Extension extension = new Extension(urn);
        Iterator<Map.Entry<String, JsonNode>> fieldIterator = rootNode.fields();
        while (fieldIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = fieldIterator.next();
            switch (entry.getValue().getNodeType()) {
                case BOOLEAN:
                    handleBoolean(extension, entry);
                    break;
                case STRING:
                    handleString(extension, entry);
                    break;
                case NUMBER:
                    handleNumber(extension, entry);
                    break;
                default:
                    throw new IllegalArgumentException("JSON type not supported: " + entry.getValue().getNodeType());
            }
        }

        return extension;
    }

    private void handleNumber(Extension extension, Map.Entry<String, JsonNode> entry) {

        String stringValue = entry.getValue().asText();
        if (stringValue.contains(".")) {
            BigDecimal value = ExtensionFieldType.DECIMAL.fromString(stringValue);
            extension.addOrUpdateField(entry.getKey(), value);
        } else {
            BigInteger value = ExtensionFieldType.INTEGER.fromString(stringValue);
            extension.addOrUpdateField(entry.getKey(), value);
        }
    }


    private void handleString(Extension extension, Map.Entry<String, JsonNode> entry) {
        String value = ExtensionFieldType.STRING.fromString(entry.getValue().asText());
        extension.addOrUpdateField(entry.getKey(), value);
    }

    private void handleBoolean(Extension extension, Map.Entry<String, JsonNode> entry) {
        Boolean value = ExtensionFieldType.BOOLEAN.fromString(entry.getValue().asText());
        extension.addOrUpdateField(entry.getKey(), value);
    }

    void setUrn(String urn) {
        this.urn = urn;
    }
}