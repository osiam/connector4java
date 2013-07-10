package client;

import com.sun.jersey.api.client.WebResource;
import org.osiam.resources.scim.User;

import java.util.UUID;

public class OsiamService {

    private WebResource userWebResource;

    public OsiamService(WebResource userWebResource) {
        this.userWebResource = userWebResource;
    }

    public User getUserByUUID(UUID id, String accessToken) {
        return userWebResource.path(id.toString()).get(User.class);
    }
}