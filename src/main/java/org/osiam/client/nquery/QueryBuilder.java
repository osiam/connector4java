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
package org.osiam.client.nquery;

public class QueryBuilder {
    private static final int DEFAULT_START_INDEX = 1;
    private static final int DEFAULT_COUNT = 100;

    String attributes;
    String filter;
    String sortBy;
    String sortOrder;

    public long startIndex = DEFAULT_START_INDEX;
    public int count = DEFAULT_COUNT;

    /**
     * Creates a new {@link Query} using this builder's values. 
     * @return The query built by this {@link QueryBuilder}
     */
    public Query build() {
        return new Query(this);
    }

    /**
     * Sets the attributes of the resources to return as a comma-separated
     * list, e.g <code>userName, displayName</code>.
     * @see http://tools.ietf.org/html/draft-ietf-scim-api-04#section-3.7
     */
    public QueryBuilder attributes(String attributes) {
        this.attributes = attributes;
        return this;
    }

    /**
     * Sets the search filter string.
     * @see http://tools.ietf.org/html/draft-ietf-scim-api-04#section-3.2.2.2
     */
    public QueryBuilder filter(String filter) {
        this.filter = filter;
        return this;
    }

    /**
     * Sets the attribute to sort the returned resources by.
     * @see http://tools.ietf.org/html/draft-ietf-scim-api-04#section-3.2.2.3
     */
    public QueryBuilder sortBy(String sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    /**
     * Sets the sort order (ascending, descending) the sort the returned resources by.
     * @see http://tools.ietf.org/html/draft-ietf-scim-api-04#section-3.2.2.3
     */
    public QueryBuilder sortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    /**
     * Sets the index (1-based) of the first resource in the list of returned resources. 
     * @see http://tools.ietf.org/html/draft-ietf-scim-api-04#section-3.2.2.4
     */
    public QueryBuilder startIndex(long startIndex) {
        this.startIndex = startIndex;
        return this;
    }

    /**
     * Sets the number of returned resources per page.
     * @see http://tools.ietf.org/html/draft-ietf-scim-api-04#section-3.2.2.4
     */
    public QueryBuilder count(int count) {
        this.count = count;
        return this;
    }

    /**
     * Gets the attributes of the resources to return.
     */
    public String getAttributes() {
        return attributes;
    }

    /**
     * Gets the filter string.
     */
    public String getFilter() {
        return filter;
    }

    /**
     * Gets the attribute to sort the returned resources by.
     */
    public String getSortBy() {
        return sortBy;
    }

    /**
     * Gets the sort order (ascending, descending) of the returned resources.
     */
    public String getSortOrder() {
        return sortOrder;
    }

    /**
     * Gets the index (1-based) of the first resource in the list of returned resource.
     */
    public long getStartIndex() {
        return startIndex;
    }

    /**
     * Gets the number of returned resources per page.
     */
    public int getCount() {
        return count;
    }
    
}
