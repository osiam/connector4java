package org.osiam.client;
/*
 * for licensing see in the license.txt
 */
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import javax.ws.rs.core.MediaType;

import org.osiam.client.exception.ConnectionInitializationException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * The ServiceBuilder is responsible for providing the respective OSIAM-service that allow access to the
 * respective entities stored in the OSIAM installation.
 */
public class ServiceBuilder {

	/**
	 * 
	 * @param endpoint the address of the OSIAM installation for example http://localhost:8080/osiam-server
	 * @param clientId the in OSIAM registered client_id
	 * @param redirectURL the redirect uri after the login from a user. This url has to be registered in OSIAM to avoid pishing
	 * @param clientSecret the in OSIAM registered client secret to avoid pishing
	 * @return a OsiamUserService which will be connected to the given OSIAM and provides all needed User methods
	 */
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
