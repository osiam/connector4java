package org.osiam.client.query;
/*
 * for licensing see the file license.txt.
 */

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.osiam.resources.scim.Group;

public class QueryResultTest {

    final static private String GROUP_PATH = "src/test/resources/__files/query_all_groups.json";
    final static private int EXPECTED_NUMBER_OF_GROUPS = 7;
    private String jsonGroupString;
    private ObjectMapper mapper;
    private QueryResult<Group> result;

    @Before
    public void setUp() throws IOException {
        jsonGroupString = FileUtils.readFileToString(new File(GROUP_PATH));
        mapper = new ObjectMapper();
    }

    @Test
    public void groups_query_result_can_be_deserialized() throws IOException {
        givenADeserializedGroupQueryResult();
        assertEquals(EXPECTED_NUMBER_OF_GROUPS, result.getTotalResults());
    }

    @Test
    public void group_query_result_returns_list_of_correct_size() throws IOException {
        givenADeserializedGroupQueryResult();
        assertEquals(EXPECTED_NUMBER_OF_GROUPS, result.getResources().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void group_query_result_is_immutable() throws IOException {
        givenADeserializedGroupQueryResult();
        result.getResources().add(new Group());
    }

    @Test
    public void group_query_result_items_per_page_is_as_expected() throws IOException {
        givenADeserializedGroupQueryResult();
        assertEquals(100, result.getItemsPerPage());
    }

    @Test
    public void group_query_index_is_at_correct_position() throws IOException {
        givenADeserializedGroupQueryResult();
        assertEquals(0, result.getStartIndex());
    }

    private void givenADeserializedGroupQueryResult() throws IOException {
        result = mapper.readValue(jsonGroupString, new TypeReference<QueryResult<Group>>() {
        });
    }
}
