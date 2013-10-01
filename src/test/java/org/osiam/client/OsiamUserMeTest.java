package org.osiam.client;
/*
 * for licensing see the file license.txt.
 */


import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.entity.ContentType;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.resources.scim.Address;
import org.osiam.resources.scim.Email;
import org.osiam.resources.scim.Meta;
import org.osiam.resources.scim.BasicMultiValuedAttribute;
import org.osiam.resources.scim.Name;
import org.osiam.resources.scim.PhoneNumber;
import org.osiam.resources.scim.User;
import org.osiam.resources.type.PhoneNumberType;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class OsiamUserMeTest {


    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9090); // No-args constructor defaults to port 8080


    final static private String COUNTRY = "Germany";
    final static private String ENDPOINT = "http://localhost:9090/osiam-server/";
    final static private String USER_ID = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";  
    private static final String URL_BASE = "/osiam-server//Users";

    private AccessToken accessToken;
    private AccessTokenMockProvider tokenProvider;
    private User singleUserResult;
    
    OsiamUserService service;

    @Before
    public void setUp() throws Exception {
        service = new OsiamUserService.Builder(ENDPOINT).build();
        tokenProvider = new AccessTokenMockProvider("/__files/valid_accesstoken.json");

        givenAnAccessToken();
    }

    @Test
    public void me_user_is_returned() throws Exception {
        givenAccessTokenForMeIsValid();
        whenMeIsLookedUp();
        thenReturnedUserHasID(USER_ID);
        thenMetaDataWasDeserializedCorrectly();
        thenAddressIsDeserializedCorrectly();
        thenPhoneNumbersAreDeserializedCorrectly();
        thenBasicValuesAreDeserializedCorrectly();
    }
    
    @Test
    public void me_user_has_valid_values() throws Exception {
        givenAccessTokenForMeIsValid();
        whenMeIsLookedUp();
        thenReturnedUserMatchesExpectations();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void accessToken_is_null_by_getting_me_user_raises_exception() throws Exception {
        givenIDisEmpty();
        accessToken = null;
        whenMeIsLookedUp();
        fail("Exception expected");
    }
    
    @Test(expected = UnauthorizedException.class)
    public void invalid_access_token_by_getting_me_user_raises_exception() throws Exception {
        givenAccessTokenForMeIsInvalid();
        whenMeIsLookedUp();
        fail("Exception expected");
    }
    
    @Test(expected = UnauthorizedException.class)
    public void expired_access_token_by_getting_me_user_raises_exception() throws Exception {
        givenAccessTokenForMeHasExpired();
        whenMeIsLookedUp();
        fail("Exception expected");
    }
    
    private void givenAnAccessToken() throws IOException {
        this.accessToken = tokenProvider.valid_access_token();
    }

    private void thenReturnedUserHasID(String id) {
        assertEquals(id, singleUserResult.getId());
    }
    private void givenAccessTokenForMeIsValid() {
        stubFor(givenMeIsLookedUp(accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("user_" + USER_ID + ".json")));
    }
    
    private void whenMeIsLookedUp() {
        singleUserResult = service.getMeBasic(accessToken);
    }
    
    private void givenIDisEmpty() {
        stubFor(givenIDisLookedUp("", accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("query_all_users.json")));
    }
    
    private void givenAccessTokenForMeIsInvalid() {
        stubFor(givenMeIsLookedUp(accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_UNAUTHORIZED)));
    }
    
    private void givenAccessTokenForMeHasExpired() {
        stubFor(givenMeIsLookedUp(accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_UNAUTHORIZED)));
    }
    
    private MappingBuilder givenMeIsLookedUp(AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE + "/me"))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }
    
    private MappingBuilder givenIDisLookedUp(String id, AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE + "/" + id))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }
    
    private void thenMetaDataWasDeserializedCorrectly() throws ParseException {
        Meta deserializedMeta = singleUserResult.getMeta();
        Date expectedCreated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2011-08-01 20:29:49");

        assertEquals(expectedCreated, deserializedMeta.getCreated());
        assertEquals(expectedCreated, deserializedMeta.getLastModified());
        assertEquals("https://example.com/v1/Users/2819c223...", deserializedMeta.getLocation());
        assertEquals(null, deserializedMeta.getVersion());
        assertEquals("User", deserializedMeta.getResourceType());
    }
    
    public void thenAddressIsDeserializedCorrectly() throws Exception {
        List<Address> addresses = singleUserResult.getAddresses();
        assertEquals(1, addresses.size());
        Address address = addresses.get(0);

        assertEquals("example street 42", address.getStreetAddress());
        assertEquals("11111", address.getPostalCode());
        assertEquals(COUNTRY, address.getCountry());
        assertEquals(COUNTRY, address.getRegion());
        assertEquals(COUNTRY, address.getLocality());
    }
    
    public void thenPhoneNumbersAreDeserializedCorrectly() {

        List<PhoneNumber> phonenumbers = singleUserResult.getPhoneNumbers();
        assertEquals(1, phonenumbers.size());
        PhoneNumber phonenumber = phonenumbers.get(0);

        assertEquals("555-555-8377", phonenumber.getValue().toString());
        assertEquals(PhoneNumberType.WORK, phonenumber.getType());

    }

    public void thenBasicValuesAreDeserializedCorrectly() throws Exception {
        assertEquals("bjensen", singleUserResult.getExternalId());
        assertEquals(null, singleUserResult.isActive());
        assertEquals("BarbaraJ.", singleUserResult.getDisplayName());
        assertEquals("de", singleUserResult.getLocale());
        assertEquals("Barbara", singleUserResult.getNickName());
        assertEquals("Dr.", singleUserResult.getTitle());
        assertEquals("bjensen", singleUserResult.getUserName());
    }
    
    private void thenReturnedUserMatchesExpectations() throws Exception {

        User expectedUser = get_expected_user();

        assertEquals(expectedUser.getDisplayName(), singleUserResult.getDisplayName());
        assertEqualsEmailList(expectedUser.getEmails(), singleUserResult.getEmails());
        assertEquals(expectedUser.getExternalId(), singleUserResult.getExternalId());
        assertEquals(expectedUser.getLocale(), singleUserResult.getLocale());
        assertEqualsName(expectedUser.getName(), singleUserResult.getName());
        assertEquals(expectedUser.getNickName(), singleUserResult.getNickName());
        assertEquals(expectedUser.getPassword(), singleUserResult.getPassword());
        assertEquals(expectedUser.getPhotos(), singleUserResult.getPhotos());
        assertEquals(expectedUser.getPreferredLanguage(), singleUserResult.getPreferredLanguage());
        assertEquals(expectedUser.getProfileUrl(), singleUserResult.getProfileUrl());
        assertEquals(expectedUser.getTimezone(), singleUserResult.getTimezone());
        assertEquals(expectedUser.getTitle(), singleUserResult.getTitle());
        assertEquals(expectedUser.getUserName(), singleUserResult.getUserName());
        assertEquals(expectedUser.getUserType(), singleUserResult.getUserType());
        assertEquals(expectedUser.isActive(), singleUserResult.isActive());
    }
    
    private void assertEqualsEmailList(List<Email> expected, List<Email> actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected.size() != actual.size()) {
            fail("The expected List has not the same number of values like the actual list");
        }
        for (int count = 0; count < expected.size(); count++) {
            BasicMultiValuedAttribute expectedAttribute = expected.get(count);
            BasicMultiValuedAttribute actualAttribute = actual.get(count);
            assertEquals(expectedAttribute.getValue().toString(), actualAttribute.getValue().toString());
        }
    }

    private void assertEqualsName(Name expected, Name actual) {
        if (expected == null && actual == null) {
            return;
        }
        assertEquals(expected.getFamilyName(), actual.getFamilyName());
        assertEquals(expected.getGivenName(), actual.getGivenName());
        assertEquals(expected.getMiddleName(), actual.getMiddleName());
        assertEquals(expected.getHonorificPrefix(), actual.getHonorificPrefix());
        assertEquals(expected.getHonorificSuffix(), actual.getHonorificSuffix());
    }
    
    private User get_expected_user() throws Exception {
        Reader reader = null;
        StringBuilder jsonUser = null;
        User expectedUser;
        try {
            reader = new FileReader("src/test/resources/__files/user_" + USER_ID + ".json");
            jsonUser = new StringBuilder();
            for (int c; (c = reader.read()) != -1; )
                jsonUser.append((char) c);
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
            }
        }

        expectedUser = new ObjectMapper().readValue(jsonUser.toString(), User.class);
        return expectedUser;
    }

}