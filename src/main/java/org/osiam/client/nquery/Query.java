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

/**
 * This class represents query for submission to the resource server.
 */
public class Query {
    private final String attributes;
    private final String filter;
    private final String sortBy;
    private final String sortOrder;
    private final long startIndex;
    private final int count;
    
    /**
     * Creates a new {@link Query} from the given Builder.
     * @param builder
     */
    Query(QueryBuilder builder) {
        attributes = builder.attributes;
        filter = builder.filter;
        sortBy = builder.sortBy;
        sortOrder = builder.sortOrder;
        startIndex = builder.startIndex;
        count = builder.count;
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
