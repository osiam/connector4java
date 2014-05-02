package org.osiam.client.nquery;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class QueryBuilderTest {

    QueryBuilder queryBuilder = new QueryBuilder();
    
    @Test
    public void build_empty_query() {
        Query query = queryBuilder.build();
        
        assertThat(query.getAttributes(), isEmptyOrNullString());
        assertThat(query.getFilter(), isEmptyOrNullString());
        assertThat(query.getSortBy(), isEmptyOrNullString());
        assertThat(query.getSortOrder(), isEmptyOrNullString());
        assertThat(query.getStartIndex(), is(1L));
        assertThat(query.getCount(), is(100));
    }
    
    @Test
    public void setting_all_values_of_the_query_works() {
        Query query = queryBuilder
                .attributes("attributes")
                .filter("filter")
                .sortBy("sortBy")
                .sortOrder("sortOrder")
                .startIndex(11)
                .count(10)
                .build();
        
        assertThat(query.getAttributes(), is(equalTo("attributes")));
        assertThat(query.getFilter(), is(equalTo("filter")));
        assertThat(query.getSortBy(), is(equalTo("sortBy")));
        assertThat(query.getSortOrder(), is(equalTo("sortOrder")));
        assertThat(query.getStartIndex(), is(11L));
        assertThat(query.getCount(), is(10));
    }

}
