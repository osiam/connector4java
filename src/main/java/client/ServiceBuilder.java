package client;

import client.exception.ConnectionInitializationException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * The ServiceBuilder is responsible for providing the respective OSIAM-service that allow access to the
 * respective entities stored in the OSIAM installation.
 */
public class ServiceBuilder {

    public static OsiamUserService buildUserService(URI endpoint, String clientId, URI redirectURL, String clientSecret) {
        final WebResource userWebResource;
        try {
            userWebResource = createWebResource(new URI(endpoint.toString() + "/User/"));
        } catch (URISyntaxException e) {
            throw new ConnectionInitializationException("Unable to setup connection", e);
        }
        return new OsiamUserService(userWebResource);
    }

    private static WebResource createWebResource(URI path) {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(config);
        WebResource resource = client.resource(path);
        resource.accept(MediaType.APPLICATION_JSON);
        return resource;
    }
}
