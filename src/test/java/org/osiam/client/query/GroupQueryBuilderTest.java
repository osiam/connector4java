package org.osiam.client.query;
/*
 * for licensing see the file license.txt.
 */

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.joda.time.DateTime;
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
    }

    @Test
    public void flat_attribute_is_added_to_query() {
        filter = new Query.Filter(User.class, DEFAULT_ATTR.equalTo(IRRELEVANT));
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation("filter=" + DEFAULT_ATTR + "+eq+%22" + IRRELEVANT + "%22");
    }

    @Test
    public void and_attribute_is_added_correctly() throws UnsupportedEncodingException {
        filter = new Query.Filter(User.class, DEFAULT_ATTR.contains(IRRELEVANT)).and(DEFAULT_ATTR.contains(IRRELEVANT));
        queryBuilder.setFilter(filter);
        buildStringMeetsExpectation("filter=" + URLEncoder.encode(DEFAULT_ATTR + " co \"" + IRRELEVANT + "\" and " + DEFAULT_ATTR + " co \"" + IRRELEVANT + "\"", Charsets.UTF_8.name()));
    }

    @Test
    public void attribute_meta_created_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, Group_.Meta.created.equalTo(new DateTime()));
    }

    @Test
    public void attribute_meta_last_modified_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, Group_.Meta.lastModified.equalTo(new DateTime()));
    }

    @Test
    public void attribute_meta_location_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, Group_.Meta.location.equalTo(IRRELEVANT));
    }

    @Test
    public void attribute_meta_resource_type_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, Group_.Meta.resourceType.equalTo(IRRELEVANT));
    }

    @Test
    public void attribute_meta_version_is_correct_reconised_over_reflection(){
        new Query.Filter(User.class, Group_.Meta.version.equalTo(IRRELEVANT));
    }

    private void buildStringMeetsExpectation(String buildString) {
        Query expectedQuery = new Query(buildString);
	    assertEquals(expectedQuery, queryBuilder.build());
    }
}
