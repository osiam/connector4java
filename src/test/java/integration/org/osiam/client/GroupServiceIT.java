package integration.org.osiam.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.osiam.client.OsiamGroupService;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.AuthService;
import org.osiam.client.oauth.GrantType;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.MultiValuedAttribute;

public class GroupServiceIT {

    private static final String INVALID_UUID = "ffffffff-934a-4358-86bc-fffffffffff";
    private static final String VALID_GROUP_UUID = "cef7552e-00a9-4cec-a086-d171374ffbef";
    private static final String VALID_USER_UUID = "cef9452e-00a9-4cec-a086-d171374ffbef"; 

    private AccessToken accessToken;
    private UUID uuidStandardGroup;
    private String endpointAddress = "http://localhost:8080/osiam-server";

    private String clientId = "example-client";
    private String clientSecret = "secret";
    private AuthService authService;
    private OsiamGroupService service;
    
    @Before
    public void setUp() throws URISyntaxException {

        AuthService.Builder authBuilder = new AuthService.Builder(endpointAddress).
                withClientId(clientId).
                withClientSecret(clientSecret).
                withGrantType(GrantType.PASSWORD).
                withUsername("marissa").
                withPassword("koala");
        authService = authBuilder.build();
        service = new OsiamGroupService.Builder(endpointAddress).build();
    }
    
    @Test
    public void get_a_valid_group() throws Exception {
        given_a_test_group_UUID();
        given_a_valid_access_token();
        Group group = service.getGroupByUUID(uuidStandardGroup, accessToken);
        assertEquals(VALID_GROUP_UUID, group.getId());
    }
    
    @Test
    public void ensure_all_values_are_deserialized_correctly() throws Exception {
        given_a_test_group_UUID();
        given_a_valid_access_token();
        Group actualGroup = service.getGroupByUUID(uuidStandardGroup, accessToken);

        assertEquals("Group", actualGroup.getMeta().getResourceType());
        Date created = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("01.08.2011 18:29:49");
        assertEquals(created, actualGroup.getMeta().getCreated());
        assertEquals(created, actualGroup.getMeta().getLastModified());
        assertEquals(VALID_GROUP_UUID, actualGroup.getId());
        assertEquals("testGroup04", actualGroup.getDisplayName());
        Set<MultiValuedAttribute> users = actualGroup.getMembers();
        int count = 0;
        for (MultiValuedAttribute multiValuedAttribute : users) {
			Object value = multiValuedAttribute.getValue();
			assertTrue(value.getClass().equals(String.class));
			String userId = (String)multiValuedAttribute.getValue();
			assertEquals(VALID_USER_UUID, userId);
			count++;
		}
        assertEquals(1, count);
    }
    
    @Test(expected = NoResultException.class)
    public void get_an_invalid_group_raises_exception() throws Exception {
        given_a_valid_access_token();
        service.getGroupByUUID(UUID.fromString("b01e0710-e9b9-4181-995f-4f1f59dc2999"), accessToken);
    }
    
    @Test(expected = UnauthorizedException.class)
    public void provide_an_invalid_access_token_raises_exception() throws Exception {
        given_a_test_group_UUID();
        given_an_invalid_access_token();

        service.getGroupByUUID(uuidStandardGroup, accessToken);
        fail();
    }
    
    private void given_an_invalid_access_token() throws Exception {
        accessToken = new AccessToken();
        Field tokenField = accessToken.getClass().getDeclaredField("token");
        tokenField.setAccessible(true);
        tokenField.set(accessToken, INVALID_UUID);
        tokenField.setAccessible(false);
    }
    
    private void given_a_test_group_UUID() {
        uuidStandardGroup = UUID.fromString(VALID_GROUP_UUID);
    }
    
    private void given_a_valid_access_token() throws Exception {
        if (accessToken == null) {
            accessToken = authService.retrieveAccessToken();
        }
    }
}
