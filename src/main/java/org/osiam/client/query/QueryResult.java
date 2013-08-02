package org.osiam.client.query;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.osiam.resources.scim.Group;

import java.util.Set;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class QueryResult {

    private Integer totalResults;
    private Integer itemsPerPage;
    private Integer startIndex;
    private String schemas;
    @JsonProperty("Resources")
    private Set<Group> resources;

    public Integer getTotalResults() {
        return totalResults;
    }

    public Integer getItemsPerPage() {
        return itemsPerPage;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public String getSchemas() {
        return schemas;
    }

    public Set<Group> getResources() {
        return resources;
    }
}
