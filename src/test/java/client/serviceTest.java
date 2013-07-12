package client;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osiam.resources.scim.User;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class serviceTest {

    private String accessToken = "1bdc909c-4457-4071-aa8c-42719e9392dd";
    private String validUUID = "d134cd95-be1a-4861-ae6b-097ca1dda648";
    private String endpointAddress = "http://localhost:8080/osiam-server";
    private URI serviceEndpoint;
    private String redirectAddress = "http://localhost:500/oauth2";
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

    @Test @Ignore
    public void getValidUser() {
        User user = service.getUserByUUID(UUID.fromString(validUUID), accessToken);

        assertEquals(validUUID, user.getId());
        assertEquals("florian", user.getExternalId());
    }
}
