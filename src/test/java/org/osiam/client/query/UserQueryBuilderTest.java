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
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        DATE_STR = df.format(DATE);
    }

    @Test
    public void nested_email_attribute_is_added_to_query() {
        filter = new Query.Filter(User.class, VALID_EMAIL_ATTR.equalTo(IRRELEVANT));
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(VALID_EMAIL_ATTR + " eq \"" + IRRELEVANT + "\""));
    }

    @Test
    public void nested_name_attribute_is_added_to_query() {
        filter = new Query.Filter(User.class, VALID_NAME_ATTR.equalTo(IRRELEVANT));
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(VALID_NAME_ATTR + " eq \"" + IRRELEVANT + "\""));
    }

    @Test
    public void nested_meta_attribute_is_added_to_query() {
        filter = new Query.Filter(User.class, VALID_META_ATTR.greaterThan(DATE));
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(VALID_META_ATTR + " gt \"" + DATE_STR + "\""));
    }

    @Test
    public void flat_attribute_is_added_to_query() {
        filter = new Query.Filter(User.class, DEFAULT_ATTR.equalTo(IRRELEVANT));
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(DEFAULT_ATTR + " eq \"" + IRRELEVANT + "\""));
    }

    @Test
    public void and_attribute_is_added_correctly() {
        filter = new Query.Filter(User.class, DEFAULT_ATTR.contains(IRRELEVANT)).and(DEFAULT_ATTR.contains(IRRELEVANT));
        queryBuilder.setFilter(filter);
        
        buildStringMeetsExpectation(FILTER + encodeExpectedString(DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" and " + DEFAULT_ATTR + " co \"" + IRRELEVANT + "\""));
    }


    @Test
    public void or_attribute_is_added_correctly() {
        filter = new Query.Filter(User.class, DEFAULT_ATTR.contains(IRRELEVANT)).or(DEFAULT_ATTR.contains(IRRELEVANT));
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" or " + DEFAULT_ATTR + " co \"" + IRRELEVANT + "\""));
    }

    @Test
    public void filter_contains_is_added_to_query() {
        filter = new Query.Filter(User.class, DEFAULT_ATTR.contains(IRRELEVANT));
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(DEFAULT_ATTR + " co \"" + IRRELEVANT + "\""));
    }

    @Test
    public void filter_equals_is_added_to_query() {
        filter = new Query.Filter(User.class, DEFAULT_ATTR.equalTo(IRRELEVANT));
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(DEFAULT_ATTR + " eq \"" + IRRELEVANT + "\""));
    }

    @Test
    public void filter_startsWith_is_added_to_query() {
        filter = new Query.Filter(User.class, DEFAULT_ATTR.startsWith(IRRELEVANT));
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(DEFAULT_ATTR + " sw \"" + IRRELEVANT + "\""));
    }

    @Test
    public void filter_present_is_added_to_query() {
        filter = new Query.Filter(User.class, DEFAULT_ATTR.present());
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(DEFAULT_ATTR + " pr "));
    }

    @Test
    public void filter_greater_than_is_added_to_query() {
        filter = new Query.Filter(User.class, VALID_META_ATTR.greaterThan(DATE));
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(VALID_META_ATTR + " gt \"" + DATE_STR + "\""));
    }

    @Test
    public void filter_greater_equals_is_added_to_query() {
        filter = new Query.Filter(User.class, VALID_META_ATTR.greaterEquals(DATE));
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(VALID_META_ATTR + " ge \"" + DATE_STR + "\""));
    }

    @Test
    public void filter_less_than_is_added_to_query() {
        filter = new Query.Filter(User.class, VALID_META_ATTR.lessThan(DATE));
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(VALID_META_ATTR + " lt \"" + DATE_STR + "\""));
    }

    @Test
    public void filter_less_equals_is_added_to_query() {
        filter = new Query.Filter(User.class, VALID_META_ATTR.lessEquals(DATE));
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(VALID_META_ATTR + " le \"" + DATE_STR + "\""));
    }

    @Test(expected = InvalidAttributeException.class)
    public void exception_raised_when_nested_attr_is_not_valid() {
        filter = new Query.Filter(User.class, IRRELEVANT_FIELD.contains(IRRELEVANT));
        queryBuilder.setFilter(filter);
    }

    @Test
    public void sort_order_ascending() {
        queryBuilder.setSortOrder(SortOrder.ASCENDING);
        buildStringMeetsExpectation("sortOrder=ascending");
    }

    @Test
    public void sort_order_descending() {
        queryBuilder.setSortOrder(SortOrder.DESCENDING);
        buildStringMeetsExpectation("sortOrder=descending");
    }

    @Test
    public void query_and_sort_order_ascending() {
        filter = new Query.Filter(User.class, DEFAULT_ATTR.contains(IRRELEVANT)).and(DEFAULT_ATTR.contains(IRRELEVANT));
        queryBuilder.setFilter(filter)
                .setSortOrder(SortOrder.ASCENDING);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" and " + DEFAULT_ATTR + " co \"" + IRRELEVANT + "\"") + "&sortOrder=ascending");
    }

    @Test
    public void two_times_set_sort_order_descending() {
        queryBuilder.setSortOrder(SortOrder.ASCENDING).setSortOrder(SortOrder.DESCENDING);
        buildStringMeetsExpectation("sortOrder=descending");
    }

    @Test
    public void start_index_added() {
        queryBuilder.setStartIndex(START_INDEX);
        buildStringMeetsExpectation("startIndex=" + START_INDEX);
    }

    @Test
    public void count_per_page_added() {
        queryBuilder.setCountPerPage(COUNT_PER_PAGE);
        buildStringMeetsExpectation("count=" + COUNT_PER_PAGE);
    }

    @Test
    public void start_index_and_count_added_to_complete_query() {
        filter = new Query.Filter(User.class, DEFAULT_ATTR.contains(IRRELEVANT)).and(DEFAULT_ATTR.contains(IRRELEVANT));
        queryBuilder.setFilter(filter)
                .setStartIndex(START_INDEX)
                .setCountPerPage(COUNT_PER_PAGE)
                .setSortOrder(SortOrder.ASCENDING);
        String exceptedQuery = encodeExpectedString(DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" and " + DEFAULT_ATTR
                + " co \"" + IRRELEVANT + "\"") + "&sortOrder=ascending&count=" + COUNT_PER_PAGE
                + "&startIndex=" + START_INDEX;
        buildStringMeetsExpectation(FILTER + exceptedQuery);
    }

    @Test
    public void inner_and_sql_added() {
        Query.Filter innerFilter = new Query.Filter(User.class, DEFAULT_ATTR.equalTo(IRRELEVANT));

        filter = new Query.Filter(User.class, DEFAULT_ATTR.contains(IRRELEVANT)).and(innerFilter);
        queryBuilder.setFilter(filter).setStartIndex(START_INDEX);

        String exceptedQuery = FILTER + encodeExpectedString(DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" and (" + DEFAULT_ATTR
                + " eq \"" + IRRELEVANT + "\")") + "&startIndex=" + START_INDEX;
        buildStringMeetsExpectation(exceptedQuery);
    }

    @Test
    public void inner_or_sql_added() {
        Query.Filter innerFilter = new Query.Filter(User.class, DEFAULT_ATTR.equalTo(IRRELEVANT));

        filter = new Query.Filter(User.class, DEFAULT_ATTR.contains(IRRELEVANT)).or(innerFilter);
        queryBuilder.setFilter(filter).setStartIndex(START_INDEX);

        String exceptedQuery = FILTER + encodeExpectedString(DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" or (" + DEFAULT_ATTR
                + " eq \"" + IRRELEVANT + "\")") + "&startIndex=" + START_INDEX;
        buildStringMeetsExpectation(exceptedQuery);
    }

    @Test
    public void one_sort_by_value_added(){
        queryBuilder.setSortBy(DEFAULT_ATTR);
        String exceptedQuery = "sortBy=" + DEFAULT_ATTR;
        buildStringMeetsExpectation(exceptedQuery);
    }

    @Test
    public void complet_query_with_all_attributes(){
        Query.Filter innerFilter = new Query.Filter(User.class, DEFAULT_ATTR.equalTo(IRRELEVANT));

        filter = new Query.Filter(User.class, DEFAULT_ATTR.contains(IRRELEVANT)).or(innerFilter);
        queryBuilder.setFilter(filter).setStartIndex(START_INDEX)
                .setCountPerPage(COUNT_PER_PAGE).setSortBy(DEFAULT_ATTR).setSortOrder(SortOrder.ASCENDING);

        String exceptedQuery = FILTER + encodeExpectedString(DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" or (" + DEFAULT_ATTR
                + " eq \"" + IRRELEVANT + "\")") + "&sortBy=" + DEFAULT_ATTR + "&sortOrder=" + SortOrder.ASCENDING.toString()
                + "&count=" + COUNT_PER_PAGE + "&startIndex=" + START_INDEX;
        buildStringMeetsExpectation(exceptedQuery);
    }

    @Test (expected = InvalidAttributeException.class)
    public void invalid_attribut_to_sort_by_added(){
        queryBuilder.setSortBy(IRRELEVANT_FIELD);
        fail("Exception excpected");
    }

    @Test (expected = InvalidAttributeException.class)
    public void invalid_attribut_to_filter_added(){
        new Query.Filter(User.class, IRRELEVANT_FIELD.contains(IRRELEVANT));
        fail("Exception excpected");
    }

    @Test (expected = InvalidAttributeException.class)
    public void invalid_attribut_to_and_added(){
        new Query.Filter(User.class, DEFAULT_ATTR.contains(IRRELEVANT)).and(IRRELEVANT_FIELD.contains(IRRELEVANT));
        fail("Exception excpected");
    }

    @Test (expected = InvalidAttributeException.class)
    public void invalid_attribut_to_or_added(){
        new Query.Filter(User.class, DEFAULT_ATTR.contains(IRRELEVANT)).or(IRRELEVANT_FIELD.contains(IRRELEVANT));
        fail("Exception excpected");
    }

    @Test
    public void attribute_email_type_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.emails.type.equalTo(IRRELEVANT));
    }

    @Test
    public void attribute_email_value_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.emails.value.equalTo(IRRELEVANT));
    }

    @Test
    public void attribute_meta_created_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.meta.created.equalTo(DATE));
    }

    @Test
    public void attribute_meta_last_modified_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.meta.lastModified.equalTo(DATE));
    }

    @Test
    public void attribute_meta_location_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.meta.location.equalTo(IRRELEVANT));
    }

    @Test
    public void attribute_meta_resource_type_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.meta.resourceType.equalTo(IRRELEVANT));
    }

    @Test
    public void attribute_meta_version_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.meta.version.equalTo(IRRELEVANT));
    }

    @Test
    public void attribute_name_formatted_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.name.formatted.equalTo(IRRELEVANT));
    }

    @Test
    public void attribute_name_fanily_name_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.name.familyName.equalTo(IRRELEVANT));
    }

    @Test
    public void attribute_name_given_name_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.name.givenName.equalTo(IRRELEVANT));
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
