package org.osiam.client;
/*
 * for licensing see the file license.txt.
 */

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import javax.ws.rs.core.MediaType;
import java.net.URI;

/**
 * The WebResourceProducer is responsible for providing {@link WebResource}s that are readily configured
 * for the use in the client library. It should not be necessary to use it directly.
 */
class WebResourceProducer {

    private WebResourceProducer(){    }

    static WebResource createWebResource(URI path) {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(config);
        WebResource resource = client.resource(path);
        resource.accept(MediaType.APPLICATION_JSON);
        return resource;
    }
}
