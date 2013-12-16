/* Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package org.osiam.client.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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

    private static final StringAttribute DEFAULT_ATTR = User_.Name.givenName;
    private static final DateAttribute VALID_META_ATTR = User_.Meta.created;
    private static final StringAttribute VALID_NAME_ATTR = User_.Name.givenName;
    private static final StringAttribute VALID_EMAIL_ATTR = User_.Emails.value;
    private static final String IRRELEVANT = "irrelevant";
    private static final String IRRELEVANT_EMAIL_TYPE = "other";
    private static final String IRRELEVANT_FIELD = "members.invalid";
    private static final int START_INDEX = 5;
    private static final int COUNT_PER_PAGE = 7;
    private static final String FILTER = "filter=";
    private Query.Builder queryBuilder;
    private Query.Filter filter;
    private DateTime dateNow = new DateTime();
    private String dateNowString;

    @Before
    public void setUp() throws UnsupportedEncodingException {
        queryBuilder = new Query.Builder(User.class);
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        dateNowString = dateFormat.print(dateNow);
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
        filter = new Query.Filter(User.class, VALID_META_ATTR.greaterThan(dateNow));
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(VALID_META_ATTR + " gt \"" + dateNowString + "\""));
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
        filter = new Query.Filter(User.class, VALID_META_ATTR.greaterThan(dateNow));
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(VALID_META_ATTR + " gt \"" + dateNowString + "\""));
    }

    @Test
    public void filter_greater_equals_is_added_to_query() {
        filter = new Query.Filter(User.class, VALID_META_ATTR.greaterEquals(dateNow));
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(VALID_META_ATTR + " ge \"" + dateNowString + "\""));
    }

    @Test
    public void filter_less_than_is_added_to_query() {
        filter = new Query.Filter(User.class, VALID_META_ATTR.lessThan(dateNow));
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(VALID_META_ATTR + " lt \"" + dateNowString + "\""));
    }

    @Test
    public void filter_less_equals_is_added_to_query() {
        filter = new Query.Filter(User.class, VALID_META_ATTR.lessEquals(dateNow));
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation(FILTER + encodeExpectedString(VALID_META_ATTR + " le \"" + dateNowString + "\""));
    }

    @Test(expected = InvalidAttributeException.class)
    public void exception_raised_when_nested_attr_is_not_valid() {
        StringAttribute stringAttribute = new StringAttribute(IRRELEVANT_FIELD);
        filter = new Query.Filter(User.class, stringAttribute.contains(IRRELEVANT));
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
        StringAttribute stringAttribute = new StringAttribute(IRRELEVANT_FIELD);
        queryBuilder.setSortBy(stringAttribute);
        fail("Exception excpected");
    }

    @Test (expected = InvalidAttributeException.class)
    public void invalid_attribut_to_filter_added(){
        StringAttribute stringAttribute = new StringAttribute(IRRELEVANT_FIELD);
        new Query.Filter(User.class, stringAttribute.contains(IRRELEVANT));
        fail("Exception excpected");
    }

    @Test (expected = InvalidAttributeException.class)
    public void invalid_attribut_to_and_added(){
        StringAttribute stringAttribute = new StringAttribute(IRRELEVANT_FIELD);
        new Query.Filter(User.class, DEFAULT_ATTR.contains(IRRELEVANT)).and(stringAttribute.contains(IRRELEVANT));
        fail("Exception excpected");
    }

    @Test (expected = InvalidAttributeException.class)
    public void invalid_attribut_to_or_added(){
        StringAttribute stringAttribute = new StringAttribute(IRRELEVANT_FIELD);
        new Query.Filter(User.class, DEFAULT_ATTR.contains(IRRELEVANT)).or(stringAttribute.contains(IRRELEVANT));
        fail("Exception excpected");
    }

    @Test
    public void attribute_email_type_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.Emails.type.equalTo(IRRELEVANT_EMAIL_TYPE));
    }

    @Test
    public void attribute_email_value_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.Emails.value.equalTo(IRRELEVANT));
    }

    @Test
    public void attribute_meta_created_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.Meta.created.equalTo(dateNow));
    }

    @Test
    public void attribute_meta_last_modified_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.Meta.lastModified.equalTo(dateNow));
    }

    @Test
    public void attribute_meta_location_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.Meta.location.equalTo(IRRELEVANT));
    }

    @Test
    public void attribute_meta_resource_type_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.Meta.resourceType.equalTo(IRRELEVANT));
    }

    @Test
    public void attribute_meta_version_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.Meta.version.equalTo(IRRELEVANT));
    }

    @Test
    public void attribute_name_formatted_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.Name.formatted.equalTo(IRRELEVANT));
    }

    @Test
    public void attribute_name_fanily_name_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.Name.familyName.equalTo(IRRELEVANT));
    }

    @Test
    public void attribute_name_given_name_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, User_.Name.givenName.equalTo(IRRELEVANT));
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
