package org.osiam.client.query;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osiam.client.exception.InvalidAttributeException;
import org.osiam.resources.scim.User;

import static org.junit.Assert.assertEquals;

public class UserQueryBuilderTest {

    private static final String DEFAULT_ATTR = "name";
    private static final String VALID_META_ATTR = "meta.created";
    private static final String VALID_NAME_ATTR = "name.givenName";
    private static final String VALID_EMAIL_ATTR = "emails.value";
    private static final String INVALID_EMAIL_ATTR = "emails.false";
    private static final String IRRELEVANT = "irrelevant";
    private static final int START_INDEX = 5;
    private static final int COUNT_PER_PAGE = 7;
    private static final String FILTER = "filter=";
    private QueryBuilder queryBuilder;

    @Before
    public void setUp() {
        queryBuilder = new QueryBuilder(User.class);
    }

    @Test
    public void nested_email_attribute_is_added_to_query() {
        queryBuilder.filter(VALID_EMAIL_ATTR);
        buildStringMeetsExpectation(FILTER + VALID_EMAIL_ATTR);
    }

    @Test
    public void nested_name_attribute_is_added_to_query() {
        queryBuilder.filter(VALID_NAME_ATTR);
        buildStringMeetsExpectation(FILTER + VALID_NAME_ATTR);
    }

    @Test
    public void nested_meta_attribute_is_added_to_query() {
        queryBuilder.filter(VALID_META_ATTR);
        buildStringMeetsExpectation(FILTER + VALID_META_ATTR);
    }

    @Test
    public void flat_attribute_is_added_to_query() {
        queryBuilder.filter(DEFAULT_ATTR);
        buildStringMeetsExpectation(FILTER + DEFAULT_ATTR);
    }

    @Test
    public void and_attribute_is_added_correctly() {
        queryBuilder.filter(DEFAULT_ATTR)
                .contains(IRRELEVANT)
                .and(DEFAULT_ATTR).contains(IRRELEVANT);
        buildStringMeetsExpectation(FILTER + DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" and " + DEFAULT_ATTR + " co \"" + IRRELEVANT + "\"");
    }

    @Test
    public void or_attribute_is_added_correctly() {
        queryBuilder.filter(DEFAULT_ATTR)
                .contains(IRRELEVANT)
                .or(DEFAULT_ATTR).contains(IRRELEVANT);
        buildStringMeetsExpectation(FILTER + DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" or " + DEFAULT_ATTR + " co \"" + IRRELEVANT + "\"");
    }

    @Test
    public void filter_contains_is_added_to_query() {
        queryBuilder.filter(DEFAULT_ATTR).contains(IRRELEVANT);
        buildStringMeetsExpectation(FILTER + DEFAULT_ATTR + " co \"" + IRRELEVANT + "\"");
    }

    @Test
    public void filter_equals_is_added_to_query() {
        queryBuilder.filter(DEFAULT_ATTR).equalTo(IRRELEVANT);
        buildStringMeetsExpectation(FILTER + DEFAULT_ATTR + " eq \"" + IRRELEVANT + "\"");
    }

    @Test
    public void filter_startsWith_is_added_to_query() {
        queryBuilder.filter(DEFAULT_ATTR).startsWith(IRRELEVANT);
        buildStringMeetsExpectation(FILTER + DEFAULT_ATTR + " sw \"" + IRRELEVANT + "\"");
    }

    @Test
    public void filter_present_is_added_to_query() {
        queryBuilder.filter(DEFAULT_ATTR).present();
        buildStringMeetsExpectation(FILTER + DEFAULT_ATTR + " pr ");
    }

    @Test
    public void filter_greater_than_is_added_to_query() {
        queryBuilder.filter(DEFAULT_ATTR).greaterThan(IRRELEVANT);
        buildStringMeetsExpectation(FILTER + DEFAULT_ATTR + " gt \"" + IRRELEVANT + "\"");
    }

    @Test
    public void filter_greater_equals_is_added_to_query() {
        queryBuilder.filter(DEFAULT_ATTR).greaterEquals(IRRELEVANT);
        buildStringMeetsExpectation(FILTER + DEFAULT_ATTR + " ge \"" + IRRELEVANT + "\"");
    }

    @Test
    public void filter_less_than_is_added_to_query() {
        queryBuilder.filter(DEFAULT_ATTR).lessThan(IRRELEVANT);
        buildStringMeetsExpectation(FILTER + DEFAULT_ATTR + " lt \"" + IRRELEVANT + "\"");
    }

    @Test
    public void filter_less_equals_is_added_to_query() {
        queryBuilder.filter(DEFAULT_ATTR).lessEquals(IRRELEVANT);
        buildStringMeetsExpectation(FILTER + DEFAULT_ATTR + " le \"" + IRRELEVANT + "\"");
    }

    @Test(expected = InvalidAttributeException.class)
    public void exception_raised_when_attr_is_not_valid() {
        queryBuilder.filter(IRRELEVANT);
    }

    @Test(expected = InvalidAttributeException.class)
    public void exception_raised_when_nested_attr_is_not_valid() {
        queryBuilder.filter(INVALID_EMAIL_ATTR);
    }

    @Test
    public void sort_order_ascending() {
        queryBuilder.withSortOrder(SortOrder.ASCENDING);
        buildStringMeetsExpectation("sortOrder=ascending");
    }

    @Test
    public void sort_order_descending() {
        queryBuilder.withSortOrder(SortOrder.DESCENDING);
        buildStringMeetsExpectation("sortOrder=descending");
    }

    @Test
    public void query_and_sort_order_ascending() {
        queryBuilder.filter(DEFAULT_ATTR)
                .contains(IRRELEVANT)
                .and(DEFAULT_ATTR).contains(IRRELEVANT)
                .withSortOrder(SortOrder.ASCENDING);
        buildStringMeetsExpectation(FILTER + DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" and " + DEFAULT_ATTR + " co \"" + IRRELEVANT + "\"&sortOrder=ascending");
    }

    @Test
    public void two_times_set_sort_order_descending() {
        queryBuilder.withSortOrder(SortOrder.ASCENDING).withSortOrder(SortOrder.DESCENDING);
        buildStringMeetsExpectation("sortOrder=descending");
    }

    @Test
    public void start_index_added() {
        queryBuilder.startIndex(START_INDEX);
        buildStringMeetsExpectation("startIndex=" + START_INDEX);
    }

    @Test
    public void count_per_page_added() {
        queryBuilder.countPerPage(COUNT_PER_PAGE);
        buildStringMeetsExpectation("count=" + COUNT_PER_PAGE);
    }

    @Test
    public void start_index_and_count_added_to_complete_query() {
        queryBuilder.filter(DEFAULT_ATTR)
                .contains(IRRELEVANT)
                .and(DEFAULT_ATTR).contains(IRRELEVANT)
                .startIndex(START_INDEX)
                .countPerPage(COUNT_PER_PAGE)
                .withSortOrder(SortOrder.ASCENDING);
        String exceptedQuery = DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" and " + DEFAULT_ATTR
                + " co \"" + IRRELEVANT + "\"&sortOrder=ascending&count=" + COUNT_PER_PAGE
                + "&startIndex=" + START_INDEX;
        buildStringMeetsExpectation(FILTER + exceptedQuery);
    }

    @Test
    public void inner_and_sql_added(){
        QueryBuilder innerBuilder = new QueryBuilder(User.class);
        innerBuilder.query(DEFAULT_ATTR).equalTo(IRRELEVANT);

        queryBuilder.query(DEFAULT_ATTR).contains(IRRELEVANT).and(innerBuilder).startIndex(START_INDEX);

        String exceptedQuery = DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" and (" + DEFAULT_ATTR
                + " eq \"" + IRRELEVANT + "\")&startIndex=" + START_INDEX;
        buildStringMeetsExpectation(exceptedQuery);
    }

    @Test
    public void inner_or_sql_added(){
        QueryBuilder innerBuilder = new QueryBuilder(User.class);
        innerBuilder.query(DEFAULT_ATTR).equalTo(IRRELEVANT);

        queryBuilder.query(DEFAULT_ATTR).contains(IRRELEVANT).or(innerBuilder).startIndex(START_INDEX);

        String exceptedQuery = DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" or (" + DEFAULT_ATTR
                + " eq \"" + IRRELEVANT + "\")&startIndex=" + START_INDEX;
        buildStringMeetsExpectation(exceptedQuery);
    }

    private void buildStringMeetsExpectation(String buildString) {
        Query expectedQuery = new Query(buildString);
        assertEquals(expectedQuery, queryBuilder.build());
    }
}
