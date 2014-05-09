package helper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.osiam.client.oauth.Scope;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class ScopeDeserializer extends JsonDeserializer<Set<Scope>> {

    @Override
    public Set<Scope> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
            JsonProcessingException {
        String text = jp.getText();
        return parseParameterList(text);
    }

    private Set<Scope> parseParameterList(String values) {
        Set<Scope> scopeResult = new HashSet<Scope>();
        if (values != null && values.trim().length() > 0) {
            String[] scopes = values.split("\\s+");
            for (String scope : scopes) {
                scopeResult.add(new Scope(scope));
            }
        }
        return scopeResult;
    }
}
