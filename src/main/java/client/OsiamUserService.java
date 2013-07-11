package client;

import com.sun.jersey.api.client.WebResource;
import org.osiam.resources.scim.User;

import java.util.UUID;

public class OsiamUserService {

    private WebResource userWebResource;

    public OsiamUserService(WebResource userWebResource) {
        this.userWebResource = userWebResource;
    }

    public User getUserByUUID(UUID id, String accessToken) {
        return userWebResource.path(id.toString()).
                header("Authorization", "Bearer " + accessToken).get(User.class);
    }
}