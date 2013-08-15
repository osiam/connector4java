package org.osiam.client.query;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.Before;
import org.junit.Test;
import org.osiam.client.exception.InvalidAttributeException;
import org.osiam.resources.scim.Group;

import com.google.common.base.Charsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GroupQueryBuilderTest {

    private static final String DEFAULT_ATTR = "displayName";
    private static final String IRRELEVANT = "irrelevant";
    private Query.Builder queryBuilder;

    @Before
    public void setUp() {
        queryBuilder = new Query.Builder(Group.class);
    }

    @Test
    public void flat_attribute_is_added_to_query() {
        queryBuilder.filter(DEFAULT_ATTR);
        buildStringMeetsExpectation("filter=" + DEFAULT_ATTR);
    }

    @Test
    public void and_attribute_is_added_correctly() throws UnsupportedEncodingException {
        queryBuilder.filter(DEFAULT_ATTR)
                .contains(IRRELEVANT)
                .and(DEFAULT_ATTR).contains(IRRELEVANT);
        buildStringMeetsExpectation("filter=" + URLEncoder.encode(DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" and " + DEFAULT_ATTR + " co \"" + IRRELEVANT + "\"", Charsets.UTF_8.name()));
    }

    @Test(expected = InvalidAttributeException.class)
    public void exception_raised_when_attr_is_not_valid() {
        queryBuilder.filter(IRRELEVANT);
    }

    private void buildStringMeetsExpectation(String buildString) {
        Query expectedQuery = new Query(buildString);
        try {
	    assertEquals(expectedQuery, queryBuilder.build());
	} catch (UnsupportedEncodingException e) {
	    fail("Filter contains non UTF-8 characters");
	}
    }
}
