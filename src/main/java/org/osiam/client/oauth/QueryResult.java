package org.osiam.client.oauth;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.osiam.resources.scim.Group;

import java.util.Set;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class QueryResult {

    private Integer totalResults;
    private Integer itemsPerPage;
    private Integer startIndex;
    private String schemas;
    public Set<Group> Resources; //TODO can not be set if private. Now, not known why.

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

    public Set<Group> Resources() {
        return Resources;
    }
}
