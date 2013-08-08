package org.osiam.client.query;

import org.junit.Before;
import org.junit.Test;
import org.osiam.client.exception.QueryBuilderInitializationException;
import org.osiam.resources.scim.User;

import static org.junit.Assert.assertEquals;

public class QueryBuilderTest {

    private static final String DEFAULT_ATTR = "name";
    private static final String VALID_META_ATTR = "Meta.created";
    private static final String VALID_NAME_ATTR = "Name.givenName";
    private static final String VALID_EMAIL_ATTR = "Emails.value";
    private static final String INVALID_EMAIL_ATTR = "Emails.false";
    private static final String IRRELEVANT = "irrelevant";
    private QueryBuilder queryBuilder;

    @Before
    public void setUp() {
        queryBuilder = new QueryBuilder();
    }

    @Test
    public void nested_email_attribute_is_added_to_query() {
        queryBuilder.forClass(User.class).query(VALID_EMAIL_ATTR);
        buildStringMeetsExpectation(VALID_EMAIL_ATTR.toLowerCase());
    }

    @Test
    public void nested_name_attribute_is_added_to_query() {
        queryBuilder.forClass(User.class).query(VALID_NAME_ATTR);
        buildStringMeetsExpectation(VALID_NAME_ATTR.toLowerCase());
    }

    @Test
    public void nested_meta_attribute_is_added_to_query() {
        queryBuilder.forClass(User.class).query(VALID_META_ATTR);
        buildStringMeetsExpectation(VALID_META_ATTR.toLowerCase());
    }

    @Test
    public void flat_attribute_is_added_to_query() {
        queryBuilder.forClass(User.class).query(DEFAULT_ATTR);
        buildStringMeetsExpectation(DEFAULT_ATTR);
    }

    @Test
    public void and_attribute_is_added_correctly() {
        queryBuilder.forClass(User.class).query(DEFAULT_ATTR)
                .contains(IRRELEVANT)
                .and(DEFAULT_ATTR).contains(IRRELEVANT);
        buildStringMeetsExpectation(DEFAULT_ATTR + " co " + IRRELEVANT + " and " + DEFAULT_ATTR + " co " + IRRELEVANT);
    }

    @Test
    public void or_attribute_is_added_correctly() {
        queryBuilder.forClass(User.class).query(DEFAULT_ATTR)
                .contains(IRRELEVANT)
                .or(DEFAULT_ATTR).contains(IRRELEVANT);
        buildStringMeetsExpectation(DEFAULT_ATTR + " co " + IRRELEVANT + " or " + DEFAULT_ATTR + " co " + IRRELEVANT);
    }

    @Test
    public void filter_contains_is_added_to_query() {
        queryBuilder.forClass(User.class).query(DEFAULT_ATTR).contains(IRRELEVANT);
        buildStringMeetsExpectation(DEFAULT_ATTR + " co " + IRRELEVANT);
    }

    @Test
    public void filter_equals_is_added_to_query() {
        queryBuilder.forClass(User.class).query(DEFAULT_ATTR).equals(IRRELEVANT);
        buildStringMeetsExpectation(DEFAULT_ATTR + " eq " + IRRELEVANT);
    }

    @Test
    public void filter_startsWith_is_added_to_query() {
        queryBuilder.forClass(User.class).query(DEFAULT_ATTR).startsWith(IRRELEVANT);
        buildStringMeetsExpectation(DEFAULT_ATTR + " sw " + IRRELEVANT);
    }

    @Test
    public void filter_present_is_added_to_query() {
        queryBuilder.forClass(User.class).query(DEFAULT_ATTR).present(IRRELEVANT);
        buildStringMeetsExpectation(DEFAULT_ATTR + " pr " + IRRELEVANT);
    }

    @Test
    public void filter_greater_than_is_added_to_query() {
        queryBuilder.forClass(User.class).query(DEFAULT_ATTR).greaterThan(IRRELEVANT);
        buildStringMeetsExpectation(DEFAULT_ATTR + " gt " + IRRELEVANT);
    }

    @Test
    public void filter_greater_equals_is_added_to_query() {
        queryBuilder.forClass(User.class).query(DEFAULT_ATTR).greaterEquals(IRRELEVANT);
        buildStringMeetsExpectation(DEFAULT_ATTR + " ge " + IRRELEVANT);
    }

    @Test
    public void filter_less_than_is_added_to_query() {
        queryBuilder.forClass(User.class).query(DEFAULT_ATTR).lessThan(IRRELEVANT);
        buildStringMeetsExpectation(DEFAULT_ATTR + " lt " + IRRELEVANT);
    }

    @Test
    public void filter_less_equals_is_added_to_query() {
        queryBuilder.forClass(User.class).query(DEFAULT_ATTR).lessEquals(IRRELEVANT);
        buildStringMeetsExpectation(DEFAULT_ATTR + " le " + IRRELEVANT);
    }

    @Test(expected = QueryBuilderInitializationException.class)
    public void exception_raised_when_attr_is_not_valid() {
        queryBuilder.forClass(User.class).query(IRRELEVANT);
    }

    @Test(expected = QueryBuilderInitializationException.class)
    public void exception_raised_when_nested_attr_is_not_valid() {
        queryBuilder.forClass(User.class).query(INVALID_EMAIL_ATTR);
    }

    @Test(expected = QueryBuilderInitializationException.class)
    public void exception_raised_when_type_not_set() {
        queryBuilder.query(IRRELEVANT);
    }

    private void buildStringMeetsExpectation(String buildString) {
        assertEquals(buildString, queryBuilder.build());
    }
}
