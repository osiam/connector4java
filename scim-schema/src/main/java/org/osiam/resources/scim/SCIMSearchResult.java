package org.osiam.resources.scim;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class SCIMSearchResult<T> {

    private long totalResults;
    private long itemsPerPage;
    private long startIndex;
    private Set<String> schemas;
    private List<T> resources;

    public SCIMSearchResult() {}

    public SCIMSearchResult(List<T> resources, long totalResults, long itemsPerPage, long startIndex, String schema) {
        this.resources = resources;
        this.totalResults = totalResults;
        this.itemsPerPage = itemsPerPage;
        this.startIndex = startIndex;

        this.schemas = new HashSet<>();
        this.schemas.add(schema);
    }

    public SCIMSearchResult(List<T> resources, long totalResults, long itemsPerPage, long startIndex, Set<String> schemas) {
        this.resources = resources;
        this.totalResults = totalResults;
        this.itemsPerPage = itemsPerPage;
        this.startIndex = startIndex;
        this.schemas = schemas;
    }

    @JsonProperty("Resources")
    public List<T> getResources() {
        return resources;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public Set<String> getSchemas() {
        return schemas;
    }

    public long getItemsPerPage() {
        return itemsPerPage;
    }

    public long getStartIndex() {
        return startIndex;
    }
}