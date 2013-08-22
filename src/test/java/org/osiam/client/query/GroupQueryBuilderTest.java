package org.osiam.client.query;


import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.Before;
import org.junit.Test;
import org.osiam.client.query.metamodel.Group_;
import org.osiam.client.query.metamodel.StringAttribute;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.User;

import com.google.common.base.Charsets;

public class GroupQueryBuilderTest {

    private static final StringAttribute DEFAULT_ATTR = Group_.displayName;
    private static final String IRRELEVANT = "irrelevant";
    private Query.Builder queryBuilder;
    private Query.Filter filter;

    @Before
    public void setUp() {
        queryBuilder = new Query.Builder(Group.class);
        filter = new Query.Filter(User.class);
    }

    @Test
    public void flat_attribute_is_added_to_query() {
        filter = filter.startsWith(DEFAULT_ATTR.equalTo(IRRELEVANT));
        queryBuilder.filter(filter);
        buildStringMeetsExpectation("filter=" + DEFAULT_ATTR + "+eq+%22" + IRRELEVANT + "%22");
    }

    @Test
    public void and_attribute_is_added_correctly() throws UnsupportedEncodingException {
        filter = filter.startsWith(DEFAULT_ATTR.contains(IRRELEVANT)).and(DEFAULT_ATTR.contains(IRRELEVANT));
        queryBuilder.filter(filter);
        buildStringMeetsExpectation("filter=" + URLEncoder.encode(DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" and " + DEFAULT_ATTR + " co \"" + IRRELEVANT + "\"", Charsets.UTF_8.name()));
    }

    private void buildStringMeetsExpectation(String buildString) {
        Query expectedQuery = new Query(buildString);
	    assertEquals(expectedQuery, queryBuilder.build());
    }
}
