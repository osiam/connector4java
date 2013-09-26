package org.osiam.client;
/*
 * for licensing see the file license.txt.
 */

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;
import org.apache.http.entity.ContentType;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryResult;
import org.osiam.client.query.metamodel.User_;
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

public class OsiamUserServiceTest {

    private static final String URL_BASE = "/osiam-server//Users";
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9090); // No-args constructor defaults to port 8080

    private final static String COUNTRY = "Germany";
    private final static String USER_ID = "94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4";
    private final static String INVALID_USER_ID_STRING = "55bbe688-4b1e-4e4e-80e7-e5ba5c4d";
    private final static String endpoint = "http://localhost:9090/osiam-server/";
    private final static String SIMPLE_QUERY_STRING = "filter=displayName+eq+BarbaraJ.";
    
    private String searchedID;
    private AccessToken accessToken;
    private AccessTokenMockProvider tokenProvider;

    private User singleUserResult;
    private Query query;
    private QueryResult<User> queryResult;
    private List<User> allUsers;

    OsiamUserService service;

    @Before
    public void setUp() throws Exception {
        service = new OsiamUserService.Builder(endpoint).build();
        tokenProvider = new AccessTokenMockProvider("/__files/valid_accesstoken.json");

        givenAnUserID();
        givenAnAccessToken();
    }

    @Test
    public void service_returns_correct_uri() throws Exception {
        assertEquals(new URI(endpoint + "/Users"), service.getUri());
    }

    @Test
    public void existing_user_is_returned() throws Exception {
        givenIDcanBeFound();
        whenSingleIDisLookedUp();
        thenReturnedUserHasID(searchedID);
        thenMetaDataWasDeserializedCorrectly();
        thenAddressIsDeserializedCorrectly();
        thenPhoneNumbersAreDeserializedCorrectly();
        thenBasicValuesAreDeserializedCorrectly();
    }

    @Test
    public void user_has_valid_values() throws Exception {
        givenIDcanBeFound();
        whenSingleIDisLookedUp();
        thenReturnedUserMatchesExpectations();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void id_is_null_by_getting_single_user_raises_exception() throws Exception {
        givenIDisEmpty();
        searchedID = null;
        whenSingleIDisLookedUp();
        fail("Exception expected");
    }

    @Test(expected = IllegalArgumentException.class)
    public void accessToken_is_null_by_getting_single_user_raises_exception() throws Exception {
        givenIDisEmpty();
        accessToken = null;
        whenSingleIDisLookedUp();
        fail("Exception expected");
    }

    @Test(expected = IllegalArgumentException.class)
    public void accessToken_is_null_by_getting_all_group_raises_exception() throws Exception {
        givenIDisEmpty();
        accessToken = null;
        whenAllUsersAreLookedUp();
        fail("Exception expected");
    }

    @Test(expected = IllegalArgumentException.class)
    public void accessToken_is_null_by_searching_for_group_by_string_raises_exception() throws Exception {
        givenIDisEmpty();
        accessToken = null;
        whenSearchIsUsedByString("meta.version=3");
        fail("Exception expected");
    }

    @Test(expected = NoResultException.class)
    public void user_does_not_exist() throws IOException {
        givenIDcanNotBeFound();
        whenSingleIDisLookedUp();
        fail("Exception expected");
    }

    @Test(expected = UnauthorizedException.class)
    public void expired_access_token() throws Exception {
        givenExpiredAccessTokenIsUsedForLookup();
        whenSingleIDisLookedUp();
        fail("Exception expected");
    }

    @Test(expected = UnauthorizedException.class)
    public void invalid_access_token() throws Exception {
        givenInvalidAccessTokenIsUsedForLookup();
        whenSingleIDisLookedUp();
        fail("Exception expected");
    }

    @Test(expected = NoResultException.class)
    public void invalid_ID_search() throws IOException {
        givenIDisInvalid();
        whenSingleIDisLookedUp();
        fail("Exception expected");
    }

    @Test(expected = NoResultException.class)
    public void invalid_ID_is_star() throws IOException {
        givenIDisSpecial("*");
        whenSingleIDisLookedUp();
        fail("Exception expected");
    }

    @Test(expected = NoResultException.class)
    public void invalid_ID_is_dot() throws IOException {
        givenIDisSpecial(".");
        whenSingleIDisLookedUp();
        fail("Exception expected");
    }

    @Test
    public void all_users_are_looked_up() {
        givenAllUsersAreLookedUpSuccessfully();
        whenAllUsersAreLookedUp();
        thenNumberOfAllUsersIs(1);
    }

    @Test
    public void search_for_single_user_is_successful() {
        givenASingleUserCanBeSearchedByQuery();
        whenSearchIsUsedByString(SIMPLE_QUERY_STRING);
        thenQueryWasValid();
        thenNumberOfReturnedUsersIs(1);
        thenReturnedListOfSearchedUsersIsAsExpected();
    }

    @Test
    public void query_string_is_split_correctly() throws UnsupportedEncodingException {
        givenAQueryContainingDifficultCharacters();
        givenAUserCanBeSearchedByQuery();
        whenSearchIsUsedByQuery();
        thenQueryStringIsSplitCorrectly();

    }

    @Test
    public void sort_order_is_split_correctly() throws UnsupportedEncodingException{
        givenAQueryContainingDifficultCharactersAndSortBy();
        givenAUserCanBeSearchedByQuery();
        whenSearchIsUsedByQuery();
        thenSortedQueryStringIsSplitCorrectly();
    }

    @Test (expected = IllegalArgumentException.class)
    public void create_null_user_raises_exception(){
        User newUser = null;
        service.createUser(newUser, accessToken);
        Assert.fail("Exception excpected");
    }

    @Test (expected = IllegalArgumentException.class)
    public void create_user_with_null_accestoken_raises_exception(){
        User newUser = new User.Builder("cuwna").build();
        service.createUser(newUser, null);
        Assert.fail("Exception excpected");
    }

    @Test (expected = IllegalArgumentException.class)
    public void delete_null_user_raises_exception(){
        String userID = null;
        service.deleteUser(userID, accessToken);
        Assert.fail("Exception excpected");
    }

    @Test (expected = IllegalArgumentException.class)
    public void delete_user_with_null_accestoken_raises_exception(){
        String id = "HelloWorld";
        service.deleteUser(id, null);
        Assert.fail("Exception excpected");
    }

    private void givenAnAccessToken() throws IOException {
        this.accessToken = tokenProvider.valid_access_token();
    }

    private void givenAnUserID() {
        this.searchedID = USER_ID;
    }

    private void givenAQueryContainingDifficultCharactersAndSortBy() throws UnsupportedEncodingException {
        Query.Filter filter = new Query.Filter(User.class, User_.Name.formatted.contains("Schulz & Schulz Industries"));
        query = new Query.Builder(User.class).setFilter(filter).setSortBy(User_.userName).build();
    }

    private void givenAQueryContainingDifficultCharacters() throws UnsupportedEncodingException {
        Query.Filter filter = new Query.Filter(User.class, User_.Name.formatted.contains("Schulz & Schulz Industries"));
        query = new Query.Builder(User.class).setFilter(filter).build();
    }

    private void givenAUserCanBeSearchedByQuery() {
        stubFor(get(urlMatching(URL_BASE + "\\?filter=.+"))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("query_user_by_name.json")));
    }

    private void givenExpiredAccessTokenIsUsedForLookup() {
        stubFor(givenIDisLookedUp(USER_ID, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_UNAUTHORIZED)));
    }

    private void givenInvalidAccessTokenIsUsedForLookup() {
        stubFor(givenIDisLookedUp(USER_ID, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_UNAUTHORIZED)));
    }

    private void givenIDcanNotBeFound() {
        stubFor(givenIDisLookedUp(USER_ID, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_NOT_FOUND)));
    }

    private void givenIDcanBeFound() {
        stubFor(givenIDisLookedUp(USER_ID, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("user_" + USER_ID + ".json")));
    }

    private void givenIDisEmpty() {
        stubFor(givenIDisLookedUp("", accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("query_all_users.json")));
    }

    private void givenIDisInvalid() {
        stubFor(givenIDisLookedUp(INVALID_USER_ID_STRING, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_NOT_FOUND)));
    }

    private void givenIDisSpecial(String wildcard) {
        stubFor(givenIDisLookedUp(wildcard, accessToken)
                .willReturn(aResponse()
                        .withStatus(SC_CONFLICT)));
    }

    private MappingBuilder givenIDisLookedUp(String id, AccessToken accessToken) {
        return get(urlEqualTo(URL_BASE + "/" + id))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()));
    }

    private void givenAllUsersAreLookedUpSuccessfully() {
        stubFor(get(urlEqualTo(URL_BASE + "?count=" + Integer.MAX_VALUE))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withHeader("Authorization", equalTo("Bearer " + accessToken.getToken()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("query_all_users.json")));
    }

    private void givenASingleUserCanBeSearchedByQuery() {
        stubFor(get(urlEqualTo(URL_BASE + "?filter=displayName+eq+BarbaraJ."))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .withBodyFile("query_user_by_name.json")));
    }

    private void whenSingleIDisLookedUp() {
        singleUserResult = service.getUser(searchedID, accessToken);
    }

    private void whenAllUsersAreLookedUp() {
        allUsers = service.getAllUsers(accessToken);
    }

    private void whenSearchIsUsedByQuery() {
        queryResult = service.searchUsers(query, accessToken);
    }

    private void whenSearchIsUsedByString(String queryString) {
        queryResult = service.searchUsers(queryString, accessToken);
    }

    private void thenQueryStringIsSplitCorrectly() {
        verify(getRequestedFor(urlEqualTo(URL_BASE + "?filter=name.formatted+co+%22Schulz+%26+Schulz+Industries%22"))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType())));
    }

    private void thenSortedQueryStringIsSplitCorrectly() {
        verify(getRequestedFor(urlEqualTo(URL_BASE + "?filter=name.formatted+co+%22Schulz+%26+Schulz+Industries%22&sortBy=userName"))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType())));
    }

    private void thenReturnedUserHasID(String id) {
        assertEquals(id, singleUserResult.getId());
    }

    private void thenQueryWasValid() {
        verify(getRequestedFor(urlEqualTo(URL_BASE + "?filter=displayName+eq+BarbaraJ."))
                .withHeader("Content-Type", equalTo(ContentType.APPLICATION_JSON.getMimeType())));
    }


    private void thenReturnedListOfSearchedUsersIsAsExpected() {
        assertEquals(1, queryResult.getTotalResults());
        assertEquals("BarbaraJ.", queryResult.getResources().iterator().next().getDisplayName());
    }

    private void thenNumberOfReturnedUsersIs(int numberOfUsers) {
        assertEquals(numberOfUsers, queryResult.getTotalResults());
        assertEquals(numberOfUsers, queryResult.getResources().size());
    }
    
    private void thenNumberOfAllUsersIs(int numberOfUsers) {
        assertEquals(numberOfUsers, allUsers.size());
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
