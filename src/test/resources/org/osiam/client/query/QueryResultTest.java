package org.osiam.client.query;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Test;
import org.osiam.resources.scim.Group;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class QueryResultTest {

    final static private String PATH = "src/test/resources/__files/query_all_groups.json";
    private static final int EXPECTED_NUMBER_OF_GROUPS = 7;
    private String jsonString;

    private ObjectMapper mapper;

    private QueryResult<Group> result;

    @Before
    public void setUp() throws IOException {
        jsonString = FileUtils.readFileToString(new File(PATH));
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
        result = mapper.readValue(jsonString, new TypeReference<QueryResult<Group>>() {
        });
    }
}
