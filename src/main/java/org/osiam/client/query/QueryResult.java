package org.osiam.client.query;
/*
 * for licensing see the file license.txt.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.osiam.resources.scim.CoreResource;

/**
 * The Query result for the search for Users or Groups
 * 
 * @param <T> User.class or Group.class
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class QueryResult<T extends CoreResource> {

    private int totalResults;
    private int itemsPerPage;
    private int startIndex;
    private List<String> schemas;
    @JsonProperty("Resources")
    private List<T> resources = new ArrayList<>();

    /**
     * 
     * @return the total number of all found resources
     */
    public int getTotalResults() {
        return totalResults;
    }

    /**
     * 
     * @return the number of Items per Page
     */
    public int getItemsPerPage() {
        return itemsPerPage;
    }

    /**
     * 
     * @return the start index of the actual QueryResult
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * 
     * @return the schema of the actual Result
     */
    public List<String> getSchemas() {
        return schemas;
    }

    /**
     * 
     * @return a list of Users or Groups that have been found 
     */
    public List<T> getResources() {
        return Collections.unmodifiableList(resources);
    }
}
