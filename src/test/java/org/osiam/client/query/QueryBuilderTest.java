package org.osiam.client.query;

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
                .ascending("sortByAttribute")
                .startIndex(11L)
                .count(10)
                .build();

        assertThat(query.getAttributes(), is(equalTo("attributes")));
        assertThat(query.getFilter(), is(equalTo("filter")));
        assertThat(query.getSortBy(), is(equalTo("sortByAttribute")));
        assertThat(query.getSortOrder(), is(equalTo("ascending")));
        assertThat(query.getStartIndex(), is(11L));
        assertThat(query.getCount(), is(10));
    }

    @Test
    public void using_the_copy_of_constructor_works() {
        Query queryOriginal = queryBuilder
                .attributes("attributes")
                .filter("filter")
                .ascending("sortByAttribute")
                .startIndex(11L)
                .count(10)
                .build();

        Query queryCopy = new QueryBuilder(queryOriginal).build();

        assertThat(queryCopy.getAttributes(), is(equalTo(queryOriginal.getAttributes())));
        assertThat(queryCopy.getFilter(), is(equalTo(queryOriginal.getFilter())));
        assertThat(queryCopy.getSortBy(), is(equalTo(queryOriginal.getSortBy())));
        assertThat(queryCopy.getSortOrder(), is(equalTo(queryOriginal.getSortOrder())));
        assertThat(queryCopy.getStartIndex(), is(queryOriginal.getStartIndex()));
        assertThat(queryCopy.getCount(), is(queryOriginal.getCount()));
    }

    @Test
    public void start_index_cannot_be_less_than_one() {
        Query query = queryBuilder
                .startIndex(0)
                .build();

        assertThat(query.getStartIndex(), is(1L));
    }

    @Test
    public void count_cannot_be_less_than_one() {
        Query query = queryBuilder
                .count(0)
                .build();

        assertThat(query.getCount(), is(100));
    }

    @Test
    public void count_cannot_be_greater_than_one_hundred() {
        Query query = queryBuilder
                .count(101)
                .build();

        assertThat(query.getCount(), is(100));
    }

    @Test
    public void sorting_ascending_works() {
        Query query = queryBuilder
                .ascending("sortByAttribute")
                .build();

        assertThat(query.getSortOrder(), is("ascending"));
    }

    @Test
    public void sorting_descending_works() {
        Query query = queryBuilder
                .descending("sortByAttribute")
                .build();

        assertThat(query.getSortOrder(), is("descending"));
    }

}
