package helper;

import java.io.IOException;
import java.util.Set;

import org.osiam.client.oauth.Scope;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ScopeSerializer extends JsonSerializer<Set<Scope>> {

    @Override
    public void serialize(Set<Scope> value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonProcessingException {
        if (value != null && !value.isEmpty()) {
            StringBuffer scopeResult = new StringBuffer();
            for (Scope scope : value) {
                scopeResult.append(scope).append(" ");
            }
            jgen.writeString(scopeResult.toString().trim());
        }
    }
}
