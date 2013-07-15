package client;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osiam.resources.scim.User;

import com.sun.jersey.api.client.UniformInterfaceException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UserServiceIT {

    private String accessToken = "2b534de7-d248-4f86-a508-e4decd0a3795";
    private String validUserUUID = "4a51b8fa-e4c5-4164-8864-b52a1bb5ca17";
    private String endpointAddress = "http://localhost:8080/osiam-server";
    private URI serviceEndpoint;
    private String redirectAddress = "http://localhost:5000/oauth2";
    private URI redirectURL;
    private String clientId = "example-client";
    private String clientSecret = "secret";
    private OsiamUserService service;

    @Before
    public void setUp() throws URISyntaxException {

        serviceEndpoint = new URI(endpointAddress);
        redirectURL = new URI(redirectAddress);

        service = ServiceBuilder.buildUserService(serviceEndpoint, clientId, redirectURL, clientSecret);
    }

    /* Attention this test does not work automated for now! Please provide a valid access token before using it */

    @Test
    public void getValidUser() {
        User user = service.getUserByUUID(UUID.fromString(validUserUUID), accessToken);

        assertEquals(validUserUUID, user.getId());
        assertEquals("tobias", user.getExternalId());
        assertEquals(null, user.getNickName());
    }
    
    @Test(expected = UniformInterfaceException.class)
    public void getInvalidUser() {
    	service.getUserByUUID(UUID.fromString("b01e0710-e9b9-4181-995f-4f1f59dc2999"), accessToken);       
    }
}
