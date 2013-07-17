package org.osiam.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.resources.scim.User;

public class UserServiceIT {

    private String accessToken = "c923fe21-cd4b-49b4-86bb-197ab28906b8";
    private String uuidStandardUser = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
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
        User user = service.getUserByUUID(UUID.fromString(uuidStandardUser), accessToken);
        assertEquals(uuidStandardUser, user.getId());
    }
    
    @Test
    public void ensureValuesStandardUser() throws ParseException {
        User actualUser = service.getUserByUUID(UUID.fromString(uuidStandardUser), accessToken);

        assertEquals("User", actualUser.getMeta().getResourceType());
        Date created = new SimpleDateFormat( "dd.MM.yyyy HH:mm:ss" ).parse("01.08.2011 18:29:49");
        assertEquals(created, actualUser.getMeta().getCreated());
        assertEquals(created, actualUser.getMeta().getLastModified());      
        assertEquals(uuidStandardUser, actualUser.getId());
        assertEquals(1, actualUser.getAddresses().size());
        assertEquals("example street 42", actualUser.getAddresses().get(0).getStreetAddress());
        assertEquals("germany", actualUser.getAddresses().get(0).getCountry());
        assertEquals("11111", actualUser.getAddresses().get(0).getPostalCode());
        assertEquals("MaxExample", actualUser.getDisplayName());
        assertEquals(2, actualUser.getEmails().size());
        String email = actualUser.getEmails().get(0).getValue().toString();
        boolean exists = email.equals("MaxExample@work.com") || email.equals("MaxExample@home.de"); 
        assertTrue(exists);
        email = actualUser.getEmails().get(1).getValue().toString();
        exists = email.equals("MaxExample@work.com") || email.equals("MaxExample@home.de");
        assertTrue(exists);
        assertEquals("MExample", actualUser.getExternalId());
        assertEquals("de", actualUser.getLocale());
        assertEquals("Example", actualUser.getName().getFamilyName());
        assertEquals("Max", actualUser.getName().getGivenName());
        assertEquals("Jason", actualUser.getName().getMiddleName());
        assertEquals("Max", actualUser.getNickName());
        assertEquals(null, actualUser.getPassword());
        assertEquals(1, actualUser.getPhoneNumbers().size());
        assertEquals("666-999-6666", actualUser.getPhoneNumbers().get(0).getValue().toString());
        assertEquals("de", actualUser.getPreferredLanguage());
        assertEquals("http://test.de", actualUser.getProfileUrl());
        assertEquals("UTC", actualUser.getTimezone());
        assertEquals("Dr", actualUser.getTitle());
        assertEquals("MaxExample", actualUser.getUserName());
        assertEquals("User", actualUser.getUserType());
        assertEquals(null, actualUser.isActive());
    }
    
    @Test
    public void getInvalidUser() {
    	User user = service.getUserByUUID(UUID.fromString("b01e0710-e9b9-4181-995f-4f1f59dc2999"), accessToken);
    	assertEquals(null, user);
    }
    
    @Test(expected = UnauthorizedException.class)
    public void provideWrongAccessToken() {
    	service.getUserByUUID(UUID.fromString(uuidStandardUser), "wrongToken");   
    	fail("A Exception should be thrown");
    }
}
