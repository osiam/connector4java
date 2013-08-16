package org.osiam.client.query;
/*
 * for licensing see the file license.txt.
 */

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.osiam.resources.scim.CoreResource;

import java.util.Collections;
import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class QueryResult<T extends CoreResource> {

    private int totalResults;
    private int itemsPerPage;
    private int startIndex;
    private String schemas;
    @JsonProperty("Resources")
    private List<T> resources;

    public int getTotalResults() {
        return totalResults;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public String getSchemas() {
        return schemas;
    }

    public List<T> getResources() {
        return Collections.unmodifiableList(resources);
    }
}
