package org.osiam.client.query;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;
import org.osiam.client.connector.OsiamConnector;

public class StringQueryBuilderTest {

    @Test
    public void stringQueryBuilder_works_as_aspected() {
        StringQueryBuilder queryBuilder = new StringQueryBuilder()
                .setFilter("userName eq  \"marissa\"")
                .setSortBy("userName")
                .setCount(50)
                .setSortOrder("ascending")
                .setStartIndex(3);
        String query;

        query = queryBuilder.build();
        String excpectedtQuery = "filter=userName+eq++%22marissa%22&sortBy=userName&sortOrder=ascending&count=50&startIndex=3";
        assertThat(query, is(excpectedtQuery));
    }

    public void stringQueryBuilder_works_as_aspected_while_two_attributes_are_set() {
        StringQueryBuilder queryBuilder = new StringQueryBuilder()
                .setFilter("userName eq  \"marissa\"")
                .setCount(50);
        String query = queryBuilder.build();

        String excpectedtQuery = "&filter=userName+eq++%22marissa%22&count=50";
        assertThat(query, is(excpectedtQuery));
    }
}
