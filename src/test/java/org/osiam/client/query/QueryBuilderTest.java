package org.osiam.client.query;

import org.junit.Before;
import org.junit.Test;
import org.osiam.client.exception.InvalidAttributeException;
import org.osiam.resources.scim.User;

import static org.junit.Assert.assertEquals;

public class QueryBuilderTest {

    private static final String DEFAULT_ATTR = "name";
    private static final String VALID_META_ATTR = "meta.created";
    private static final String VALID_NAME_ATTR = "name.givenName";
    private static final String VALID_EMAIL_ATTR = "emails.value";
    private static final String INVALID_EMAIL_ATTR = "emails.false";
    private static final String IRRELEVANT = "irrelevant";
    private QueryBuilder queryBuilder;

    @Before
    public void setUp() {
        queryBuilder = new QueryBuilder(User.class);
    }

    @Test
    public void nested_email_attribute_is_added_to_query() {
        queryBuilder.query(VALID_EMAIL_ATTR);
        builtStringMeetsExpectation(VALID_EMAIL_ATTR);
    }

    @Test
    public void nested_name_attribute_is_added_to_query() {
        queryBuilder.query(VALID_NAME_ATTR);
        builtStringMeetsExpectation(VALID_NAME_ATTR);
    }

    @Test
    public void nested_meta_attribute_is_added_to_query() {
        queryBuilder.query(VALID_META_ATTR);
        builtStringMeetsExpectation(VALID_META_ATTR);
    }

    @Test
    public void flat_attribute_is_added_to_query() {
        queryBuilder.query(DEFAULT_ATTR);
        builtStringMeetsExpectation(DEFAULT_ATTR);
    }

    @Test
    public void and_attribute_is_added_correctly() {
        queryBuilder.query(DEFAULT_ATTR)
                .contains(IRRELEVANT)
                .and(DEFAULT_ATTR).contains(IRRELEVANT);
        builtStringMeetsExpectation(DEFAULT_ATTR + " co " + IRRELEVANT + " and " + DEFAULT_ATTR + " co " + IRRELEVANT);
    }

    @Test
    public void or_attribute_is_added_correctly() {
        queryBuilder.query(DEFAULT_ATTR)
                .contains(IRRELEVANT)
                .or(DEFAULT_ATTR).contains(IRRELEVANT);
        builtStringMeetsExpectation(DEFAULT_ATTR + " co " + IRRELEVANT + " or " + DEFAULT_ATTR + " co " + IRRELEVANT);
    }

    @Test
    public void filter_contains_is_added_to_query() {
        queryBuilder.query(DEFAULT_ATTR).contains(IRRELEVANT);
        builtStringMeetsExpectation(DEFAULT_ATTR + " co " + IRRELEVANT);
    }

    @Test
    public void filter_equals_is_added_to_query() {
        queryBuilder.query(DEFAULT_ATTR).equalTo(IRRELEVANT);
        builtStringMeetsExpectation(DEFAULT_ATTR + " eq " + IRRELEVANT);
    }

    @Test
    public void filter_startsWith_is_added_to_query() {
        queryBuilder.query(DEFAULT_ATTR).startsWith(IRRELEVANT);
        builtStringMeetsExpectation(DEFAULT_ATTR + " sw " + IRRELEVANT);
    }

    @Test
    public void filter_present_is_added_to_query() {
        queryBuilder.query(DEFAULT_ATTR).present();
        builtStringMeetsExpectation(DEFAULT_ATTR + " pr ");
    }

    @Test
    public void filter_greater_than_is_added_to_query() {
        queryBuilder.query(DEFAULT_ATTR).greaterThan(IRRELEVANT);
        builtStringMeetsExpectation(DEFAULT_ATTR + " gt " + IRRELEVANT);
    }

    @Test
    public void filter_greater_equals_is_added_to_query() {
        queryBuilder.query(DEFAULT_ATTR).greaterEquals(IRRELEVANT);
        builtStringMeetsExpectation(DEFAULT_ATTR + " ge " + IRRELEVANT);
    }

    @Test
    public void filter_less_than_is_added_to_query() {
        queryBuilder.query(DEFAULT_ATTR).lessThan(IRRELEVANT);
        builtStringMeetsExpectation(DEFAULT_ATTR + " lt " + IRRELEVANT);
    }

    @Test
    public void filter_less_equals_is_added_to_query() {
        queryBuilder.query(DEFAULT_ATTR).lessEquals(IRRELEVANT);
        builtStringMeetsExpectation(DEFAULT_ATTR + " le " + IRRELEVANT);
    }

    @Test(expected = InvalidAttributeException.class)
    public void exception_raised_when_attr_is_not_valid() {
        queryBuilder.query(IRRELEVANT);
    }

    @Test(expected = InvalidAttributeException.class)
    public void exception_raised_when_nested_attr_is_not_valid() {
        queryBuilder.query(INVALID_EMAIL_ATTR);
    }

    private void builtStringMeetsExpectation(String buildString) {
        assertEquals(buildString, queryBuilder.build());
    }
}
