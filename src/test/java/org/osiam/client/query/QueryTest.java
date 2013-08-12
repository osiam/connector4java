package org.osiam.client.query;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QueryTest {

    private static final String QUERY_LACKING_COUNT_AND_INDEX = "IRRELEVANT";
    private static final int EXPECTED_INT = 200;

    Query query;

    @Before
    public void setUp() {
        query = new Query(QUERY_LACKING_COUNT_AND_INDEX);
    }

    @Test
    public void query_string_is_not_altered_on_construction() {
        assertEquals(QUERY_LACKING_COUNT_AND_INDEX, query.toString());
    }

    @Test
    public void no_parameters_in_query_returns_default_index() {
        assertEquals(0, query.getStartIndex());
    }

    @Test
    public void no_parameters_in_query_returns_default_count() {
        assertEquals(100, query.getCount());
    }

    @Test
    public void index_is_appended_and_incremented() {
        assertEquals(QUERY_LACKING_COUNT_AND_INDEX + "&startIndex=100", query.nextPage().toString());
    }

    @Test
    public void existing_index_is_incremented() {
        givenQuery(QUERY_LACKING_COUNT_AND_INDEX + "&startIndex=200" + QUERY_LACKING_COUNT_AND_INDEX);
        assertEquals(QUERY_LACKING_COUNT_AND_INDEX + "&startIndex=300" + QUERY_LACKING_COUNT_AND_INDEX, query.nextPage().toString());
    }

    @Test
    public void index_is_decremented() {
        givenQuery(QUERY_LACKING_COUNT_AND_INDEX + "&startIndex=200");
        assertEquals(QUERY_LACKING_COUNT_AND_INDEX + "&startIndex=100", query.previousPage().toString());
    }

    @Test(expected = IllegalStateException.class)
    public void exception_is_raised_if_index_gets_negative() {
        givenQuery(QUERY_LACKING_COUNT_AND_INDEX);
        query.previousPage();
    }

    @Test
    public void count_is_found_in_front_of_string() {
        givenQuery("count=200&" + QUERY_LACKING_COUNT_AND_INDEX);
        assertEquals(EXPECTED_INT, query.getCount());
    }

    @Test
    public void count_is_found_at_end_of_string() {
        givenQuery(QUERY_LACKING_COUNT_AND_INDEX + "&count=200");
        assertEquals(EXPECTED_INT, query.getCount());
    }

    @Test
    public void index_is_found_in_front_of_string() {
        givenQuery("startIndex=200&" + QUERY_LACKING_COUNT_AND_INDEX);
        assertEquals(EXPECTED_INT, query.getStartIndex());
    }

    @Test
    public void index_is_found_at_end_of_string() {
        givenQuery(QUERY_LACKING_COUNT_AND_INDEX + "&startIndex=200");
        assertEquals(EXPECTED_INT, query.getStartIndex());
    }

    private void givenQuery(String queryString) {
        query = new Query(queryString);
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Query.class)
                .usingGetClass()
                .suppress(Warning.NULL_FIELDS) // Can't be null ever.
                .verify();
    }

}