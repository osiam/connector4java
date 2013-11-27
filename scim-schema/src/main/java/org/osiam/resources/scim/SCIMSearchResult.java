/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.resources.scim;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class SCIMSearchResult<T> {

    private long totalResults;
    private long itemsPerPage;
    private long startIndex;
    private Set<String> schemas;
    private List<T> resources = new ArrayList<>();

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