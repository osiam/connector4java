package org.osiam.client;
/*
 * for licensing see in the license.txt
 */

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.osiam.client.exception.ConnectionInitializationException;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * The ServiceBuilder is responsible for providing the respective OSIAM-service that allow access to the
 * respective entities stored in the OSIAM installation.
 */
public class ServiceBuilder {

    /**
     * Returns a OsiamUserService of the given OSIAM Service to retrieve or to save any OSIAM users
     *
     * @param endpoint     the address of the OSIAM installation for example http://localhost:8080/osiam-server
     * @param clientId     the in OSIAM registered client_id
     * @param redirectURL  the redirect uri after the login from a user. This url has to be registered in OSIAM to avoid pishing
     * @param clientSecret the in OSIAM registered client secret to avoid pishing
     * @return a new OsiamUserService
     * @throws ConnectionInitializationException
     *          will be thrown if no connection to the given endpoint could be initialized
     */
    public static OsiamUserService buildUserService(URI endpoint, String clientId, URI redirectURL, String clientSecret) {
        final WebResource userWebResource;
        try {
            userWebResource = createWebResource(new URI(endpoint.toString() + "/User/"));
            ClientResponse res = userWebResource.get(ClientResponse.class);
            int status = res.getStatus();
            if (status == 404) {
            //    throw new ConnectionInitializationException("Unable to find the given OSIAM service (" + endpoint.toString() + ")");
            }
        } catch (ClientHandlerException | URISyntaxException e) {
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
