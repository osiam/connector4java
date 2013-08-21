package org.osiam.client.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.osiam.client.exception.InvalidAttributeException;
import org.osiam.client.query.metamodel.DateAttribute;
import org.osiam.client.query.metamodel.Group_;
import org.osiam.client.query.metamodel.StringAttribute;
import org.osiam.client.query.metamodel.User_;
import org.osiam.resources.scim.User;

import com.google.common.base.Charsets;

public class UserQueryBuilderTest {

    private static final StringAttribute DEFAULT_ATTR = User_.name.givenName;
    private static final DateAttribute VALID_META_ATTR = User_.meta.created;
    private static final StringAttribute VALID_NAME_ATTR = User_.name.givenName;
    private static final StringAttribute VALID_EMAIL_ATTR = User_.emails.value;
    //private static final String INVALID_EMAIL_ATTR = "emails.false";
    private static final String IRRELEVANT = "irrelevant";
    private static final StringAttribute IRRELEVANT_FIELD = Group_.members.value;
    private static final int START_INDEX = 5;
    private static final int COUNT_PER_PAGE = 7;
    private static final String FILTER = "filter=";
    private Query.Builder queryBuilder;
    private Query.Filter filter;
    private Date DATE = new Date();
    private String DATE_STR;

    @Before
    public void setUp() throws UnsupportedEncodingException {
        queryBuilder = new Query.Builder(User.class);
        filter = new Query.Filter(User.class);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        DATE_STR = df.format(DATE);
    }

    @Test
    public void nested_email_attribute_is_added_to_query() {
        filter = filter.startsWith(VALID_EMAIL_ATTR.equalTo(IRRELEVANT));
        queryBuilder.filter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(VALID_EMAIL_ATTR + " eq \"" + IRRELEVANT + "\""));
    }

    @Test
    public void nested_name_attribute_is_added_to_query() {
        filter = filter.startsWith(VALID_NAME_ATTR.equalTo(IRRELEVANT));
        queryBuilder.filter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(VALID_NAME_ATTR + " eq \"" + IRRELEVANT + "\""));
    }

    @Test
    public void nested_meta_attribute_is_added_to_query() {
        filter = filter.startsWith(VALID_META_ATTR.greaterThan(DATE));
        queryBuilder.filter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(VALID_META_ATTR + " gt \"" + DATE_STR + "\""));
    }

    @Test
    public void flat_attribute_is_added_to_query() {
        filter = filter.startsWith(DEFAULT_ATTR.equalTo(IRRELEVANT));
        queryBuilder.filter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(DEFAULT_ATTR + " eq \"" + IRRELEVANT + "\""));
    }

    @Test
    public void and_attribute_is_added_correctly() {
        filter = filter.startsWith(DEFAULT_ATTR.contains(IRRELEVANT)).and(DEFAULT_ATTR.contains(IRRELEVANT));
        queryBuilder.filter(filter);
        
        buildStringMeetsExpectation(FILTER + encodeExpectedString(DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" and " + DEFAULT_ATTR + " co \"" + IRRELEVANT + "\""));
    }


    @Test
    public void or_attribute_is_added_correctly() {
        filter = filter.startsWith(DEFAULT_ATTR.contains(IRRELEVANT)).or(DEFAULT_ATTR.contains(IRRELEVANT));
        queryBuilder.filter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" or " + DEFAULT_ATTR + " co \"" + IRRELEVANT + "\""));
    }

    @Test
    public void filter_contains_is_added_to_query() {
        filter = filter.startsWith(DEFAULT_ATTR.contains(IRRELEVANT));
        queryBuilder.filter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(DEFAULT_ATTR + " co \"" + IRRELEVANT + "\""));
    }

    @Test
    public void filter_equals_is_added_to_query() {
        filter = filter.startsWith(DEFAULT_ATTR.equalTo(IRRELEVANT));
        queryBuilder.filter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(DEFAULT_ATTR + " eq \"" + IRRELEVANT + "\""));
    }

    @Test
    public void filter_startsWith_is_added_to_query() {
        filter = filter.startsWith(DEFAULT_ATTR.startsWith(IRRELEVANT));
        queryBuilder.filter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(DEFAULT_ATTR + " sw \"" + IRRELEVANT + "\""));
    }

    @Test
    public void filter_present_is_added_to_query() {
        filter = filter.startsWith(DEFAULT_ATTR.present());
        queryBuilder.filter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(DEFAULT_ATTR + " pr "));
    }

    @Test
    public void filter_greater_than_is_added_to_query() {
        filter = filter.startsWith(VALID_META_ATTR.greaterThan(DATE));
        queryBuilder.filter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(VALID_META_ATTR + " gt \"" + DATE_STR + "\""));
    }

    @Test
    public void filter_greater_equals_is_added_to_query() {
        filter = filter.startsWith(VALID_META_ATTR.greaterEquals(DATE));
        queryBuilder.filter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(VALID_META_ATTR + " ge \"" + DATE_STR + "\""));
    }

    @Test
    public void filter_less_than_is_added_to_query() {
        filter = filter.startsWith(VALID_META_ATTR.lessThan(DATE));
        queryBuilder.filter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(VALID_META_ATTR + " lt \"" + DATE_STR + "\""));
    }

    @Test
    public void filter_less_equals_is_added_to_query() {
        filter = filter.startsWith(VALID_META_ATTR.lessEquals(DATE));
        queryBuilder.filter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(VALID_META_ATTR + " le \"" + DATE_STR + "\""));
    }

    @Test(expected = InvalidAttributeException.class)
    public void exception_raised_when_nested_attr_is_not_valid() {
        filter = filter.startsWith(IRRELEVANT_FIELD.contains(IRRELEVANT));
        queryBuilder.filter(filter);
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
        filter = filter.startsWith(DEFAULT_ATTR.contains(IRRELEVANT)).and(DEFAULT_ATTR.contains(IRRELEVANT));
        queryBuilder.filter(filter)
                .withSortOrder(SortOrder.ASCENDING);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" and " + DEFAULT_ATTR + " co \"" + IRRELEVANT + "\"") + "&sortOrder=ascending");
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
        filter = filter.startsWith(DEFAULT_ATTR.contains(IRRELEVANT)).and(DEFAULT_ATTR.contains(IRRELEVANT));
        queryBuilder.filter(filter)
                .startIndex(START_INDEX)
                .countPerPage(COUNT_PER_PAGE)
                .withSortOrder(SortOrder.ASCENDING);
        String exceptedQuery = encodeExpectedString(DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" and " + DEFAULT_ATTR
                + " co \"" + IRRELEVANT + "\"") + "&sortOrder=ascending&count=" + COUNT_PER_PAGE
                + "&startIndex=" + START_INDEX;
        buildStringMeetsExpectation(FILTER + exceptedQuery);
    }

    @Test
    public void inner_and_sql_added() {
        Query.Filter innerFilter = new Query.Filter(User.class);
        innerFilter = innerFilter.startsWith(DEFAULT_ATTR.equalTo(IRRELEVANT));

        filter = filter.startsWith(DEFAULT_ATTR.contains(IRRELEVANT)).and(innerFilter);
        queryBuilder.filter(filter).startIndex(START_INDEX);

        String exceptedQuery = FILTER + encodeExpectedString(DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" and (" + DEFAULT_ATTR
                + " eq \"" + IRRELEVANT + "\")") + "&startIndex=" + START_INDEX;
        buildStringMeetsExpectation(exceptedQuery);
    }

    @Test
    public void inner_or_sql_added() {
        Query.Filter innerFilter = new Query.Filter(User.class);
        innerFilter = innerFilter.startsWith(DEFAULT_ATTR.equalTo(IRRELEVANT));

        filter = filter.startsWith(DEFAULT_ATTR.contains(IRRELEVANT)).or(innerFilter);
        queryBuilder.filter(filter).startIndex(START_INDEX);

        String exceptedQuery = FILTER + encodeExpectedString(DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" or (" + DEFAULT_ATTR
                + " eq \"" + IRRELEVANT + "\")") + "&startIndex=" + START_INDEX;
        buildStringMeetsExpectation(exceptedQuery);
    }

    @Test
    public void one_sort_by_value_added(){
        queryBuilder.sortBy(DEFAULT_ATTR);
        String exceptedQuery = "sortBy=" + DEFAULT_ATTR;
        buildStringMeetsExpectation(exceptedQuery);
    }

    @Test
    public void complet_query_with_all_attributes(){
        Query.Filter innerFilter = new Query.Filter(User.class);
        innerFilter = innerFilter.startsWith(DEFAULT_ATTR.equalTo(IRRELEVANT));

        filter = filter.startsWith(DEFAULT_ATTR.contains(IRRELEVANT)).or(innerFilter);
        queryBuilder.filter(filter).startIndex(START_INDEX)
                .countPerPage(COUNT_PER_PAGE).sortBy(DEFAULT_ATTR).withSortOrder(SortOrder.ASCENDING);

        String exceptedQuery = FILTER + encodeExpectedString(DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" or (" + DEFAULT_ATTR
                + " eq \"" + IRRELEVANT + "\")") + "&sortBy=" + DEFAULT_ATTR + "&sortOrder=" + SortOrder.ASCENDING.toString()
                + "&count=" + COUNT_PER_PAGE + "&startIndex=" + START_INDEX;
        buildStringMeetsExpectation(exceptedQuery);
    }

    @Test (expected = InvalidAttributeException.class)
    public void invalid_attribut_to_sort_by_added(){
        queryBuilder.sortBy(IRRELEVANT_FIELD);
        fail("Exception excpected");
    }

    @Test (expected = InvalidAttributeException.class)
    public void invalid_attribut_to_filter_added(){
        filter.startsWith(IRRELEVANT_FIELD.contains(IRRELEVANT));
        fail("Exception excpected");
    }

    @Test (expected = InvalidAttributeException.class)
    public void invalid_attribut_to_and_added(){
        filter.startsWith(DEFAULT_ATTR.contains(IRRELEVANT)).and(IRRELEVANT_FIELD.contains(IRRELEVANT));
        fail("Exception excpected");
    }

    @Test (expected = InvalidAttributeException.class)
    public void invalid_attribut_to_or_added(){
        filter.startsWith(DEFAULT_ATTR.contains(IRRELEVANT)).or(IRRELEVANT_FIELD.contains(IRRELEVANT));
        fail("Exception excpected");
    }

    private void buildStringMeetsExpectation(String buildString) {
        Query expectedQuery = new Query(buildString);
	    assertEquals(expectedQuery, queryBuilder.build());
    }
    
    private String encodeExpectedString(String string) {
	try {
	    return URLEncoder.encode(string, Charsets.UTF_8.name());
	} catch (UnsupportedEncodingException e) {
	    fail("Filter contains non UTF-8 characters");
	}
	
	return "";
    }
}
