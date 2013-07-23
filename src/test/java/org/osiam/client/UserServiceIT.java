package org.osiam.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.model.AccessToken;
import org.osiam.resources.scim.User;

public class UserServiceIT {

    private AccessToken accessToken;
    private UUID uuidStandardUser = null;
    private String endpointAddress = "http://localhost:8080/osiam-server";
    private URI serviceEndpoint;
    private String redirectAddress = "http://localhost:5000/oauth2";
    private URI redirectURL;
    private String clientId = "example-client";
    private String clientSecret = "secret";
    private OsiamUserService service;

    private void giveAccessToken() throws IOException{
    	if(accessToken == null){
    		accessToken = new AuthService().retrieveAccessToken(endpointAddress + "/oauth/token", clientId, clientSecret, "marissa", "koala");
    	}
    }

    private void giveTestUserUUID(){
    	uuidStandardUser = UUID.fromString("94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4");
    }

    @Before
    public void setUp() throws URISyntaxException {
        serviceEndpoint = new URI(endpointAddress);
        redirectURL = new URI(redirectAddress);

        service = ServiceBuilder.buildUserService(serviceEndpoint, clientId, redirectURL, clientSecret);
    }

    @Test
    public void getValidUser() throws IOException {
    	giveTestUserUUID();
    	giveAccessToken();
        User user = service.getUserByUUID(uuidStandardUser, accessToken);
        assertEquals(uuidStandardUser.toString(), user.getId());
    }

    @Test
    public void ensureValuesStandardUser() throws ParseException, IOException {
    	giveTestUserUUID();
    	giveAccessToken();
        User actualUser = service.getUserByUUID(uuidStandardUser, accessToken);

        assertEquals("User", actualUser.getMeta().getResourceType());
        Date created = new SimpleDateFormat( "dd.MM.yyyy HH:mm:ss" ).parse("01.08.2011 18:29:49");
        assertEquals(created, actualUser.getMeta().getCreated());
        assertEquals(created, actualUser.getMeta().getLastModified());
        assertEquals(uuidStandardUser.toString(), actualUser.getId());
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
        assertEquals(1, actualUser.getPhotos().size());
        assertEquals("photo", actualUser.getPhotos().get(0).getType());
        assertEquals("https://photos.example.com/profilephoto/72930000000Ccne.jpg"
        		, actualUser.getPhotos().get(0).getValue().toString());
        assertEquals(1, actualUser.getIms().size());
        assertEquals("someaimhandle", actualUser.getIms().get(0).getValue().toString());
    }

    @Test(expected = NoResultException.class)
    public void getInvalidUser() throws IOException {
    	giveAccessToken();
    	User user = service.getUserByUUID(UUID.fromString("b01e0710-e9b9-4181-995f-4f1f59dc2999"), accessToken);
    }

    @Test(expected = UnauthorizedException.class)
    public void provideWrongAccessToken() {
    	giveTestUserUUID();
    	AccessToken wrongAccessToken = new AccessToken();
    	wrongAccessToken.setAccessToken("wrong Token");
    	service.getUserByUUID(uuidStandardUser, wrongAccessToken);
    	fail("A Exception should be thrown");
    }

}
