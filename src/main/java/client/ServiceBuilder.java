package client;

import client.exception.ConnectionInitializationException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;

public class ServiceBuilder {


    public static OsiamService buildService(URI endpoint, String clientId, URI redirectURL, String clientSecret) {
        final WebResource userWebResource;
        try {
            userWebResource = createWebResource(new URI(endpoint.toString() + "/User/"));
        } catch (URISyntaxException e) {
            throw new ConnectionInitializationException("Unable to setup connection", e);
        }
        return new OsiamService(userWebResource);
    }

    private static WebResource createWebResource(URI path) {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource resource = client.resource(path);
        resource.accept(MediaType.APPLICATION_JSON);
        return resource;
    }
}
